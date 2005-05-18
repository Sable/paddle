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
import java.util.*;
import soot.*;
import soot.util.*;

/** Assigns a unique integer to an AllocNode. AllocNodes of the same type
 * have their initial bits equal; the final objBits bits distinguish
 * AllocNodes with the same type.
 * @author Ondrej Lhotak
 */
public class ObjNumberer implements IterableNumberer
{ 
    public static final int objBits = 5;
    public static final long objMax = 1L<<objBits;
    public Map numToAlloc = new HashMap();
    public Map allocToNum = new HashMap();
    public Map typeToTypeEntry = new HashMap();
    public ArrayList allocs = new ArrayList();
    class TypeEntry {
        long prefix; // the type portion of the number
        long nextSuffix; // the node portion of the number
    }
    private long nextPrefix = 0;

    public ObjNumberer() {
        add(null);
    }
    public void add( Object o ) {
        if(allocToNum.containsKey(o)) return;
        allocs.add(o);
        AllocNode an = (AllocNode) o;
        Type t = (an==null) ? null : an.getType();
        TypeEntry te = (TypeEntry) typeToTypeEntry.get(t);
        if(te == null) {
            te = new TypeEntry();
            te.prefix = nextPrefix;
            nextPrefix += objMax;
            typeToTypeEntry.put(t, te);
        }
        long num = te.prefix + te.nextSuffix++;
        if(te.nextSuffix >= objMax) {
            te.prefix = nextPrefix;
            nextPrefix += objMax;
            te.nextSuffix = 0;
        }
        Long lnum = new Long(num);
        numToAlloc.put(lnum, an);
        allocToNum.put(an, lnum);
    }
    public long get( Object o ) {
        Long ret = (Long) allocToNum.get(o);
        if(ret == null) throw new RuntimeException("unnumbered: "+o);
        return ret.longValue();
    }
    public Object get( long num ) {
        if(num == 0L) return null;
        Object ret = numToAlloc.get( new Long(num) );
        if( ret == null ) throw new RuntimeException( "no object with number "+num );
        return ret;
    }
    public int size() {
        return numToAlloc.size();
    }
    public Iterator iterator() {
        return new NumbererIterator();
    }

    final class NumbererIterator implements Iterator {
        int cur = 0;
        public final boolean hasNext() {
            return cur < allocs.size();
        }
        public final Object next() { 
            if( !hasNext() ) throw new NoSuchElementException();
            return allocs.get(cur++);
        }
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

