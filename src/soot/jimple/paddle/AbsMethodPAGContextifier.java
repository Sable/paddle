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
import soot.*;
import soot.jimple.paddle.queue.*;

/** Instantiates the pointer flow edges of methods in specific contexts.
 * @author Ondrej Lhotak
 */
public abstract class AbsMethodPAGContextifier implements PaddleComponent
{ 
    protected Rsrc_dst simple;
    protected Rsrc_fld_dst load;
    protected Rsrc_dst_fld store;
    protected Robj_var alloc;

    protected Rctxt_method rcout;

    protected Qsrcc_src_dstc_dst csimple;
    protected Qsrcc_src_fld_dstc_dst cload;
    protected Qsrcc_src_dstc_dst_fld cstore;
    protected Qobjc_obj_varc_var calloc;

    public AbsMethodPAGContextifier(
        Rsrc_dst simple,
        Rsrc_fld_dst load,
        Rsrc_dst_fld store,
        Robj_var alloc,

        Rctxt_method rcout,

        Qsrcc_src_dstc_dst csimple,
        Qsrcc_src_fld_dstc_dst cload,
        Qsrcc_src_dstc_dst_fld cstore,
        Qobjc_obj_varc_var calloc ) 
    {
        this.simple = simple;
        this.load = load;
        this.store = store;
        this.alloc = alloc;

        this.rcout = rcout;

        this.csimple = csimple;
        this.cload = cload;
        this.cstore = cstore;
        this.calloc = calloc;
    }
    public abstract boolean update();
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(simple, this);
        depMan.addQueueDep(load, this);
        depMan.addQueueDep(store, this);
        depMan.addQueueDep(alloc, this);
        depMan.addQueueDep(rcout, this);
    }
}

