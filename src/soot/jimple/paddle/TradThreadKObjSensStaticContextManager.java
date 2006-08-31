/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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
import java.util.*;

/** Assigns a target context to static call edges depending on the source
 * context.
 * @author Richard L. Halpert
 */
public class TradThreadKObjSensStaticContextManager extends AbsStaticContextManager
{ 
    TradThreadKObjSensStaticContextManager( Rsrcc_srcm_stmt_kind_tgtc_tgtm in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, int k ) {
        super( in, out );
    }
    public boolean update() {
        boolean change = false;
        // just keep the old context (copy srcc into tgtc)
        for( Iterator tIt = in.iterator(); tIt.hasNext(); ) {
            final Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple t = (Rsrcc_srcm_stmt_kind_tgtc_tgtm.Tuple) tIt.next();
            out.add( t.srcc(),
                    t.srcm(),
                    t.stmt(),
                    t.kind(),
                    t.srcc(),
                    t.tgtm() );
            change = true;
        }
        return change;
    }
}


