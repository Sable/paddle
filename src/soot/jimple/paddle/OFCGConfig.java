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

/** On-the-fly call graph Paddle configuration
 * @author Ondrej Lhotak
 */
public class OFCGConfig extends AbsConfig 
{ 
    public void setup() {
        ofcgScene = new OFCGScene();
        ofcgScene.setup();
        PaddleScene.v().ni.queueDeps(ofcgScene.depMan);
        PaddleScene.v().tm.queueDeps(ofcgScene.depMan);
    }
    public void solve() {
        ofcgScene.solve();
        Results.v().cg = ofcgScene.cg;
        Results.v().rc = ofcgScene.rc;
        Results.v().pt = ofcgScene.prop.p2sets();
        Results.v().gnf = ofcgScene.nodeFactory;
    }

    private OFCGScene ofcgScene;
}

