/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

/** Keeps track of which methods are reachable.
 * @author Ondrej Lhotak
 */
public class TradReachableMethods extends AbsReachableMethods
{ 
    private Set reachables = new HashSet();
    private AbsCallGraph cg;
    private Rctxt_method newMethods;
    TradReachableMethods( Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn, Rctxt_method methodsIn, Qctxt_method out, AbsCallGraph cg ) {
        super( edgesIn, methodsIn, out );
        this.cg = cg;
        newMethods = out.reader("tradrm");
    }
    private boolean processEdge( Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t ) {
        if( !reachables.contains( MethodContext.v( t.srcm(), t.srcc() ) ) ) return false;
        return add( t.tgtc(), t.tgtm() );
    }
    public boolean update() {
        boolean change = false;

        if( methodsIn != null ) {
            for( Iterator tIt = methodsIn.iterator(); tIt.hasNext(); ) {
                final Rctxt_method.Tuple t = (Rctxt_method.Tuple) tIt.next();
                change = change | add( t.ctxt(), t.method() );
            }
        }
        
        for( Iterator tIt = edgesIn.iterator(); tIt.hasNext(); ) {
        
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            change = change | processEdge( t );
        }
        while( newMethods.hasNext() ) {
            for( Iterator tIt = cg.edgesOutOf( newMethods ).iterator(); tIt.hasNext(); ) {
                final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
                change = change | processEdge( t );
            }
        }
        return change;
    }
    boolean add( Context c, SootMethod m ) {
        if( reachables.add( MethodContext.v(m, c) ) ) {
            out.add( c, m );
            return true;
        }
        return false;
    }
    int size() {
        return reachables.size();
    }
    boolean contains( Context c, SootMethod m ) {
        return reachables.contains(MethodContext.v(m, c));
    }
    Rctxt_method methods() {
        Qctxt_methodTrad qret = new Qctxt_methodTrad("methods");
        Rctxt_method ret = qret.reader("methods");
        for( Iterator momcIt = reachables.iterator(); momcIt.hasNext(); ) {
            final MethodOrMethodContext momc = (MethodOrMethodContext) momcIt.next();
            qret.add( momc.context(), momc.method() );
        }
        return ret;
    }
}

