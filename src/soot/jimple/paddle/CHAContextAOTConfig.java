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

/** CHA-Context-AOT call graph Paddle configuration
 * @author Ondrej Lhotak
 */
public class CHAContextAOTConfig extends AbsConfig 
{ 
    public void setup() {
        chaScene = new CHAScene();
        chaScene.setup();
        PaddleScene.v().ni.queueDeps(chaScene.depMan);
        PaddleScene.v().tm.queueDeps(chaScene.depMan);

        zhuContext = new ZhuContext(chaScene.cg, csEdges);

        aotcgScene = new AOTCGScene();
        aotcgScene.setup(csEdges);
        PaddleScene.v().ni.queueDeps(aotcgScene.depMan);
        PaddleScene.v().tm.queueDeps(aotcgScene.depMan);
    }
    public void solve() {
        chaScene.solve();
        zhuContext.solve();
        aotcgScene.solve();

        Results.v().cg = aotcgScene.cg;
        Results.v().rc = aotcgScene.rc;
        Results.v().pt = aotcgScene.prop.p2sets();
        Results.v().gnf = aotcgScene.nodeFactory;
    }

    private CHAScene chaScene;
    private AOTCGScene aotcgScene;
    private ZhuContext zhuContext;
    private Qsrcc_srcm_stmt_kind_tgtc_tgtm csEdges =
        PaddleScene.v().qFactory.Qsrcc_srcm_stmt_kind_tgtc_tgtm("csEdges");
}

