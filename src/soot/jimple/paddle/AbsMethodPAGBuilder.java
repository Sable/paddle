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

/** Creates intra-procedural pointer assignment edges.
 * @author Ondrej Lhotak
 */
public abstract class AbsMethodPAGBuilder implements PaddleComponent
{ 
    protected Rmethod in;
    protected Qsrc_dst simple;
    protected Qsrc_fld_dst load;
    protected Qsrc_dst_fld store;
    protected Qobj_var alloc;
    protected NodeFactory gnf;

    AbsMethodPAGBuilder( 
        Rmethod in,
        Qsrc_dst simple,
        Qsrc_fld_dst load,
        Qsrc_dst_fld store,
        Qobj_var alloc,
		NodeFactory gnf) {
        this.in = in;
        this.simple = simple;
        this.load = load;
        this.store = store;
        this.alloc = alloc;
        this.gnf = gnf;
    }
    public abstract boolean update();
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(in, this);
    }
}


