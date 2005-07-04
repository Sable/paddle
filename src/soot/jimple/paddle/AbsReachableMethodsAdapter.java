/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Ondrej Lhotak
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

/** Extracts source and destination methods from call edges as reachable methods.
 * @author Ondrej Lhotak
 */
public abstract class AbsReachableMethodsAdapter implements PaddleComponent
{ 
    protected Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn;
    protected Qctxt_method cmout;
    AbsReachableMethodsAdapter( Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn, Qctxt_method cmout ) {
        this.edgesIn = edgesIn;
        this.cmout = cmout;
    }
    public abstract boolean update();
    
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(edgesIn, this);
    }
}


