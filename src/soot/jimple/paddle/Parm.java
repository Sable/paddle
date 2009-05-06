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
import java.util.*;
import soot.*;
import soot.toolkits.scalar.Pair;

/** Represents a method parameter.
 * @author Ondrej Lhotak
 */
public class Parm implements PaddleField {
    private int index;
    private SootMethod method;
    private Parm( SootMethod m, int i ) {
        index = i;
        method = m;
        Scene.v().getFieldNumberer().add(this);
    }
    public static Parm v( SootMethod m, int index ) {
        Pair p = new Pair( m, new Integer(index) );
        Parm ret = (Parm) G.v().Parm_pairToElement.get( p );
        if( ret == null ) {
            G.v().Parm_pairToElement.put( p, ret = new Parm( m, index ) );
        }
        return ret;
    }
    public static final void delete() {
        G.v().Parm_pairToElement = null;
    }
    public String toString() {
        return "Parm "+index+" to "+method;
    }

    public final int getNumber() {
        return number;
    }
    public final void setNumber(int number) {
        this.number = number;
    }
    private int number = 0;

    public int getIndex() {
        return index;
    }

    public SootMethod getMethod() {
        return method;
    }
}
