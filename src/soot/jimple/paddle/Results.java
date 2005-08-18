/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004, 2005 Ondrej Lhotak
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
import soot.options.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import jedd.*;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/** This class collects the analysis results computed by Paddle.
 * @author Ondrej Lhotak
 */
public class Results
{ 
    public Results( PaddleSingletons.Global g ) {}
    public static Results v() { return PaddleG.v().soot_jimple_paddle_Results(); }

    AbsCallGraph cg;
    AbsReachableMethods rc;
    AbsP2Sets pt;
    NodeFactory gnf;

    public AbsCallGraph callGraph() {
        return cg;
    }
    public AbsReachableMethods reachableMethods() {
        return rc;
    }
    public AbsP2Sets p2sets() {
        return pt;
    }
    public NodeFactory nodeFactory() {
        return gnf;
    }

    /** Convert Paddle-generated interprocedural results into the
     * standard formats expected by other Soot analyses.
     */
    public void makeStandardSootResults() {
        CallGraph newCallGraph = new CallGraph();
        for( Iterator tIt = cg.ciEdges().iterator(); tIt.hasNext(); ) {
            final Rsrcm_stmt_kind_tgtm.Tuple t = (Rsrcm_stmt_kind_tgtm.Tuple) tIt.next();
            Edge e = new Edge( t.srcm(), t.stmt(), t.tgtm(), t.kind() );
            newCallGraph.addEdge(e);
        }
        Scene.v().setCallGraph( newCallGraph );

        Set methods = new HashSet();
        for( Iterator tIt = rc.methods().iterator(); tIt.hasNext(); ) {
            final Rmethod.Tuple t = (Rmethod.Tuple) tIt.next();
            methods.add( t.method() );
        }
        ReachableMethods rm = new ReachableMethods(newCallGraph, methods);
        Scene.v().setReachableMethods(rm);

        if(pt != null) {
            if(pt instanceof BDDP2Sets) {
                Scene.v().setPointsToAnalysis(new BDDPointsToAnalysis(pt));
            } else {
                Scene.v().setPointsToAnalysis(new TradPointsToAnalysis(pt));
            }
        }

        Scene.v().setContextSensitiveCallGraph(new PaddleContextSensitiveCallGraph(cg));
    }
    AbsNodeInfo nodeInfo;
}

