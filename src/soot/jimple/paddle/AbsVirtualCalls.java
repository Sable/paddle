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
import soot.*;
import soot.jimple.paddle.queue.*;

/** Resolves virtual calls based on the actual type of the receiver.
 * @author Ondrej Lhotak
 */
public abstract class AbsVirtualCalls implements PaddleComponent
{ 
    protected Rvarc_var_objc_obj pt;
    protected Rvar_srcm_stmt_dtp_signature_kind receivers;
    protected Rvar_srcm_stmt_tgtm specials;
    protected Qvarc_var_objc_obj_srcm_stmt_kind_tgtm out;
    protected Qsrcc_srcm_stmt_kind_tgtc_tgtm statics;
    protected AbsP2Sets p2sets;

    AbsVirtualCalls( Rvarc_var_objc_obj pt,
            Rvar_srcm_stmt_dtp_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qvarc_var_objc_obj_srcm_stmt_kind_tgtm out,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm statics,
            AbsP2Sets p2sets
        ) {
        this.pt = pt;
        this.receivers = receivers;
        this.specials = specials;
        this.out = out;
        this.statics = statics;
        this.p2sets = p2sets;
    }
    public abstract boolean update();
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(pt, this);
        depMan.addQueueDep(receivers, this);
        depMan.addQueueDep(specials, this);
    }
}


