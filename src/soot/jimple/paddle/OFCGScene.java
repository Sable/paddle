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
import soot.jimple.paddle.bdddomains.*;
import soot.options.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import jedd.*;
import jedd.order.*;
import soot.jimple.toolkits.pointer.util.NativeHelper;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;

/** This class puts all of the pieces of Paddle together and connects them
 * with queues.
 * @author Ondrej Lhotak
 */
public class OFCGScene 
{ 
    public AbsStaticCallBuilder scgb;

    public AbsContextCallGraphBuilder cscgb;
    public AbsCallGraph cg;
    public AbsStaticContextManager scm;
    public AbsReachableMethods rc;

    public AbsMethodPAGBuilder mpb;
    public AbsMethodPAGContextifier mpc;
    public AbsCallEdgeContextifier cec;
    public AbsCallEdgeHandler ceh;

    public AbsPAG pag;
    public AbsPropagator prop;
    public PaddleComponent fprop;

    public AbsVirtualCalls vcr;
    public AbsVirtualContextManager vcm;

    public Qmethod rmout;
    public Qsrcm_stmt_kind_tgtm scgbout;
    public Qvar_srcm_stmt_dtp_signature_kind receivers;
    public Qvar_srcm_stmt_tgtm specials;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm staticcalls;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm csedges;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cgout;
    public Qsrcm_stmt_kind_tgtm ecsout;
    public Qctxt_method rcout;
    public Qsrcm_stmt_kind_tgtm_src_dst parms;
    public Qsrcm_stmt_kind_tgtm_src_dst rets;

    public Qsrc_dst simple;
    public Qsrc_fld_dst load;
    public Qsrc_dst_fld store;
    public Qobj_var alloc;

    public Qsrcc_src_dstc_dst csimple;
    public Qsrcc_src_fld_dstc_dst cload;
    public Qsrcc_src_dstc_dst_fld cstore;
    public Qobjc_obj_varc_var calloc;

    public Qvarc_var_objc_obj paout;

    public Qvarc_var_objc_obj_srcm_stmt_kind_tgtm virtualcalls;

    private PaddleNativeHelper nativeHelper;
    private NativeMethodDriver nativeMethodDriver;
    public NodeFactory nodeFactory;

    public DependencyManager depMan = new DependencyManager();

    public void setup() {
        build();

        scgb.queueDeps(depMan);
        cscgb.queueDeps(depMan);
        cg.queueDeps(depMan);
        scm.queueDeps(depMan);
        rc.queueDeps(depMan);
        vcr.queueDeps(depMan);
        vcm.queueDeps(depMan);
        pag.queueDeps(depMan);
        prop.queueDeps(depMan);
        fprop.queueDeps(depMan);
        mpc.queueDeps(depMan);

        cec.queueDeps(depMan);
        ceh.queueDeps(depMan);
        mpb.queueDeps(depMan);

        depMan.addDep(PaddleScene.v().ni, mpb);
        depMan.addDep(PaddleScene.v().ni, cec);

        depMan.addDep(PaddleScene.v().tm, prop);
        depMan.addDep(pag, prop);
        depMan.addDep(prop, prop);
        depMan.addDep(cg, rc);

        depMan.addDep(fprop, fprop);
        depMan.addDep(prop, fprop);
        depMan.addDep(fprop, prop);
        depMan.addDep(pag, fprop);
        depMan.addDep(PaddleScene.v().tm, fprop);

        depMan.addPrec(cec, PaddleScene.v().ni);
        depMan.addPrec(cec, ceh);

        depMan.addPrec(mpc, PaddleScene.v().ni);
        depMan.addPrec(mpc, mpb);
        depMan.addPrec(mpc, rc);

        depMan.addPrec(fprop, pag);

        depMan.addPrec(prop, pag);

        //depMan.addPrec(vcr, scgb);

        // for speed only
        depMan.addPrec(fprop, prop);

        depMan.addPrec(prop, vcr);
        depMan.addPrec(prop, vcm);
        depMan.addPrec(prop, scm);
        depMan.addPrec(prop, cg);
        depMan.addPrec(prop, rc);
        depMan.addPrec(prop, scgb);
        depMan.addPrec(prop, cscgb);
        depMan.addPrec(prop, ceh);
        depMan.addPrec(prop, cec);
        depMan.addPrec(prop, mpb);
        depMan.addPrec(prop, mpc);

        depMan.addPrec(fprop, vcr);
        depMan.addPrec(fprop, vcm);
        depMan.addPrec(fprop, scm);
        depMan.addPrec(fprop, cg);
        depMan.addPrec(fprop, rc);
        depMan.addPrec(fprop, scgb);
        depMan.addPrec(fprop, cscgb);
        depMan.addPrec(fprop, ceh);
        depMan.addPrec(fprop, cec);
        depMan.addPrec(fprop, mpb);
        depMan.addPrec(fprop, mpc);
    }
    public void solve() {
        for( Iterator mIt = Scene.v().getEntryPoints().iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            rc.add( null, m );
        }
        depMan.update();
    }

