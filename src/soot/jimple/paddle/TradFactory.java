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
import soot.options.CGOptions;
import soot.options.PaddleOptions;

/** Factory that constructs Paddle components.
 * @author Ondrej Lhotak
 */
public class TradFactory extends AbsFactory
{ 
    public AbsCallEdgeContextifier CallEdgeContextifier(
            AbsNodeInfo ni,
            Rsrcm_stmt_kind_tgtm_src_dst parms,
            Rsrcm_stmt_kind_tgtm_src_dst rets,
            Rsrcc_srcm_stmt_kind_tgtc_tgtm calls,
            Qsrcc_src_dstc_dst csimple
            ) {
        return new TradCallEdgeContextifier((TradNodeInfo) ni, parms, rets, calls, csimple);
    }
    public AbsCallEdgeHandler CallEdgeHandler(
            Rsrcm_stmt_kind_tgtm in,
            Qsrcm_stmt_kind_tgtm_src_dst parms,
            Qsrcm_stmt_kind_tgtm_src_dst rets,
            NodeFactory gnf,
            boolean processThis
            ) {
        return new TradCallEdgeHandler(in, parms, rets, gnf, processThis);
    }
    public AbsCallGraph CallGraph(
            Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
            Qsrcm_stmt_kind_tgtm ciout,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm csout
            ) {
        return new TradCallGraph(in, ciout, csout);
    }
    public AbsClassHierarchyAnalysis ClassHierarchyAnalysis(
            Rvar_srcm_stmt_dtp_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qobj_var out
            ) {
        return new TradClassHierarchyAnalysis(receivers, specials, out);
    }
    public AbsContextCallGraphBuilder ContextCallGraphBuilder(
            Rctxt_method methodsIn,
            Rsrcm_stmt_kind_tgtm edgesIn,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out
            ) {
        return new TradContextCallGraphBuilder(methodsIn, edgesIn, out);
    }
    public AbsMethodPAGBuilder MethodPAGBuilder(
            Rmethod in,
            Qsrc_dst simple,
            Qsrc_fld_dst load,
            Qsrc_dst_fld store,
            Qobj_var alloc,
			NodeFactory gnf
            ) {
        return new TradMethodPAGBuilder(in, simple, load, store, alloc, gnf);
    }
    public AbsMethodPAGContextifier MethodPAGContextifier(
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
            ) {
        return new TradMethodPAGContextifier((TradNodeInfo) ni,
                simple, load, store, alloc,
                rcout, csimple, cload, cstore, calloc);
    }
    public AbsNodeInfo NodeInfo(
            Rvar_method_type locals,
            Rvar_type globals,
            Robj_method_type localallocs,
            Robj_type globalallocs
            ) {
        return new TradNodeInfo(locals, globals, localallocs, globalallocs);
    }
    public AbsPAG PAG(
            Rsrcc_src_dstc_dst simple,
            Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc
            ) {
        return new TradPAG(simple, load, store, alloc);
    }
    public AbsPropagator Propagator(
            int kind,
            Rsrcc_src_dstc_dst simple,
            Rsrcc_src_fld_dstc_dst load,
            Rsrcc_src_dstc_dst_fld store,
            Robjc_obj_varc_var alloc,
            Qvarc_var_objc_obj propout,
            AbsPAG pag
            ) {
        switch( kind ) {
            case PaddleOptions.propagator_worklist:
                return new PropWorklist(simple, load, store, alloc, propout, pag);
            case PaddleOptions.propagator_iter:
                return new PropIter(simple, load, store, alloc, propout, pag);
            case PaddleOptions.propagator_alias:
                return new PropAlias(simple, load, store, alloc, propout, pag);
            case PaddleOptions.propagator_bdd:
                return new PropBDD(simple, load, store, alloc, propout, pag);
            case PaddleOptions.propagator_incbdd:
                return new PropBDDInc(simple, load, store, alloc, propout, pag);
            default:
                throw new RuntimeException( "Unimplemented propagator specified "+kind );
        }
    }
    public AbsReachableMethods ReachableMethods(
            Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn,
            Rctxt_method methodsIn,
            Qmethod mout,
            Qctxt_method cmout,
            AbsCallGraph cg
            ) {
        return new TradReachableMethods(edgesIn, methodsIn, mout, cmout, cg);
    }
    public AbsReachableMethodsAdapter ReachableMethodsAdapter(
            Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn,
            Qctxt_method cmout
            ) {
        return new TradReachableMethodsAdapter(edgesIn, cmout);
    }
    public AbsStaticCallBuilder StaticCallBuilder(
            Rmethod in,
            Qsrcm_stmt_kind_tgtm out,
            Qvar_srcm_stmt_dtp_signature_kind receivers,
            Qvar_srcm_stmt_tgtm specials,
			NodeFactory gnf
            ) {
        return new TradStaticCallBuilder(in, out, receivers, specials, gnf);
    }
    public AbsStaticContextManager StaticContextManager(
            int kind,
            Rsrcc_srcm_stmt_kind_tgtc_tgtm in,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out,
            int k
            ) {
        switch(kind) {
            case CGOptions.context_insens:
                return new TradInsensitiveStaticContextManager(in, out);
            case CGOptions.context_1cfa:
                return new Trad1CFAStaticContextManager(in, out);
            case CGOptions.context_objsens:
                return new TradObjSensStaticContextManager(in, out);
            case CGOptions.context_kcfa:
                return new TradKCFAStaticContextManager(in, out, k);
            case CGOptions.context_kobjsens:
                return new TradKObjSensStaticContextManager(in, out, k);
            default:
                throw new RuntimeException( "Unhandled kind of context-sensitivity "+kind );
        }
    }
    public AbsTypeManager TypeManager(
            Rvar_method_type locals,
            Rvar_type globals,
            Robj_method_type localallocs,
            Robj_type globalallocs
            ) {
        return new TradTypeManager(locals, globals, localallocs, globalallocs);
    }
    public AbsVirtualCalls VirtualCalls(
            Rvarc_var_objc_obj pt,
            Rvar_srcm_stmt_dtp_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qvarc_var_objc_obj_srcm_stmt_kind_tgtm out,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm statics,
			AbsP2Sets p2sets
            ) {
        return new TradVirtualCalls(pt, receivers, specials, out, statics, p2sets);
    }
    public AbsVirtualContextManager VirtualContextManager(
            int kind,
            Rvarc_var_objc_obj_srcm_stmt_kind_tgtm in,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm out,
            Qobjc_obj_varc_var thisOut,
            NodeFactory gnf,
            int k
            ) {
        switch(kind) {
            case CGOptions.context_insens:
                return new TradInsensitiveVirtualContextManager(in, out, thisOut, gnf);
            case CGOptions.context_1cfa:
                return new Trad1CFAVirtualContextManager(in, out, thisOut, gnf);
            case CGOptions.context_objsens:
                return new TradObjSensVirtualContextManager(in, out, thisOut, gnf);
            case CGOptions.context_kcfa:
                return new TradKCFAVirtualContextManager(in, out, thisOut, gnf, k);
            case CGOptions.context_kobjsens:
                return new TradKObjSensVirtualContextManager(in, out, thisOut, gnf, k);
            default:
                throw new RuntimeException( "Unhandled kind of context-sensitivity "+kind );
        }
    }
}

