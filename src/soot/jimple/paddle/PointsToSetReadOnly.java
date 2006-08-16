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

/** Abstract base class for implementations of points-to sets.
 * @author Ondrej Lhotak
 */
public abstract class PointsToSetReadOnly implements PointsToSet {
    /** Calls v's visit method on all nodes in this set. */
    public abstract boolean forall( P2SetVisitor v );
    /** Returns set of newly-added nodes since last call to flushNew. */
    public PointsToSetReadOnly getNewSet() { return this; }
    /** Returns set of nodes already present before last call to flushNew. */
    public PointsToSetReadOnly getOldSet() { return EmptyPointsToSet.v(); }
    /** Returns true iff the set contains n. */
    public abstract boolean contains( ContextAllocNode n );

    public PointsToSetReadOnly( Type type ) { this.type = type; }

    public boolean hasNonEmptyIntersection( PointsToSet other ) {
        final PointsToSetReadOnly o = (PointsToSetReadOnly) other;
        return forall( new P2SetVisitor() {
            public void visit( ContextAllocNode n ) {
                if( o.contains( n ) ) returnValue = true;
            }
        } );
    }
    public Set possibleTypes() {
        final HashSet ret = new HashSet();
        forall( new P2SetVisitor() {
            public void visit( ContextAllocNode n ) {
                Type t = n.getType();
                if( t instanceof RefType ) {
                    RefType rt = (RefType) t;
                    if( rt.getSootClass().isAbstract() ) return;
                }
                ret.add( t );
            }
        } );
        return ret;
    }
    public Type getType() {
        return type;
    }
    public int size() {
        final int[] ret = new int[1];
        forall( new P2SetVisitor() {
            public void visit( ContextAllocNode n ) {
                ret[0]++;
            }
        } );
        return ret[0];
    }
    public String toString() {
        final StringBuffer ret = new StringBuffer();
        this.forall( new P2SetVisitor() {
        public final void visit( ContextAllocNode n ) {
            ret.append( ""+n+"," );
        }} );
        return ret.toString();
    }

    public Set possibleStringConstants() { 
        final HashSet ret = new HashSet();
        return this.forall( new P2SetVisitor() {
        public final void visit( ContextAllocNode n ) {
            if( n.obj() instanceof StringConstantNode ) {
                ret.add( ((StringConstantNode)n.obj()).getString() );
            } else {
                returnValue = true;
            }
        }} ) ? null : ret;
    }
    public Set possibleClassConstants() {
    	return Collections.EMPTY_SET;
    }

    /* End of public methods. */
    /* End of package methods. */

    protected Type type;

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		P2SetVisitorInt visitor = new P2SetVisitorInt(1) {

			final int PRIME = 31;
			
			public void visit(ContextAllocNode n) {
				intValue = PRIME * intValue + n.hashCode(); 
			}
			
		};
		
		return visitor.intValue;
	}
	
	/**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
    	if(this==other) {
    		return true;
    	}
    	if(!(other instanceof PointsToSetInternal)) {
    		return false;
    	}
    	PointsToSetInternal otherPts = (PointsToSetInternal) other;
    	
    	//both sets are equal if they are supersets of each other 
    	return superSetOf(otherPts, this) && superSetOf(this, otherPts);
    	
    }
    
	/**
	 * Returns <code>true</code> if <code>onePts</code> is a (non-strict) superset of <code>otherPts</code>.
	 */
	private boolean superSetOf(PointsToSetReadOnly onePts, final PointsToSetReadOnly otherPts) {
		return onePts.forall(
    		new P2SetVisitorDefaultTrue() {
    			
    			public final void visit( ContextAllocNode n ) {
                    returnValue = returnValue && otherPts.contains(n);
                }
    			
            }
    	);
	}

	/**
	 * A P2SetVisitor with a default return value of <code>true</code>.
	 *
	 * @author Eric Bodden
	 */
	protected abstract class P2SetVisitorDefaultTrue extends P2SetVisitor {
		
		public P2SetVisitorDefaultTrue() {
			returnValue = true;
		}
		
	}
	
	/**
	 * A P2SetVisitor with an int value.
	 *
	 * @author Eric Bodden
	 */
	protected abstract class P2SetVisitorInt extends P2SetVisitor {
		
		int intValue;
		
		P2SetVisitorInt(int i) {
			intValue = 1;
		}
		
	}
}

