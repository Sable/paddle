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
import java.util.*;
import soot.*;
import soot.util.*;

/** Assigns a unique integer to a ContextString.
 * @author Ondrej Lhotak
 */
public class ContextStringNumberer implements Numberer
{ 
    public final int shiftWidth;
    private final long maxItem;
    private Numberer contextNumberer;
    private final int k;
    public ContextStringNumberer( jedd.Domain domain, Numberer contextNumberer, int k ) {
        this.contextNumberer = contextNumberer;
        this.k = k;
        shiftWidth = domain.maxUsefulBit();
        maxItem = 1L<<shiftWidth;

        if(k*shiftWidth > 64) throw new RuntimeException("Domain cannot be "+k*shiftWidth+" > 64 bits.");
        usefulBits = new boolean[k*shiftWidth];
        for(int i = 0; i < usefulBits.length; i++) {
            usefulBits[i] = true;
        }
    }
    public void add( Object o ) {
    }
    public long get( Object o ) {
        ContextString cs = (ContextString) o;
        if( cs == null ) {
            cs = new ContextString(k);
            for( int i = 0; i < k; i++) cs.push(null);
        }
        int ret = 0;
        for( int i = k-1; i >= 0; i-- ) {
            long num = contextNumberer.get(cs.get(i));
            if( num >= maxItem ) throw new RuntimeException( "Need to increase shiftWidth" );
            ret <<= shiftWidth;
            ret += num;
        }
        return ret;
    }
    public Object get( long num ) {
        Context[] ret = new Context[k];
        for( int i = 0; i < k; i++ ) {
            ret[i] = (Context) contextNumberer.get(num & maxItem-1);
            num >>>= shiftWidth;
        }
        for( int i = 0; i < k; i++) {
            if(ret[i] != null) return new ContextString(ret);
        }
        return null;
    }
    public int size() {
        int ret = 1;
        for( int i = 0; i < k; i++ ) {
            ret *= contextNumberer.size();
        }
        return ret;
    }
    private boolean[] usefulBits;
    public boolean[] usefulBits() {
        return usefulBits;
    }
}

