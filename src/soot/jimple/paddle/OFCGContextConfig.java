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
import soot.*;
import java.util.*;

/** OFCG-Context call graph Paddle configuration
 * @author Ondrej Lhotak
 */
public class OFCGContextConfig extends AbsConfig 
{ 
    public void setup() {
        ofcgScene = new OFCGScene();
        ofcgScene.setup();
        PaddleScene.v().ni.queueDeps(ofcgScene.depMan);
        PaddleScene.v().tm.queueDeps(ofcgScene.depMan);

        csEdges = PaddleScene.v().qFactory.Qsrcc_srcm_stmt_kind_tgtc_tgtm("csEdges");
        csout = PaddleScene.v().qFactory.Qsrcc_srcm_stmt_kind_tgtc_tgtm("csout");
        cg = PaddleScene.v().factory.CallGraph(csEdges.reader("cg"), null, csout);
        rc = PaddleScene.v().factory.ReachableMethods(csout.reader("rc"), null, null, null, cg);

        zhuContext = new ZhuContext(ofcgScene.cg, csEdges);
    }
    public void solve() {
        ofcgScene.solve();
        zhuContext.solve();
        cg.update();
        for( Iterator mIt = Scene.v().getEntryPoints().iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            rc.add(null, m);
        }
        rc.update();

        Results.v().cg = cg;
        Results.v().rc = rc;
        Results.v().pt = null;
        Results.v().gnf = null;
    }

    private OFCGScene ofcgScene;
    private ZhuContext zhuContext;
    private Qsrcc_srcm_stmt_kind_tgtc_tgtm csEdges;
    private Qsrcc_srcm_stmt_kind_tgtc_tgtm csout;
    private AbsCallGraph cg;
    private AbsReachableMethods rc;
}

