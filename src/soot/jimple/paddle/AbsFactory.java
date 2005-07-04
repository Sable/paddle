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

/** Factory that constructs Paddle components.
 * @author Ondrej Lhotak
 */
public abstract class AbsFactory 
{ 
    public abstract AbsCallEdgeContextifier CallEdgeContextifier(
            AbsNodeInfo ni,
            Rsrcm_stmt_kind_tgtm_src_dst parms,
            Rsrcm_stmt_kind_tgtm_src_dst rets,
            Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,
            Qsrcc_src_dstc_dst csimple
            );
    public abstract AbsCallEdgeHandler CallEdgeHandler(
        Rsrcm_stmt_kind_tgtm in,
        Qsrcm_stmt_kind_tgtm_src_dst parms,
        Qsrcm_stmt_kind_tgtm_src_dst rets,
        NodeFactory gnf,
        boolean processThis
        );
    public abstract AbsCallGraph CallGraph(
            Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
            Qsrcm_stmt_kind_tgtm ciout,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm csout
            );
    public abstract AbsClassHierarchyAnalysis ClassHierarchyAnalysis(
            Rvar_srcm_stmt_dtp_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qobj_var out
            );
    public abstract AbsContextCallGraphBuilder ContextCallGraphBuilder(
            Rctxt_method methodsIn,
            Rsrcm_stmt_kind_tgtm edgesIn,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out
            );
    public abstract AbsMethodPAGBuilder MethodPAGBuilder(
            Rmethod in,
            Qsrc_dst simple,
            Qsrc_fld_dst load,
            Qsrc_dst_fld store,
            Qobj_var alloc,
			NodeFactory gnf
            );
    public abstract AbsMethodPAGContextifier MethodPAGContextifier(
            AbsNodeInfo ni,
            Rsrc_dst simple,
            Rsrc_fld_dst load,
            Rsrc_dst_fld store,
            Robj_var alloc,
            Rctxt_method rcout,
            Qsrcc_src_dstc_dst csimple,
            Qsrcc_src_fld_dstc_dst cload,
            Qsrcc_src_dstc_dst_fld cstore,
            Qobjc_obj_varc_var calloc
            );
    public abstract AbsNodeInfo NodeInfo(
            Rvar_method_type locals,
            Rvar_type globals,
            Robj_method_type localallocs,
            Robj_type globalallocs
            );
    public abstract AbsPAG PAG(
            Rsrcc_src_dstc_dst simple,
            Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc
            );
    public abstract AbsPropagator Propagator(
            int kind,
            Rsrcc_src_dstc_dst simple,
            Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc,
            Qvarc_var_objc_obj propout,
            AbsPAG pag
            );
    public abstract AbsReachableMethods ReachableMethods(
            Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn,
            Rctxt_method methodsIn,
            Qmethod mout,
            Qctxt_method cmout,
            AbsCallGraph cg
            );
    public abstract AbsReachableMethodsAdapter ReachableMethodsAdapter(
            Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn,
            Qctxt_method cmout
            );
    public abstract AbsStaticCallBuilder StaticCallBuilder(
            Rmethod in,
            Qsrcm_stmt_kind_tgtm out,
            Qvar_srcm_stmt_dtp_signature_kind receivers,
            Qvar_srcm_stmt_tgtm specials,
			NodeFactory gnf
            );
    public abstract AbsStaticContextManager StaticContextManager(
            int kind,
            Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out,
            int k
            );
    public abstract AbsTypeManager TypeManager(
            Rvar_method_type locals,
            Rvar_type globals,
            Robj_method_type localallocs,
            Robj_type globalallocs
            );
    public abstract AbsVirtualCalls VirtualCalls(
            Rvarc_var_objc_obj pt,
            Rvar_srcm_stmt_dtp_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qvarc_var_objc_obj_srcm_stmt_kind_tgtm out,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm statics,
			AbsP2Sets p2sets
            );
    public abstract AbsVirtualContextManager VirtualContextManager(
            int kind,
            Rvarc_var_objc_obj_srcm_stmt_kind_tgtm in,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out,
            Qobjc_obj_varc_var thisOut,
			NodeFactory gnf,
            int k
            );
}

