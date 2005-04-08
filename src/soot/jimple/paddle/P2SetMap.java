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
import soot.*;
import java.util.*;

/** Manages the points-to sets for nodes.
 * @author Ondrej Lhotak
 */
public class P2SetMap
{ 
    private Map map;
    public P2SetMap() {
        map = new HashMap();
    }
    public PointsToSetReadOnly get( Node n ) {
        PointsToSetInternal ret = (PointsToSetInternal) map.get(n);
        if( ret == null ) return EmptyPointsToSet.v();
        return ret;
    }
    public PointsToSetInternal make( Node n ) {
        PointsToSetInternal ret = (PointsToSetInternal) map.get(n);
        if( ret == null ) {
            map.put(n, ret = PaddleScene.v().setFactory.newSet( n.getType() ));
        }
        return ret;
    }
    public Set keySet() {
        return map.keySet();
    }
}

