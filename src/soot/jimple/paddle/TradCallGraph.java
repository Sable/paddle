/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003, 2004, 2005 Ondrej Lhotak
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
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.*;

/** Keeps track of call edges.
 * @author Ondrej Lhotak
 */
public class TradCallGraph extends AbsCallGraph
{ 
    private CallGraph cscg;
    private CallGraph cicg;
    TradCallGraph( Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcm_stmt_kind_tgtm ciout, Qsrcc_srcm_stmt_kind_tgtc_tgtm csout ) {
        super(in, ciout, csout);
        cscg = new CallGraph();
        cicg = new CallGraph();
    }
    public boolean update() {
        boolean ret = false;
        if( in != null ) {
            for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
                final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
                if( cscg.addEdge( new Edge(
                            MethodContext.v( t.srcm(), t.srcc() ),
                            t.stmt(),
                            MethodContext.v( t.tgtm(), t.tgtc() ),
                            t.kind() ) ) ) {
                    if(csout != null) 
                        csout.add( t.srcc(), t.srcm(), t.stmt(), t.kind(), t.tgtc(), t.tgtm() );
                    ret = true;
                }
                if( cicg.addEdge( new Edge(
                            t.srcm(), 
                            t.stmt(),
                            t.tgtm(),
                            t.kind() ) ) ) {
                    if(ciout != null)
                        ciout.add( t.srcm(), t.stmt(), t.kind(), t.tgtm() );
                    ret = true;
                }
            }
        }
        return ret;
    }
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf( Rctxt_method methods ) {
        Qsrcc_srcm_stmt_kind_tgtc_tgtm queue =
            new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("edgesOutOf");
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = queue.reader("edgesOutOf");
        for( Iterator tIt = methods.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            edgesOutOfHelper( MethodContext.v( t.method(), t.ctxt() ), queue );
        }
        return ret;
    }

    public Rsrcm_stmt_kind_tgtm edgesOutOf( Rmethod methods ) {
        Qsrcm_stmt_kind_tgtm queue =
            new Qsrcm_stmt_kind_tgtmTrad("edgesOutOf");
        Rsrcm_stmt_kind_tgtm ret = queue.reader("edgesOutOf");
        for( Iterator tIt = methods.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            edgesOutOfHelper( MethodContext.v( t.method(), t.ctxt() ), queue );
        }
        return ret;
    }

    public Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesOutOf( Context c, SootMethod method ) {
        Qsrcc_srcm_stmt_kind_tgtc_tgtm queue =
            new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("edgesOutOf");
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = queue.reader("edgesOutOf");
        edgesOutOfHelper( MethodContext.v(method, c), queue );
        return ret;
    }
    public Rsrcm_stmt_kind_tgtm edgesOutOf( SootMethod method ) {
        Qsrcm_stmt_kind_tgtm queue =
            new Qsrcm_stmt_kind_tgtmTrad("edgesOutOf");
        Rsrcm_stmt_kind_tgtm ret = queue.reader("edgesOutOf");
        edgesOutOfHelper( method, queue );
        return ret;
    }
    private void edgesOutOfHelper( MethodOrMethodContext method, Qsrcc_srcm_stmt_kind_tgtc_tgtm queue ) {
            edgesHelper( cscg.edgesOutOf( method ), queue );
    }
    private void edgesOutOfHelper( SootMethod method, Qsrcm_stmt_kind_tgtm queue ) {
            edgesHelper( cicg.edgesOutOf( method ), queue );
    }
    private void edgesHelper( Iterator it, Qsrcc_srcm_stmt_kind_tgtc_tgtm queue ) {
            while( it.hasNext() ) {
                Edge e = (Edge) it.next();

                queue.add( e.srcCtxt(), e.src(), e.srcUnit(), e.kind(),
                        e.tgtCtxt(), e.tgt() );
            }
    }
    private void edgesOutOfHelper( MethodOrMethodContext method, Qsrcm_stmt_kind_tgtm queue ) {
            edgesHelper( cicg.edgesOutOf( method ), queue );
    }
    private void edgesHelper( Iterator it, Qsrcm_stmt_kind_tgtm queue ) {
            while( it.hasNext() ) {
                Edge e = (Edge) it.next();

                queue.add( e.src(), e.srcUnit(), e.kind(), e.tgt() );
            }
    }
    public Rsrcc_srcm_stmt_kind_tgtc_tgtm csEdges() {
        Qsrcc_srcm_stmt_kind_tgtc_tgtm queue =
            new Qsrcc_srcm_stmt_kind_tgtc_tgtmTrad("edges");
        Rsrcc_srcm_stmt_kind_tgtc_tgtm ret = queue.reader("edges");
        edgesHelper( cscg.listener(), queue );
        return ret;
    }
    public Rsrcm_stmt_kind_tgtm ciEdges() {
        Qsrcm_stmt_kind_tgtm queue =
            new Qsrcm_stmt_kind_tgtmTrad("edges");
        Rsrcm_stmt_kind_tgtm ret = queue.reader("edges");
        edgesHelper( cicg.listener(), queue );
        return ret;
    }
    public int csSize() {
        return cscg.size();
    }
    public int ciSize() {
        return cicg.size();
    }
}

