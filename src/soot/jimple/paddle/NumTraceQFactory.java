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
public class NumTraceQFactory extends AbsQFactory
{ 
    public Qmethod Qmethod(String name) {
        return new QmethodNumTrace(name); 
    }
    public Qsrcm_stmt_kind_tgtm Qsrcm_stmt_kind_tgtm(String name) {
        return new Qsrcm_stmt_kind_tgtmNumTrace(name); 
    }
    public Qvar_srcm_stmt_dtp_signature_kind Qvar_srcm_stmt_dtp_signature_kind(String name) {
        return new Qvar_srcm_stmt_dtp_signature_kindNumTrace(name); 
    }
    public Qvar_srcm_stmt_tgtm Qvar_srcm_stmt_tgtm(String name) {
        return new Qvar_srcm_stmt_tgtmNumTrace(name); 
    }
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm Qsrcc_srcm_stmt_kind_tgtc_tgtm(String name) {
        return new Qsrcc_srcm_stmt_kind_tgtc_tgtmNumTrace(name);
    }
    public Qctxt_method Qctxt_method(String name) {
        return new Qctxt_methodNumTrace(name); 
    }
    public Qsrcm_stmt_kind_tgtm_src_dst Qsrcm_stmt_kind_tgtm_src_dst(String name) {
        return new Qsrcm_stmt_kind_tgtm_src_dstNumTrace(name); 
    }
    public Qsrc_dst Qsrc_dst(String name) {
        return new Qsrc_dstNumTrace(name); 
    }
    public Qsrc_fld_dst Qsrc_fld_dst(String name) {
        return new Qsrc_fld_dstNumTrace(name); 
    }
    public Qsrc_dst_fld Qsrc_dst_fld(String name) {
        return new Qsrc_dst_fldNumTrace(name); 
    }
    public Qobj_var Qobj_var(String name) {
        return new Qobj_varNumTrace(name); 
    }
    public Qsrcc_src_dstc_dst Qsrcc_src_dstc_dst(String name) {
        return new Qsrcc_src_dstc_dstNumTrace(name); 
    }
    public Qsrcc_src_fld_dstc_dst Qsrcc_src_fld_dstc_dst(String name) {
        return new Qsrcc_src_fld_dstc_dstNumTrace(name); 
    }
    public Qsrcc_src_dstc_dst_fld Qsrcc_src_dstc_dst_fld(String name) {
        return new Qsrcc_src_dstc_dst_fldNumTrace(name); 
    }
    public Qobjc_obj_varc_var Qobjc_obj_varc_var(String name) {
        return new Qobjc_obj_varc_varNumTrace(name); 
    }
    public Qvarc_var_objc_obj Qvarc_var_objc_obj(String name) {
        return new Qvarc_var_objc_objNumTrace(name); 
    }
    public Qvarc_var_objc_obj_srcm_stmt_kind_tgtm Qvarc_var_objc_obj_srcm_stmt_kind_tgtm(String name) {
        return new Qvarc_var_objc_obj_srcm_stmt_kind_tgtmNumTrace(name); 
    }
    public Qvar_method_type Qvar_method_type(String name) {
        return new Qvar_method_typeNumTrace(name); 
    }
    public Qvar_type Qvar_type(String name) {
        return new Qvar_typeNumTrace(name); 
    }
    public Qobj_method_type Qobj_method_type(String name) {
        return new Qobj_method_typeNumTrace(name); 
    }
    public Qobj_type Qobj_type(String name) {
        return new Qobj_typeNumTrace(name); 
    }
}

