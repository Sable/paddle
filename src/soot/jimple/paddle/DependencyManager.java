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
import soot.jimple.paddle.queue.*;
import java.util.*;
import soot.util.*;

/** This class keeps track of dependencies between queues and Paddle modules,
 * and schedules which modules need to be updated.
 * @author Ondrej Lhotak
 */
public class DependencyManager 
{ 
    private static final boolean DEBUG = true;
    private Map countMap = new HashMap();
    private Set qworklist = new HashSet();
    private Heap worklist = new Heap(new Heap.Keys() {
        public int key(Object o) {
            Integer i = (Integer) countMap.get(o);
            if(i == null) return 0;
            return i.intValue();
        }
    });
    private MultiMap qdeps = new HashMultiMap();
    private MultiMap deps = new HashMultiMap();
    private MultiMap precs = new HashMultiMap();
    public DependencyManager() {
    }
    public void invalidate( DepItem item ) {
        if( item instanceof PaddleComponent ) {
            worklist.add(item);
        } else {
            qworklist.add(item);
        }
    }
    private boolean incflow = false;
    public void update() {
        if(incflow) return;
        incflow = true;
worklist:
        while( true ) {
            for( Iterator qIt = qworklist.iterator(); qIt.hasNext(); ) {
                final PaddleQueue q = (PaddleQueue) qIt.next();
                for( Iterator depIt = qdeps.get(q).iterator(); depIt.hasNext(); ) {
                    final PaddleComponent dep = (PaddleComponent) depIt.next();
                    invalidate(dep);
                }
            }
            qworklist = new HashSet();
            if(worklist.isEmpty()) break;
            PaddleComponent item = (PaddleComponent) worklist.removeMin();
            increment(item);
            if( !checkPrec(item) ) {
                if(DEBUG) System.out.println( "not updating "+item.getClass() );
                invalidate(item);
            } else {
                if(DEBUG) System.out.println( "updating "+item.getClass() );
                if( item.update() ) {
                    for( Iterator depIt = deps.get(item).iterator(); depIt.hasNext(); ) {
                        final PaddleComponent dep = (PaddleComponent) depIt.next();
                        invalidate(dep);
                    }
                }
                if(DEBUG) System.out.println( "done updating "+item.getClass() );
            }
        }
        incflow = false;
    }
    public boolean checkPrec( PaddleComponent item ) {
        for( Iterator precIt = precs.get(item).iterator(); precIt.hasNext(); ) {
            final PaddleComponent prec = (PaddleComponent) precIt.next();
            if( worklist.contains(prec) ) {
                return false;
            }
        }
        return true;
    }
    public void addQueueDep( PaddleQueueReader from, PaddleComponent to ) {
        addQueueDep(from.queue(), to);
    }
    public void addQueueDep( PaddleQueue from, PaddleComponent to ) {
        from.addDepMan(this);
        qdeps.put(from, to);
    }
    public void addDep( PaddleComponent from, PaddleComponent to ) {
        deps.put(from, to);
    }
    public void addPrec( PaddleComponent from, PaddleComponent to ) {
        precs.put(from, to);
    }
    private void increment(DepItem item) {
        Integer i = (Integer) countMap.get(item);
        if( i == null ) {
            i = new Integer(1);
        } else {
            i = new Integer(i.intValue()+1);
        }
        countMap.put(item, i);
    }
}
