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
public class AOTCGScene 
{ 
    public AbsCallGraph cg;
    public AbsReachableMethodsAdapter rca;
    public AbsReachableMethods rc;

    public AbsMethodPAGBuilder mpb;
    public AbsMethodPAGContextifier mpc;
    public AbsCallEdgeContextifier cec;
    public AbsCallEdgeHandler ceh;

    public AbsPAG pag;
    public AbsPropagator prop;
    public PaddleComponent fprop;

    public Qmethod rmout;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm csedges;
    public Qsrcc_srcm_stmt_kind_tgtc_tgtm cgout;
    public Qsrcm_stmt_kind_tgtm ecsout;
    public Qctxt_method rcin;
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

    private PaddleNativeHelper nativeHelper;
    private NativeMethodDriver nativeMethodDriver;
    public NodeFactory nodeFactory;

    public DependencyManager depMan = new DependencyManager();

    public void setup(Qsrcc_srcm_stmt_kind_tgtc_tgtm cgEdges) {
        csedges = cgEdges;
        build();

        cg.queueDeps(depMan);
        rca.queueDeps(depMan);
        rc.queueDeps(depMan);
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

        // for speed only
        depMan.addPrec(fprop, prop);

        depMan.addPrec(prop, cg);
        depMan.addPrec(prop, rc);
        depMan.addPrec(prop, ceh);
        depMan.addPrec(prop, cec);
        depMan.addPrec(prop, mpb);
        depMan.addPrec(prop, mpc);

        depMan.addPrec(fprop, cg);
        depMan.addPrec(fprop, rc);
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
        rmout = PaddleScene.v().qFactory.Qmethod("aotrmout");
        cgout = PaddleScene.v().qFactory.Qsrcc_srcm_stmt_kind_tgtc_tgtm("aotcgout");
        ecsout = PaddleScene.v().qFactory.Qsrcm_stmt_kind_tgtm("aotecsout");
        rcin = PaddleScene.v().qFactory.Qctxt_method("aotrcin");
        rcout = PaddleScene.v().qFactory.Qctxt_method("aotrcout");
        parms = PaddleScene.v().qFactory.Qsrcm_stmt_kind_tgtm_src_dst("aotparms");
        rets = PaddleScene.v().qFactory.Qsrcm_stmt_kind_tgtm_src_dst("aotrets");

        simple = PaddleScene.v().qFactory.Qsrc_dst("aotsimple");
        load = PaddleScene.v().qFactory.Qsrc_fld_dst("aotload");
        store = PaddleScene.v().qFactory.Qsrc_dst_fld("aotstore");
        alloc = PaddleScene.v().qFactory.Qobj_var("aotalloc");

        csimple = PaddleScene.v().qFactory.Qsrcc_src_dstc_dst("aotcsimple");
        cload = PaddleScene.v().qFactory.Qsrcc_src_fld_dstc_dst("aotcload");
        cstore = PaddleScene.v().qFactory.Qsrcc_src_dstc_dst_fld("aotcstore");
        calloc = PaddleScene.v().qFactory.Qobjc_obj_varc_var("aotcalloc");

        paout = PaddleScene.v().qFactory.Qvarc_var_objc_obj("aotpaout");

    }

    private void buildPTA() {
        nodeFactory = new NodeFactory( simple, load, store, alloc );
        if( PaddleScene.v().options().simulate_natives() ) {
            nativeHelper = new PaddleNativeHelper(nodeFactory);
            nativeMethodDriver = new NativeMethodDriver(nativeHelper);
        }

        cec = PaddleScene.v().factory.CallEdgeContextifier( PaddleScene.v().ni, parms.reader("aotmpc"),
                rets.reader("aotmpc"), cgout.reader("aotmpc"), csimple );
        ceh = PaddleScene.v().factory.CallEdgeHandler( ecsout.reader("aotceh"), parms, rets, nodeFactory, true );

        mpb = PaddleScene.v().factory.MethodPAGBuilder( rmout.reader("aotmpb"), simple, load, store, alloc, nodeFactory, nativeMethodDriver );
        mpc = PaddleScene.v().factory.MethodPAGContextifier(
                PaddleScene.v().ni,
                simple.reader("aotmpc"),
                load.reader("aotmpc"),
                store.reader("aotmpc"),
                alloc.reader("aotmpc"),
                rcout.reader("aotmpc"),
                csimple, cload, cstore, calloc );
        pag = PaddleScene.v().factory.PAG( csimple.reader("aotpag"), cload.reader("aotpag"),
                cstore.reader("aotpag"), calloc.reader("aotpag") );
        prop = PaddleScene.v().factory.Propagator(PaddleScene.v().options().propagator(),
                csimple.reader("aotprop"), cload.reader("aotprop"),
                cstore.reader("aotprop"), calloc.reader("aotprop"), paout, pag);
        fprop = prop.fieldPropagator();
    }

    private void build() {
        buildQueues();
        buildPTA();
        cg = PaddleScene.v().factory.CallGraph( csedges.reader("aotcg"), ecsout, cgout );
        rca = PaddleScene.v().factory.ReachableMethodsAdapter( cgout.reader("aotrca"), rcin );
        rc = PaddleScene.v().factory.ReachableMethods( null, rcin.reader("aotrc"), rmout, rcout, null );
    }
}


