/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

/** Implementation of an empty, immutable points-to set.
 * @author Ondrej Lhotak
 */
public class EmptyPointsToSet extends PointsToSetReadOnly {
    public EmptyPointsToSet( PaddleSingletons.Global g ) { super(null); }
    public static EmptyPointsToSet v() { return PaddleG.v().soot_jimple_paddle_EmptyPointsToSet(); }

    /** Returns true if this set contains no run-time objects. */
    public boolean isEmpty() { return true; }
    /** Returns true if this set shares some objects with other. */
    public boolean hasNonEmptyIntersection( PointsToSet other ) {
        return false;
    }
    /** Set of all possible run-time types of objects in the set. */
    public Set possibleTypes() { return Collections.EMPTY_SET; }
    /** Calls v's visit method on all nodes in this set. */
    public boolean forall( P2SetVisitor v ) {
        return false;
    }
    /** Returns true iff the set contains n. */
    public boolean contains( ContextAllocNode n ) {
        return false;
    }

    public Set possibleStringConstants() { return Collections.EMPTY_SET; }
    public Set possibleClassConstants() { return Collections.EMPTY_SET; }

}

