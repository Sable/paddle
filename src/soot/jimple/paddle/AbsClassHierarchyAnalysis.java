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
import soot.*;
import soot.jimple.paddle.queue.*;

/** For each receiver, constructs a dummy points-to set that may point
 * to any object of any subtype of the declared type of the receiver.
 * @author Ondrej Lhotak
 */
public abstract class AbsClassHierarchyAnalysis implements PaddleComponent
{ 
    protected Rvar_srcm_stmt_dtp_signature_kind receivers;
    protected Rvar_srcm_stmt_tgtm specials;
    protected Qobj_var out;

    AbsClassHierarchyAnalysis( 
            Rvar_srcm_stmt_dtp_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qobj_var out
        ) {
        this.receivers = receivers;
        this.specials = specials;
        this.out = out;
    }
    public abstract boolean update();
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(receivers, this);
        depMan.addQueueDep(specials, this);
    }
}


