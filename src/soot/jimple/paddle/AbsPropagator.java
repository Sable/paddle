/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003, 2004 Ondrej Lhotak
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

/** Propagates points-to sets along the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public abstract class AbsPropagator implements PaddleComponent
{ 
    protected Rsrcc_src_dstc_dst newSimple;
    protected Rsrcc_src_fld_dstc_dst newLoad;
    protected Rsrcc_src_dstc_dst_fld newStore;
    protected Robjc_obj_varc_var newAlloc;
    protected Qvarc_var_objc_obj ptout;
    protected AbsPAG pag;

    AbsPropagator( Rsrcc_src_dstc_dst simple,
            Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc,
            Qvarc_var_objc_obj ptout,
            AbsPAG pag
        ) {
        this.newSimple = simple;
        this.newLoad = load;
        this.newStore = store;
        this.newAlloc = alloc;
        this.ptout = ptout;
        this.pag = pag;
    }
    public abstract AbsP2Sets p2sets();
    public abstract boolean update();
    public abstract boolean fieldUpdate();
    public PaddleComponent fieldPropagator() {
        return new PaddleComponent() {
            public boolean update() {
                return fieldUpdate();
            }
            public void queueDeps(DependencyManager depMan) {
                depMan.addQueueDep(newSimple, this);
                depMan.addQueueDep(newLoad, this);
                depMan.addQueueDep(newStore, this);
                depMan.addQueueDep(newAlloc, this);
            }
        };
    }
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(newSimple, this);
        depMan.addQueueDep(newLoad, this);
        depMan.addQueueDep(newStore, this);
        depMan.addQueueDep(newAlloc, this);
    }
}


