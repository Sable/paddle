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
import soot.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

/** Implementation of Soot PointsToAnalysis interface.
 * @author Ondrej Lhotak
 */
public class TradPointsToAnalysis extends AbsPointsToAnalysis
{ 
    public TradPointsToAnalysis(AbsP2Sets p2sets) {
        super(p2sets);
    }

    public PointsToSet reachingObjects( Local l ) {
        if(local(l) == null) return EmptyPointsToSet.v();
        PointsToSetInternal ret = PaddleScene.v().setFactory.newSet(null);
        for( Iterator cvnIt = local(l).contexts(); cvnIt.hasNext(); ) {
            final ContextVarNode cvn = (ContextVarNode) cvnIt.next();
            ret.addAll(p2sets.get(cvn), null);
        }
        return ret;
    }
    public PointsToSet reachingObjects( Context c, Local l ) {
        if(local(l) == null) return EmptyPointsToSet.v();
        return p2sets.get(c, local(l));
    }
    public PointsToSet reachingObjects( SootField f ) {
        if(field(f) == null) return EmptyPointsToSet.v();
        return p2sets.get(null, field(f));
    }
    public PointsToSet reachingObjects( PointsToSet s, final SootField f ) {
        final PointsToSetInternal ret = PaddleScene.v().setFactory.newSet(null);
        PointsToSetReadOnly sint = (PointsToSetReadOnly) s;
        sint.forall( new P2SetVisitor() {
        public final void visit( ContextAllocNode n ) {
            ContextAllocNode can = (ContextAllocNode) n;
            ret.addAll( p2sets.get(ContextAllocDotField.make(can, f)), null );
        }} );
        return ret;
    }
    public PointsToSet reachingObjects( Local l, SootField f ) {
        PointsToSetReadOnly base = (PointsToSetReadOnly) reachingObjects(l);
        return reachingObjects(base, f);
    }
    public PointsToSet reachingObjects( Context c, Local l, SootField f ) {
        PointsToSetReadOnly base = (PointsToSetReadOnly) reachingObjects(c, l);
        return reachingObjects(base, f);
    }
    public PointsToSet reachingObjectsOfArrayElement( PointsToSet s ) {
        final PointsToSetInternal ret = PaddleScene.v().setFactory.newSet(null);
        PointsToSetInternal sint = (PointsToSetInternal) s;
        sint.forall( new P2SetVisitor() {
        public final void visit( ContextAllocNode n ) {
            ContextAllocNode can = (ContextAllocNode) n;
            ret.addAll(p2sets.get(ContextAllocDotField.make(can, ArrayElement.v())), null );
        }} );
        return ret;
    }
}

