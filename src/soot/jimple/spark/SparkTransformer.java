/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark;
import soot.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.solver.*;
import soot.jimple.spark.sets.*;
import soot.jimple.toolkits.invoke.InvokeGraph;
import soot.jimple.toolkits.invoke.InvokeGraphBuilder;
import soot.jimple.*;
import java.util.*;

/** Main entry point for Spark.
 * @author Ondrej Lhotak
 */
public class SparkTransformer extends SceneTransformer
{ 
    private static SparkTransformer instance = 
	new SparkTransformer();
    private SparkTransformer() {}
    private InvokeGraph ig;

    public static SparkTransformer v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions() +
	SparkOptions.getDeclaredOptions(); }

    public String getDefaultOptions() { return SparkOptions.getDefaultOptions(); }

    /*
    private Node getNodeFor( soot.jimple.toolkits.pointer.Node klojNode, PAG pag ) {
        if( klojNode.getType() instanceof NullType ) {
            System.out.println( "Null node: "+klojNode );
        }
        if( klojNode.getType() instanceof AnyType ) {
            System.out.println( "Anytype node: "+klojNode );
        }
        if( klojNode instanceof soot.jimple.toolkits.pointer.VarNode ) {
            soot.jimple.toolkits.pointer.VarNode vn =
                (soot.jimple.toolkits.pointer.VarNode) klojNode;
            Node ret = pag.makeVarNode( vn.getVal(), vn.getType(), vn.m );
            if( vn.getVal() instanceof Local ) {
                pag.reachingObjects( vn.m, null, (Local) vn.getVal() );
            }
            return ret;
        } else if( klojNode instanceof soot.jimple.toolkits.pointer.FieldRefNode ) {
            soot.jimple.toolkits.pointer.FieldRefNode frn =
                (soot.jimple.toolkits.pointer.FieldRefNode) klojNode;
            soot.jimple.toolkits.pointer.VarNode vn = frn.getBase();
            Object field = frn.getField();
            if( field instanceof SparkField ) {
            } else if( field instanceof Integer ) {
                if( field.equals( PointsToAnalysis.ARRAY_ELEMENTS_NODE ) ) {
                    field = ArrayElement.v();
                }
            } else {
                throw new RuntimeException( field.toString() );
            }
            return pag.makeFieldRefNode( vn.getVal(), vn.getType(), (SparkField) field, frn.m );
        } else if( klojNode instanceof soot.jimple.toolkits.pointer.AllocNode ) {
            soot.jimple.toolkits.pointer.AllocNode an =
                (soot.jimple.toolkits.pointer.AllocNode) klojNode;
            return pag.makeAllocNode( an, an.getType() );
        } else throw new RuntimeException( klojNode.toString() );
    }
    private PAG buildFromKloj( SparkOptions opts ) {
        soot.jimple.toolkits.pointer.NodePPG ppg = 
            new soot.jimple.toolkits.pointer.NodePPG( ig );
        ppg.parmsAsFields = opts.parmsAsFields();
        ppg.returnsAsFields = opts.returnsAsFields();
        ppg.collapseObjects = false;
        ppg.collapseObjects = false;
        ppg.typesForSites = false;
        ppg.mergeStringbuffer = opts.mergeStringBuffer();
        ppg.simulateNatives = opts.simulateNatives();
        if( opts.simulateNatives() ) {
            NativeHelper.register( new
                    soot.jimple.toolkits.pointer.kloj.KlojNativeHelper( ppg ) );
        }
        ppg.build();
        //ppg.collapseEBBs( Scene.v().getOrMakeFastHierarchy() );
        System.out.println( "Now converting from Kloj to PAG" );
        PAG pag = new PAG( opts );
        MultiMap[] edges = { ppg.getSimple(), ppg.getLoads(),
            ppg.getStores(), ppg.getNews() };
        for( int i = 0; i < edges.length; i++ ) {
            MultiMap m = edges[i];
            int size = 0;
            for( Iterator it = m.keySet().iterator(); it.hasNext(); ) {
                soot.jimple.toolkits.pointer.Node src =
                    (soot.jimple.toolkits.pointer.Node) it.next();
                for( Iterator it2 = m.get( src ).iterator(); it2.hasNext(); ) {
                    soot.jimple.toolkits.pointer.Node dest =
                        (soot.jimple.toolkits.pointer.Node) it2.next();
                    if( dest instanceof soot.jimple.toolkits.pointer.FieldRefNode ) {
                        soot.jimple.toolkits.pointer.FieldRefNode frn = 
                            (soot.jimple.toolkits.pointer.FieldRefNode) dest;
                        if( frn.getField().equals( soot.jimple.toolkits.pointer.PointerAnalysis.ARRAY_ELEMENTS_NODE ) )
                        {
                            Type t = frn.getBase().getType();
                            if( t instanceof ArrayType ) {
                                ArrayType at = (ArrayType) t;
                                if(!( at.getElementType() instanceof RefLikeType) )
                                    continue;
                            }
                        }
                    }
                    pag.addEdge( getNodeFor( src, pag ), getNodeFor( dest, pag ) );
                    size++;
                }
            }
            System.out.println( size );
        }
        Set[] edges2 = { pag.simpleSources(), pag.loadSources(),
            pag.storeSources(), pag.allocSources() };
        for( int i = 0; i < edges2.length; i++ ) {
            int size=0;
            int size2=0;
            for( Iterator it = edges2[i].iterator(); it.hasNext(); ) {
                int s=0;
                switch(i) {
                    case 0: s = pag.simpleLookup( (VarNode) it.next() ).length;
                    break;
                    case 1: s = pag.loadLookup( (FieldRefNode) it.next() ).length;
                    break;
                    case 2: s = pag.storeLookup( (VarNode) it.next() ).length;
                    break;
                    case 3: s = pag.allocLookup( (AllocNode) it.next() ).length;
                    break;
                }
                size += s;
                size2 += s*s;
            }
            System.out.println( "Size: "+size+" Size2: "+size2 );
        }
        for( Iterator anIt = pag.allocSources().iterator(); anIt.hasNext(); ) {
            final AllocNode an = (AllocNode) anIt.next();
            System.out.println( "Allocation site: "+an );
            Node[] nodes = pag.allocLookup( an );
            for( int i = 0; i < nodes.length; i++ ) {
                System.out.println( " "+nodes[i] );
            }
        }
        System.out.println( "Done converting from Kloj to PAG" );
        return pag;
    }
*/
    private static void reportTime( String desc, Date start, Date end ) {
        System.out.println( "[Spark] "+desc+" in "+(end.getTime()-start.getTime())/100+" deciseconds." );
    }
    private static void doGC() {
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
    }
    protected void internalTransform( String phaseName, Map options )
    {
        internalTransformGuts( this, phaseName, options );
        BitPointsToSet.delete();
        HybridPointsToSet.delete();
        Parm.delete();
        System.out.println( "[Spark] Dropping everything." );
        doGC();
    }
    private static void internalTransformGuts( SparkTransformer ths,
            String phaseName, Map options )
    {
        SparkOptions opts = new SparkOptions( options );

        // Build invoke graph
        Date startIg = new Date();
        InvokeGraphBuilder.v().transform( phaseName + ".igb" );
        ths.ig = Scene.v().getActiveInvokeGraph();
        Date endIg = new Date();
        reportTime( "Invoke Graph", startIg, endIg );

        // Build pointer assignment graph
        Builder b = new ContextInsensitiveBuilder();
        b.preJimplify();
        doGC();
        Date startBuild = new Date();
        final PAG pag = b.build( opts );
        Date endBuild = new Date();
        reportTime( "Pointer Assignment Graph", startBuild, endBuild );
        doGC();

        // Build type masks
        Date startTM = new Date();
        pag.getTypeManager().makeTypeMask( pag );
        Date endTM = new Date();
        reportTime( "Type masks", startTM, endTM );
        doGC();

        // Simplify pag
        Date startSimplify = new Date();
        if( opts.simplifySCCs() ) {
            new SCCCollapser( pag, opts.ignoreTypesForSCCs() ).collapse();
        }
        if( opts.simplifyOffline() ) new EBBCollapser( pag ).collapse();
        if( opts.simplifySCCs() || opts.simplifyOffline() ) {
            pag.cleanUpMerges();
        }
        Date endSimplify = new Date();
        reportTime( "Pointer Graph simplified", startSimplify, endSimplify );

        // Dump pag
        PAGDumper dumper = null;
        if( opts.dumpPAG() || opts.dumpSolution() ) {
            dumper = new PAGDumper( pag );
        }
        if( opts.dumpPAG() ) dumper.dump();

        // Propagate
        Date startProp = new Date();
        final Propagator[] propagator = new Propagator[1];
        opts.propagator( new SparkOptions.Switch_propagator() {
            public void case_iter() {
                propagator[0] = new PropIter( pag );
            }
            public void case_worklist() {
                propagator[0] = new PropWorklist( pag );
            }
            public void case_merge() {
                propagator[0] = new PropMerge( pag );
            }
            public void case_alias() {
                propagator[0] = new PropAlias( pag );
            }
            public void case_none() {
            }
        } );
        if( propagator[0] != null ) propagator[0].propagate();
        Date endProp = new Date();
        reportTime( "Propagation", startProp, endProp );
        doGC();
        reportTime( "Solution found", startSimplify, endProp );

        /*
        if( propagator[0] instanceof PropMerge ) {
            new MergeChecker( pag ).check();
        } else if( propagator[0] != null ) {
            new Checker( pag ).check();
        }
        findSetMass( pag );
        pag.dumpNumbersOfEdges();
        */
        ths.findSetMass( pag );
        if( opts.dumpAnswer() ) new ReachingTypeDumper( pag ).dump();
        if( opts.dumpSolution() ) dumper.dumpPointsToSets();
        if( opts.dumpHTML() ) new PAG2HTML( pag ).dump();
        //Scene.v().setActivePointsToAnalysis( pag );
    }
    protected void findSetMass( PAG pag ) {
        int mass = 0;
        int varMass = 0;
        int adfs = 0;
        int scalars = 0;
        for( Iterator vIt = pag.allVarNodes().iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
                scalars++;
            PointsToSetInternal set = v.getP2Set();
            if( set != null ) mass += set.size();
            if( set != null ) varMass += set.size();
            if( set != null && set.size() > 0 ) {
                //System.out.println( "V "+v.getValue()+" "+set.size() );
            //    System.out.println( ""+v.getValue()+" "+v.getMethod()+" "+set.size() );
            }
        }
        for( Iterator anIt = pag.allocSources().iterator(); anIt.hasNext(); ) {
            final AllocNode an = (AllocNode) anIt.next();
            for( Iterator adfIt = an.getFields().iterator(); adfIt.hasNext(); ) {
                final AllocDotField adf = (AllocDotField) adfIt.next();
                PointsToSetInternal set = adf.getP2Set();
                if( set != null ) mass += set.size();
                if( set != null && set.size() > 0 ) {
                    adfs++;
            //        System.out.println( ""+adf.getBase().getNewExpr()+"."+adf.getField()+" "+set.size() );
                }
            }
        }
        System.out.println( "Set mass: " + mass );
        System.out.println( "Variable mass: " + varMass );
        System.out.println( "Scalars: "+scalars );
        System.out.println( "adfs: "+adfs );
        // Compute points-to set sizes of dereference sites BEFORE
        // trimming sets by declared type
        int[] deRefCounts = new int[30001];
        for( Iterator vIt = pag.getDereferences().iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
            PointsToSetInternal set = v.getP2Set();
            int size = 0;
            if( set != null ) size = set.size();
            deRefCounts[size]++;
        }
        int total = 0;
        for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
        System.out.println( "Dereference counts BEFORE trimming (total = "+total+"):" );
        for( int i=0; i < deRefCounts.length; i++ ) {
            if( deRefCounts[i] > 0 ) {
                System.out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
            }
        }
        // Compute points-to set sizes of dereference sites AFTER
        // trimming sets by declared type
        if( pag.getTypeManager().getFastHierarchy() == null ) {
            pag.getTypeManager().clearTypeMask();
            pag.getTypeManager().setFastHierarchy( Scene.v().getOrMakeFastHierarchy() );
            pag.getTypeManager().makeTypeMask( pag );
            deRefCounts = new int[30001];
            for( Iterator vIt = pag.getDereferences().iterator(); vIt.hasNext(); ) {
                final VarNode v = (VarNode) vIt.next();
                PointsToSetInternal set = 
                    pag.getSetFactory().newSet( v.getType(), pag );
                int size = 0;
                if( set != null ) {
                    v.getP2Set().setType( null );
                    v.getP2Set().getNewSet().setType( null );
                    v.getP2Set().getOldSet().setType( null );
                    set.addAll( v.getP2Set(), null );
                    size = set.size();
                }
                deRefCounts[size]++;
            }
            total = 0;
            for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
            System.out.println( "Dereference counts AFTER trimming (total = "+total+"):" );
            for( int i=0; i < deRefCounts.length; i++ ) {
                if( deRefCounts[i] > 0 ) {
                    System.out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
                }
            }
        }
    }
}

