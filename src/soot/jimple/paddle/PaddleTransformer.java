/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003, 2004, 2005 Ondrej Lhotak
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
import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.options.PaddleOptions;
import soot.options.CGOptions;
import soot.tagkit.*;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.pointer.util.*;
import java.io.*;

/** Main entry point for Paddle.
 * @author Ondrej Lhotak
 */
public class PaddleTransformer extends SceneTransformer
{ 
    public static PaddleTransformer v() { return (PaddleTransformer) G.v().soot_jimple_paddle_PaddleHook().paddleTransformer(); }

    protected void internalTransform( String phaseName, Map options )
    {
        PaddleOptions opts = new PaddleOptions( options );
        setup(opts);
        solve(opts);
    }

    public void setup( PaddleOptions opts ) {
        CGOptions cgoptions = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        switch( cgoptions.context() ) {
            case CGOptions.context_insens:
                Scene.v().setContextNumberer( new MapNumberer() );
                PaddleNumberers.v().contextDomain = KindDomain.v();
                break;
            case CGOptions.context_1cfa:
                Scene.v().setContextNumberer( Scene.v().getUnitNumberer() );
                PaddleNumberers.v().contextDomain = StmtDomain.v();
                break;
            case CGOptions.context_objsens:
                Scene.v().setContextNumberer( PaddleNumberers.v().allocNodeNumberer() );
                PaddleNumberers.v().contextDomain = ObjDomain.v();
                break;
            case CGOptions.context_kcfa:
                Scene.v().setContextNumberer( new ContextStringNumberer(StmtDomain.v(), Scene.v().getUnitNumberer(), cgoptions.k()) );
                PaddleNumberers.v().contextDomain = null;
                break;
            case CGOptions.context_kobjsens:
                Scene.v().setContextNumberer( new ContextStringNumberer(ObjDomain.v(), PaddleNumberers.v().allocNodeNumberer(), cgoptions.k()) );
                PaddleNumberers.v().contextDomain = null;
                break;
            default:
                throw new RuntimeException( "Unhandled kind of context" );
        }

        PaddleScene.v().setup( opts );
    }

    public void solve(PaddleOptions opts) {
        /*
        Iterator rcoutIt = null;
        if( opts.add_tags() ) {
            rcoutIt = PaddleScene.v().rcout.reader("tags").iterator();
        }
        */

        if( opts.pre_jimplify() ) preJimplify();

        Date startSolve = new Date();

        PaddleScene.v().solve();

        Date endSolve = new Date();
        
        if( opts.verbose() ) {
            reportTime( "Propagation", startSolve, endSolve );
        }

        Results.v().makeStandardSootResults();

        if( opts.set_mass() ) findSetMass();

        if( opts.context_counts() ) ContextCountPrinter.printContextCounts();
    }

