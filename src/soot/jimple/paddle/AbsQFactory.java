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
import soot.jimple.paddle.queue.*;

/** Factory that constructs Paddle queues.
 * @author Ondrej Lhotak
 */
public abstract class AbsQFactory 
{ 
    public abstract Qmethod Qmethod(String name);
    public abstract Qsrcm_stmt_kind_tgtm Qsrcm_stmt_kind_tgtm(String name);
    public abstract Qvar_srcm_stmt_dtp_signature_kind Qvar_srcm_stmt_dtp_signature_kind(String name);
    public abstract Qvar_srcm_stmt_tgtm Qvar_srcm_stmt_tgtm(String name);
    public abstract Qsrcc_srcm_stmt_kind_tgtc_tgtm Qsrcc_srcm_stmt_kind_tgtc_tgtm(String name);
    public abstract Qctxt_method Qctxt_method(String name);
    public abstract Qsrcm_stmt_kind_tgtm_src_dst Qsrcm_stmt_kind_tgtm_src_dst(String name);
    public abstract Qsrc_dst Qsrc_dst(String name);
    public abstract Qsrc_fld_dst Qsrc_fld_dst(String name);
    public abstract Qsrc_dst_fld Qsrc_dst_fld(String name);
    public abstract Qobj_var Qobj_var(String name);
    public abstract Qsrcc_src_dstc_dst Qsrcc_src_dstc_dst(String name);
    public abstract Qsrcc_src_fld_dstc_dst Qsrcc_src_fld_dstc_dst(String name);
    public abstract Qsrcc_src_dstc_dst_fld Qsrcc_src_dstc_dst_fld(String name);
    public abstract Qobjc_obj_varc_var Qobjc_obj_varc_var(String name);
    public abstract Qvarc_var_objc_obj Qvarc_var_objc_obj(String name);
    public abstract Qvarc_var_objc_obj_srcm_stmt_kind_tgtm Qvarc_var_objc_obj_srcm_stmt_kind_tgtm(String name);
    public abstract Qvar_method_type Qvar_method_type(String name);
    public abstract Qvar_type Qvar_type(String name);
    public abstract Qobj_method_type Qobj_method_type(String name);
    public abstract Qobj_type Qobj_type(String name);
}

