/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003, 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.paddle;
import soot.jimple.paddle.queue.*;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.options.PaddleOptions;

/** Propagates points-to sets along pointer assignment graph using a worklist.
 * @author Ondrej Lhotak
 */

public final class PropWorklist extends AbsPropagator {
    protected Heap varNodeWorkList = new Heap(new Heap.Keys() {
        public int key(Object o) {
            ContextVarNode cvn = (ContextVarNode) o;
            return cvn.finishingNumber();
        }
    });
    private NodeManager nm = PaddleScene.v().nodeManager();

    public PropWorklist( Rsrcc_src_dstc_dst simple,
            Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc,
            Qvarc_var_objc_obj propout,
            AbsPAG pag ) {
        super( simple, load, store, alloc, propout, pag );
    }
    private AbsP2Sets p2sets;
    private boolean newEdges() {
        boolean ret = false;
        for( Iterator tIt = newSimple.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_dstc_dst.Tuple t = (Rsrcc_src_dstc_dst.Tuple) tIt.next();
            if( p2sets.make(t.dstc(), t.dst()).addAll( p2sets.get(t.srcc(), t.src()), null ) ) {
                varNodeWorkList.add( ContextVarNode.make(t.dstc(), t.dst()) );
                ret = true;
            }
        }
        for( Iterator tIt = newAlloc.iterator(); tIt.hasNext(); ) {
            final Robjc_obj_varc_var.Tuple t = (Robjc_obj_varc_var.Tuple) tIt.next();
            if( p2sets.make(t.varc(), t.var()).add( ContextAllocNode.make(t.objc(), t.obj()) ) ) {
                varNodeWorkList.add( ContextVarNode.make(t.varc(), t.var() ) );
                ret = true;
            }
        }
        for( Iterator tIt = newStore.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_dstc_dst_fld.Tuple t = (Rsrcc_src_dstc_dst_fld.Tuple) tIt.next();
            PointsToSetReadOnly baseSet = p2sets.get(t.dstc(), t.dst());
            final PointsToSetReadOnly p2set = p2sets.get(t.srcc(), t.src());
            if( baseSet.forall( new P2SetVisitor() {
            public final void visit( ContextAllocNode n ) {
                ContextAllocNode can = (ContextAllocNode) n;
                if( p2sets.make(ContextAllocDotField.make(can, t.fld()))
                        .addAll(p2set, null)) {
                    returnValue = true;
                }
            }} ) ) ret = true;
        }
        for( Iterator tIt = newLoad.iterator(); tIt.hasNext(); ) {
            final Rsrcc_src_fld_dstc_dst.Tuple t = (Rsrcc_src_fld_dstc_dst.Tuple) tIt.next();
            PointsToSetReadOnly baseSet = p2sets.get(t.srcc(), t.src());
            if( baseSet.forall( new P2SetVisitor() {
            public final void visit( ContextAllocNode n ) {
                ContextAllocNode can = (ContextAllocNode) n;
                PointsToSetReadOnly p2set = p2sets.get(
                    ContextAllocDotField.make(can, t.fld()));
                if( p2sets.make(t.dstc(), t.dst()).addAll(p2set, null)) {
                    varNodeWorkList.add(ContextVarNode.make(t.dstc(), t.dst()));
                    returnValue = true;
                }
            }} ) ) ret = true;
        }
        return ret;
    }
    public final boolean fieldUpdate() {
        boolean ret = false;
        if( PaddleScene.v().options().verbose() ) {
            G.v().out.println( "Now handling field references" );
        }
        for( Iterator srcIt = pag.storeSources(); srcIt.hasNext(); ) {
            final ContextVarNode src = (ContextVarNode) srcIt.next();
            for( Iterator targetIt = pag.storeLookup(src); targetIt.hasNext(); ) {
                final ContextFieldRefNode target = (ContextFieldRefNode) targetIt.next();
                if( p2sets.make(target.base()).forall( new P2SetVisitor() {
                public final void visit( ContextAllocNode n ) {
                        ContextAllocDotField nDotF = ContextAllocDotField.make( 
                            (ContextAllocNode) n, target.field() );
                        if( p2sets.make(nDotF).addAll( p2sets.get(src), null ) )
                            returnValue = true;
                    }
                } ) ) ret = true;
            }
        }
        HashSet edgesToPropagate = new HashSet();
        for( Iterator it = pag.loadSources(); it.hasNext(); ) {
            if( handleContextFieldRefNode( (ContextFieldRefNode) it.next(), edgesToPropagate ) ) ret = true;
        }
        HashSet nodesToFlush = new HashSet();
        for( Iterator pairIt = edgesToPropagate.iterator(); pairIt.hasNext(); ) {
            final Object[] pair = (Object[]) pairIt.next();
            PointsToSetInternal nDotF = (PointsToSetInternal) pair[0];
            PointsToSetReadOnly newP2Set = nDotF.getNewSet();
            ContextVarNode loadTarget = (ContextVarNode) pair[1];
            if( p2sets.make(loadTarget).addAll( newP2Set, null ) ) {
                varNodeWorkList.add( loadTarget );
                ret = true;
            }
            nodesToFlush.add( nDotF );
        }
        for( Iterator nDotFIt = nodesToFlush.iterator(); nDotFIt.hasNext(); ) {
            final PointsToSetInternal nDotF = (PointsToSetInternal) nDotFIt.next();
            nDotF.flushNew();
        }
        return ret;
    }
    /** Actually does the propagation. */
    public final boolean update() {
        boolean ret = false;
        //if( !PaddleScene.v().depMan.worklistEmpty() ) return true;
        p2sets = PaddleScene.v().p2sets;

        if( newEdges() ) ret = true;

        // have to reheapify the worklist because the sort may change the
        // node numbers
        new TopoSorter( pag, false ).sort();
        varNodeWorkList.heapify();

        if( PaddleScene.v().options().verbose() ) {
            G.v().out.println( "Worklist has "+varNodeWorkList.size()+
                    " nodes." );
        }
        while( !varNodeWorkList.isEmpty() ) {
            ContextVarNode src = (ContextVarNode) varNodeWorkList.removeMin();
            if( handleContextVarNode( src ) ) ret = true;
        }
        return ret;
    }