    /*
    private void addContext( Host h, Context c, Map contextToTag ) {
        Tag t = (Tag) contextToTag.get(c);
        if( t == null ) {
            t = new StringTag( c == null ? "null context" : "context "+c.toString() );
            contextToTag.put(c, t);
        }
        h.addTag( t );
    }
    private void addTag( Host h, Node n, Map nodeToTag ) {
        Tag t = (Tag) nodeToTag.get(n);
        if( t == null ) {
            t = new StringTag( n.toString() );
            nodeToTag.put(n, t);
        }
        h.addTag( t );
    }
    private void addTags(Iterator rcoutIt) {
        final NodeManager nm = PaddleScene.v().nodeManager();
        final AbsPAG pag = PaddleScene.v().pag;

        final Map nodeToTag = new HashMap();
        final Map contextToTag = new HashMap();

        while( rcoutIt.hasNext() ) {
            final Rctxt_method.Tuple momc = (Rctxt_method.Tuple) rcoutIt.next();
            SootMethod m = momc.method();
            if( !m.isConcrete() ) continue;
            if( !m.hasActiveBody() ) continue;
            for( Iterator sIt = m.getActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
                final Stmt s = (Stmt) sIt.next();
                if( s instanceof DefinitionStmt ) {
                    Value lhs = ((DefinitionStmt) s).getLeftOp();
                    VarNode v = null;
                    if( lhs instanceof Local ) {
                        v = nm.findLocalVarNode( (Local) lhs );
                    } else if( lhs instanceof FieldRef ) {
                        v = nm.findGlobalVarNode( ((FieldRef) lhs).getField() );
                    }
                    if( v != null ) {
                        final boolean[] addedContext = new boolean[1];
                        addedContext[0] = false;
                        ContextVarNode cvn = ContextVarNode.make(momc.ctxt(), v);
                        PointsToSetReadOnly p2set = 
                            PaddleScene.v().p2sets.get(cvn);
                        p2set.forall( new P2SetVisitor() {
                        public final void visit( ContextAllocNode n ) {
                            if( !addedContext[0] ) {
                                addContext(s, momc.ctxt(), contextToTag);
                                addedContext[0] = true;
                            }
                            addTag( s, n, nodeToTag );
                        }} );
                        Iterator it;
                        it = pag.simpleInvLookup(cvn);
                        while( it.hasNext() ) {
                            if( !addedContext[0] ) {
                                addContext(s, momc.ctxt(), contextToTag);
                                addedContext[0] = true;
                            }
                            addTag( s, (Node) it.next(), nodeToTag );
                        }
                        it = pag.allocInvLookup(cvn);
                        while( it.hasNext() ) {
                            if( !addedContext[0] ) {
                                addContext(s, momc.ctxt(), contextToTag);
                                addedContext[0] = true;
                            }
                            addTag( s, (Node) it.next(), nodeToTag );
                        }
                        it = pag.loadInvLookup(cvn);
                        while( it.hasNext() ) {
                            if( !addedContext[0] ) {
                                addContext(s, momc.ctxt(), contextToTag);
                                addedContext[0] = true;
                            }
                            addTag( s, (Node) it.next(), nodeToTag );
                        }
                    }
                }
            }
        }
    }
    */
    private void preJimplify() {
        boolean change = true;
        while( change ) {
            change = false;
            for( Iterator cIt = new ArrayList(Scene.v().getClasses()).iterator(); cIt.hasNext(); ) {
                final SootClass c = (SootClass) cIt.next();
                for( Iterator mIt = c.methodIterator(); mIt.hasNext(); ) {
                    final SootMethod m = (SootMethod) mIt.next();
                    if( !m.isConcrete() ) continue;
                    if( m.isNative() ) continue;
                    if( m.isPhantom() ) continue;
                    if( !m.hasActiveBody() ) {
                        change = true;
                        m.retrieveActiveBody();
                    }
                }
            }
        }
    }
    private static void reportTime( String desc, Date start, Date end ) {
        long time = end.getTime()-start.getTime();
        G.v().out.println( "[Paddle] "+desc+" in "+time/1000+"."+(time/100)%10+" seconds." );
    }
    protected void findSetMass() {
        int mass = 0;
        int varMass = 0;
        int adfs = 0;
        int scalars = 0;

        for( Iterator vIt = PaddleNumberers.v().contextVarNodeNumberer().iterator(); vIt.hasNext(); ) {

            final ContextVarNode v = (ContextVarNode) vIt.next();
            scalars++;
            PointsToSetReadOnly set = Results.v().p2sets().get(v);
            if( set != null ) mass += set.size();
            if( set != null ) varMass += set.size();
        }
        for( Iterator anIt = PaddleNumberers.v().contextAllocNodeNumberer().iterator(); anIt.hasNext(); ) {
            final ContextAllocNode an = (ContextAllocNode) anIt.next();
            Iterator it = an.fields();
            while( it.hasNext() ) {
                ContextAllocDotField adf = (ContextAllocDotField) it.next();
                PointsToSetReadOnly set = Results.v().p2sets().get(adf);
                if( set != null ) mass += set.size();
                if( set != null && set.size() > 0 ) {
                    adfs++;
                }
            }
        }
        G.v().out.println( "Set mass: " + mass );
        G.v().out.println( "Variable mass: " + varMass );
        G.v().out.println( "Scalars: "+scalars );
        G.v().out.println( "adfs: "+adfs );
    }
    protected static void doGC() {
        // Do 5 times because the garbage collector doesn't seem to always collect
        // everything on the first try.
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
    }

    protected void addTag( Host h, Node n, Map nodeToTag, Tag unknown ) {
        if( nodeToTag.containsKey( n ) ) h.addTag( (Tag) nodeToTag.get(n) );
        else h.addTag( unknown );
    }
}


