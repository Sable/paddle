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
import soot.jimple.paddle.queue.*;

/** Assigns a target context to virtual call edges depending on the source
 * context.
 * @author Ondrej Lhotak
 */
public abstract class AbsVirtualContextManager implements PaddleComponent
{ 
    protected Rvarc_var_objc_obj_srcm_stmt_kind_tgtm in;
    protected Qsrcc_srcm_stmt_kind_tgtc_tgtm out;
    protected Qobjc_obj_varc_var thisOut;
    protected NodeFactory gnf;
    AbsVirtualContextManager( Rvarc_var_objc_obj_srcm_stmt_kind_tgtm in,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out,
            Qobjc_obj_varc_var thisOut,
            NodeFactory gnf
            ) {
        this.in = in;
        this.out = out;
        this.thisOut = thisOut;
        this.gnf = gnf;
    }
    public abstract boolean update();
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(in, this);
    }
}


