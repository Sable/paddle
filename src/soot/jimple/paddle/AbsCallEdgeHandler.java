/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004, 2005 Ondrej Lhotak
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
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** Creates inter-procedural pointer assignment edges.
 * @author Ondrej Lhotak
 */
public abstract class AbsCallEdgeHandler implements PaddleComponent
{ 
    AbsCallEdgeHandler( 
        Rsrcm_stmt_kind_tgtm in,
        Qsrcm_stmt_kind_tgtm_src_dst parms,
        Qsrcm_stmt_kind_tgtm_src_dst rets,
        NodeFactory gnf) {
        this.in = in;
        this.parms = parms;
        this.rets = rets;
        this.gnf = gnf;
    }

    protected Rsrcm_stmt_kind_tgtm in;
    protected Qsrcm_stmt_kind_tgtm_src_dst parms;
    protected Qsrcm_stmt_kind_tgtm_src_dst rets;
    protected NodeFactory gnf;
    
    public abstract boolean update();
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(in, this);
    }
}


