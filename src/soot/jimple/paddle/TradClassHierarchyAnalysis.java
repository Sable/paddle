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
import java.util.*;
import soot.util.*;

/** 
 * @author Ondrej Lhotak
 */
public class TradClassHierarchyAnalysis extends AbsClassHierarchyAnalysis
{ 
    TradClassHierarchyAnalysis( 
            Rvar_srcm_stmt_dtp_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qobj_var out
        ) {
        super( receivers, specials, out );
    }

    public boolean update() {
        boolean ret = false;

        for( Iterator receiverIt = receivers.iterator(); receiverIt.hasNext(); ) {

            final Rvar_srcm_stmt_dtp_signature_kind.Tuple receiver = (Rvar_srcm_stmt_dtp_signature_kind.Tuple) receiverIt.next();
            if(doReceiver(receiver.var())) ret = true;
        }
        for( Iterator specialIt = specials.iterator(); specialIt.hasNext(); ) {
            final Rvar_srcm_stmt_tgtm.Tuple special = (Rvar_srcm_stmt_tgtm.Tuple) specialIt.next();
            if(doReceiver(special.var())) ret = true;
        }
        return ret;
    }

    private NumberedSet vars = new NumberedSet(PaddleNumberers.v().varNodeNumberer());
    private boolean doReceiver( VarNode node ) {
        if(!vars.add(node)) return false;
        Type declType = node.getType();
        if(declType == null || !(declType instanceof RefType)) declType = clObject;
        Type actType = AnySubType.v((RefType) declType);
        AllocNode an = PaddleScene.v().nodeManager()
            .makeGlobalAllocNode(actType, actType);
        out.add(an, node);
        return true;
    }

    protected final RefType clObject = RefType.v("java.lang.Object");
}