    /* End of public methods. */
    /* End of package methods. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleContextAllocNode( ContextAllocNode src ) {
	boolean ret = false;
        for( Iterator targetIt = pag.allocLookup(src); targetIt.hasNext(); ) {
            final ContextVarNode target = (ContextVarNode) targetIt.next();
	    if( p2sets.make(target).add( src ) ) {
                varNodeWorkList.add( target );
                ret = true;
            }
	}
	return ret;
    }
    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleContextVarNode( final ContextVarNode src ) {
	boolean ret = false;

        
	final PointsToSetReadOnly newP2Set = p2sets.get(src).getNewSet();
	if( newP2Set.isEmpty() ) {
            return false;
        }
	p2sets.make(src).flushNew();

        newP2Set.forall( new P2SetVisitor() {

        public final void visit( ContextAllocNode n ) {
        	ContextAllocNode can = (ContextAllocNode) n;
            ptout.add( src.ctxt(), src.var(), can.ctxt(), can.obj() );
        }} );

        for( Iterator simpleTargetIt = pag.simpleLookup(src); simpleTargetIt.hasNext(); ) {

            final ContextVarNode simpleTarget = (ContextVarNode) simpleTargetIt.next();
	    if( p2sets.make(simpleTarget).addAll( newP2Set, null ) ) {
                varNodeWorkList.add( simpleTarget );
                ret = true;
            }
	}

        for( Iterator storeTargetIt = pag.storeLookup(src); storeTargetIt.hasNext(); ) {

            final ContextFieldRefNode storeTarget = (ContextFieldRefNode) storeTargetIt.next();
            final PaddleField f = storeTarget.field();
            ret = p2sets.get(storeTarget.base()).forall( new P2SetVisitor() {
            public final void visit( ContextAllocNode n ) {
                    ContextAllocDotField nDotF = ContextAllocDotField.make( 
                        (ContextAllocNode) n, f );
                    if( p2sets.make(nDotF).addAll( newP2Set, null ) ) {
                        returnValue = true;
                    }
		}
	    } ) | ret;
        }

        final HashSet storesToPropagate = new HashSet();
        final HashSet loadsToPropagate = new HashSet();
	for( Iterator frIt = src.fields(); frIt.hasNext(); ) {
	    final ContextFieldRefNode fr = (ContextFieldRefNode) frIt.next();
	    final PaddleField field = fr.field();
            for( Iterator storeSourceIt = pag.storeInvLookup(fr); storeSourceIt.hasNext(); ) {
                final ContextVarNode storeSource = (ContextVarNode) storeSourceIt.next();
                newP2Set.forall( new P2SetVisitor() {
                public final void visit( ContextAllocNode n ) {
                        ContextAllocDotField nDotF = ContextAllocDotField.make(
                            (ContextAllocNode) n, field );
                        Node[] pair = { storeSource, nDotF };
                        storesToPropagate.add( pair );
                    }
                } );
            }

            for( Iterator loadTargetIt = pag.loadLookup(fr); loadTargetIt.hasNext(); ) {

                final ContextVarNode loadTarget = (ContextVarNode) loadTargetIt.next();
                newP2Set.forall( new P2SetVisitor() {
                public final void visit( ContextAllocNode n ) {
                        ContextAllocDotField nDotF = ContextAllocDotField.get(
                            (ContextAllocNode) n, field );
                        if( nDotF != null ) {
                            Node[] pair = { nDotF, loadTarget };
                            loadsToPropagate.add( pair );
                        }
                    }
                } );
            }
	}
        for( Iterator pIt = storesToPropagate.iterator(); pIt.hasNext(); ) {
            final Node[] p = (Node[]) pIt.next();
            ContextVarNode storeSource = (ContextVarNode) p[0];
            ContextAllocDotField nDotF = (ContextAllocDotField) p[1];
            if( p2sets.make(nDotF).addAll( p2sets.get(storeSource), null ) ) {
                ret = true;
            }
        }
        for( Iterator pIt = loadsToPropagate.iterator(); pIt.hasNext(); ) {
            final Node[] p = (Node[]) pIt.next();
            ContextAllocDotField nDotF = (ContextAllocDotField) p[0];
            ContextVarNode loadTarget = (ContextVarNode) p[1];
            if( p2sets.make(loadTarget).
                addAll( p2sets.get(nDotF), null ) ) {
                varNodeWorkList.add( loadTarget );
                ret = true;
            }
        }
	return ret;
    }

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleContextFieldRefNode( final ContextFieldRefNode src, 
            final HashSet edgesToPropagate ) {
        boolean ret = false;
        final PaddleField field = src.field();

        for( Iterator loadTargetIt = pag.loadLookup(src); loadTargetIt.hasNext(); ) {

            final ContextVarNode loadTarget = (ContextVarNode) loadTargetIt.next();
            if( p2sets.get(src.base()).forall( new P2SetVisitor() {
            public final void visit( ContextAllocNode n ) {
                    ContextAllocDotField nDotF = ContextAllocDotField.get( 
                        (ContextAllocNode) n, field );
                    if( nDotF != null ) {
                        PointsToSetReadOnly p2Set = p2sets.get(nDotF);
                        if( !p2Set.getNewSet().isEmpty() ) {
                            Object[] pair = { p2Set, loadTarget };
                            edgesToPropagate.add( pair );
                            returnValue = true;
                        }
                    }
                }
            } ) ) ret = true;
        }
        return ret;
    }
}