    private void buildQueues() {
        rmout = PaddleScene.v().qFactory.Qmethod("rmout");
        scgbout = PaddleScene.v().qFactory.Qsrcm_stmt_kind_tgtm("scgbout");
        receivers = PaddleScene.v().qFactory.Qvar_srcm_stmt_dtp_signature_kind("receivers");
        specials = PaddleScene.v().qFactory.Qvar_srcm_stmt_tgtm("specials");
        staticcalls = PaddleScene.v().qFactory.Qsrcc_srcm_stmt_kind_tgtc_tgtm("staticcalls");
        csedges = PaddleScene.v().qFactory.Qsrcc_srcm_stmt_kind_tgtc_tgtm("csedges");
        cgout = PaddleScene.v().qFactory.Qsrcc_srcm_stmt_kind_tgtc_tgtm("cgout");
        ecsout = PaddleScene.v().qFactory.Qsrcm_stmt_kind_tgtm("ecsout");
        rcout = PaddleScene.v().qFactory.Qctxt_method("rcout");
        parms = PaddleScene.v().qFactory.Qsrcm_stmt_kind_tgtm_src_dst("parms");
        rets = PaddleScene.v().qFactory.Qsrcm_stmt_kind_tgtm_src_dst("rets");

        simple = PaddleScene.v().qFactory.Qsrc_dst("simple");
        load = PaddleScene.v().qFactory.Qsrc_fld_dst("load");
        store = PaddleScene.v().qFactory.Qsrc_dst_fld("store");
        alloc = PaddleScene.v().qFactory.Qobj_var("alloc");

        csimple = PaddleScene.v().qFactory.Qsrcc_src_dstc_dst("csimple");
        cload = PaddleScene.v().qFactory.Qsrcc_src_fld_dstc_dst("cload");
        cstore = PaddleScene.v().qFactory.Qsrcc_src_dstc_dst_fld("cstore");
        calloc = PaddleScene.v().qFactory.Qobjc_obj_varc_var("calloc");

        paout = PaddleScene.v().qFactory.Qvarc_var_objc_obj("paout");

        virtualcalls = PaddleScene.v().qFactory.Qvarc_var_objc_obj_srcm_stmt_kind_tgtm("virtualcalls");
    }

    private void buildPTA() {
        nodeFactory = new NodeFactory( simple, load, store, alloc );
        if( PaddleScene.v().options().simulate_natives() ) {
            nativeHelper = new PaddleNativeHelper(nodeFactory);
            nativeMethodDriver = new NativeMethodDriver(nativeHelper);
        }

        cec = PaddleScene.v().factory.CallEdgeContextifier( PaddleScene.v().ni, parms.reader("mpc"),
                rets.reader("mpc"), cgout.reader("mpc"), csimple );
        ceh = PaddleScene.v().factory.CallEdgeHandler( 
                ecsout.reader("ceh"),
                parms,
                rets,
                nodeFactory,
                PaddleScene.v().options().this_edges() );

        mpb = PaddleScene.v().factory.MethodPAGBuilder( rmout.reader("mpb"), simple, load, store, alloc, nodeFactory, nativeMethodDriver );
        mpc = PaddleScene.v().factory.MethodPAGContextifier(
                PaddleScene.v().ni,
                simple.reader("mpc"),
                load.reader("mpc"),
                store.reader("mpc"),
                alloc.reader("mpc"),
                rcout.reader("mpc"),
                csimple, cload, cstore, calloc );
        pag = PaddleScene.v().factory.PAG( csimple.reader("pag"), cload.reader("pag"),
                cstore.reader("pag"), calloc.reader("pag") );
        prop = PaddleScene.v().factory.Propagator(PaddleScene.v().options().propagator(),
                csimple.reader("prop"), cload.reader("prop"),
                cstore.reader("prop"), calloc.reader("prop"), paout, pag);
        fprop = prop.fieldPropagator();
    }

    private void build() {
        buildQueues();
        buildPTA();
        scgb = PaddleScene.v().factory.StaticCallBuilder( rmout.reader("scgb"), scgbout, receivers, specials, nodeFactory );

        cg = PaddleScene.v().factory.CallGraph( csedges.reader("cg"), ecsout, cgout );
        rc = PaddleScene.v().factory.ReachableMethods( cgout.reader("rc"), null, rmout, rcout, cg );
        cscgb = PaddleScene.v().factory.ContextCallGraphBuilder( rcout.reader("cscgb"), scgbout.reader("cscgb"), staticcalls );

        vcr = PaddleScene.v().factory.VirtualCalls( paout.reader("vcr"), receivers.reader("vcr"), specials.reader("vcr"), virtualcalls, staticcalls, prop.p2sets() );

        scm = PaddleScene.v().factory.StaticContextManager( PaddleScene.v().cgoptions().context(), staticcalls.reader("scm"), csedges, PaddleScene.v().cgoptions().k() );
        vcm = PaddleScene.v().factory.VirtualContextManager(
                PaddleScene.v().cgoptions().context(),
                virtualcalls.reader("vcm"),
                csedges,
                PaddleScene.v().options().this_edges() ? null : calloc,
                nodeFactory,
                PaddleScene.v().cgoptions().k() );
    }
}


