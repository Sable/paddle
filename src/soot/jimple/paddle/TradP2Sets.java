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
import soot.jimple.paddle.queue.*;
import soot.*;
import java.util.*;

/** Manages the points-to sets for nodes.
 * @author Ondrej Lhotak
 */
public class TradP2Sets extends AbsP2Sets
{ 
    private P2SetMap vnToSet = new P2SetMap();
    private P2SetMap adfToSet = new P2SetMap();
    public PointsToSetReadOnly get( ContextVarNode cvn ) {
        return vnToSet.get(cvn);
    }
    public PointsToSetReadOnly get( ContextAllocDotField cadf ) {
        return adfToSet.get(cadf);
    }
    public PointsToSetInternal make( ContextVarNode cvn ) {
        return vnToSet.make(cvn);
    }
    public PointsToSetInternal make( ContextAllocDotField cadf ) {
        return adfToSet.make(cadf);
    }
    public Rvarc_var_objc_obj getReader( final VarNode var ) {
        final Qvarc_var_objc_obj retq = new Qvarc_var_objc_objTrad("getReader");
        Rvarc_var_objc_obj ret = retq.reader("getReader");
        for( Iterator cvnIt = var.contexts(); cvnIt.hasNext(); ) {
            final ContextVarNode cvn = (ContextVarNode) cvnIt.next();
            PointsToSetReadOnly ptset = get(cvn);
            ptset.forall( new P2SetVisitor() {
            public final void visit( ContextAllocNode n ) {
                ContextAllocNode can = (ContextAllocNode) n;
                retq.add( cvn.ctxt(), var, can.ctxt(), can.obj() );
            }} );
        }
        return ret;
    }
    public Rvarc_var_objc_obj getReader() {
        final Qvarc_var_objc_obj retq = new Qvarc_var_objc_objTrad("getReader");
        Rvarc_var_objc_obj ret = retq.reader("getReader");
        for( Iterator varIt = PaddleNumberers.v().varNodeNumberer().iterator(); varIt.hasNext(); ) {
            final VarNode var = (VarNode) varIt.next();
            for( Iterator cvnIt = var.contexts(); cvnIt.hasNext(); ) {
                final ContextVarNode cvn = (ContextVarNode) cvnIt.next();
                PointsToSetReadOnly ptset = get(cvn);
                ptset.forall( new P2SetVisitor() {
                public final void visit( ContextAllocNode n ) {
                    ContextAllocNode can = (ContextAllocNode) n;
                    retq.add( cvn.ctxt(), var, can.ctxt(), can.obj() );
                }} );
            }
        }
        return ret;
    }
    public Rbasec_base_fld_objc_obj fieldPt() {
        final Qbasec_base_fld_objc_obj retq = new Qbasec_base_fld_objc_objTrad("fieldPt");
        Rbasec_base_fld_objc_obj ret = retq.reader("fieldPt");
        for( Iterator cadfIt = adfToSet.keySet().iterator(); cadfIt.hasNext(); ) {
            final ContextAllocDotField cadf = (ContextAllocDotField) cadfIt.next();
            PointsToSetReadOnly ptset = get(cadf);
            ptset.forall( new P2SetVisitor() {
            public final void visit( ContextAllocNode n ) {
                ContextAllocNode can = (ContextAllocNode) n;
                retq.add( cadf.ctxt(), cadf.adf().base(), cadf.adf().field(),
                    can.ctxt(), can.obj() );
            }} );
        }
        return ret;
    }
}

