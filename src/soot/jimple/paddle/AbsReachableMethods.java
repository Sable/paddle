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
import soot.*;
import java.util.*;

/** Keeps track of which methods are reachable.
 * @author Ondrej Lhotak
 */
public abstract class AbsReachableMethods implements PaddleComponent
{ 
    protected Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn;
    protected Rctxt_method methodsIn;
    protected Qmethod mout;
    protected Qctxt_method cmout;
    AbsReachableMethods( Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn, Rctxt_method methodsIn, Qmethod mout, Qctxt_method cmout ) {
        this.edgesIn = edgesIn;
        this.methodsIn = methodsIn;
        this.mout = mout;
        this.cmout = cmout;
    }
    public abstract boolean update();
    abstract boolean add( Context c, SootMethod m );
    abstract int sizeM();
    abstract int sizeCM();
    abstract boolean contains( Context c, SootMethod m );
    abstract boolean contains( SootMethod m );
    public abstract Rctxt_method contextMethods();
    public abstract Rmethod methods();
    public abstract Iterator methodIterator();
    public abstract long countContexts(SootMethod m);
    
    public void queueDeps(DependencyManager depMan) {
        depMan.addQueueDep(edgesIn, this);
        if(methodsIn != null)
            depMan.addQueueDep(methodsIn, this);
    }
}


