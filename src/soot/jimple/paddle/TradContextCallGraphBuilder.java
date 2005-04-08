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
import java.util.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/** Converts context-insensitive call edges into context-sensitive ones
 * for each reachable method context.
 * @author Ondrej Lhotak
 */
public class TradContextCallGraphBuilder extends AbsContextCallGraphBuilder 
{ 
    TradContextCallGraphBuilder( Rctxt_method methodsIn,
            Rsrcm_stmt_kind_tgtm edgesIn,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out
            ) {
        super( methodsIn, edgesIn, out );
    }
    private CallGraph cicg = new CallGraph();
    private MethodToContexts m2c = new MethodToContexts();
    public boolean update() {
        boolean change = false;
        for( Iterator eIt = edgesIn.iterator(); eIt.hasNext(); ) {
            final Rsrcm_stmt_kind_tgtm.Tuple e = (Rsrcm_stmt_kind_tgtm.Tuple) eIt.next();
            if( cicg.addEdge(new Edge(e.srcm(), e.stmt(), e.tgtm(), e.kind())) ) {
                for( Iterator mcIt = m2c.get(e.srcm()).iterator(); mcIt.hasNext(); ) {
                    final MethodOrMethodContext mc = (MethodOrMethodContext) mcIt.next();
                    out.add( mc.context(), mc.method(), e.stmt(), e.kind(),
                            null, e.tgtm() );
                    change = true;
                }
            }
        }
        for( Iterator tIt = methodsIn.iterator(); tIt.hasNext(); ) {
            final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
            for( Iterator eIt = cicg.edgesOutOf(t.method()); eIt.hasNext(); ) {
                final Edge e = (Edge) eIt.next();
                out.add(t.ctxt(), t.method(), e.srcStmt(), e.kind(), null, e.tgt());
                change = true;
            }
            m2c.add( MethodContext.v( t.method(), t.ctxt() ) );
        }
        return change;
    }
}

