/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $JimpleVersion: 0.5 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on September 22, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Changed the base from Immediate to Local.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;

public class ArrayRef implements RValue, Variable, Switchable
{
    LocalBox baseBox;
    ImmediateBox indexBox; 
    
    List useBoxes;
    
    public ArrayRef(Local base, Immediate index)
    {
        this.baseBox = new LocalBox(base);
        this.indexBox = new ImmediateBox(index);
        
        useBoxes = new ArrayList();
        useBoxes.add(baseBox);
        useBoxes.add(indexBox);
        useBoxes = Collections.unmodifiableList(useBoxes);
    }   
    
    public String toString()
    {
        return baseBox.getValue().toString() + "[" + indexBox.getValue().toString() + "]";
    }
    
    public Local getBase()
    {
        return (Local) baseBox.getValue();
    }
    
    public void setBase(Local base)
    {
        baseBox.setValue(base);
    }
    
    public LocalBox getBaseBox()
    {
        return baseBox;
    }
    
    public Immediate getIndex()
    {
        return (Immediate) indexBox.getValue();
    } 
    
    public void setIndex(Immediate index)
    {
        indexBox.setValue(index);
    }
    
    public ImmediateBox getIndexBox()
    {
        return indexBox;
    }
    
    public List getUseBoxes()
    {
        return useBoxes;
    }
    
    public Type getType()
    {   
        Local base = (Local) baseBox.getValue();
        Type type = base.getType();
        
        if(type.equals(UnknownType.v()))
            return UnknownType.v();
        else {
            ArrayType arrayType = (ArrayType) type;
            
            if(arrayType.numDimensions == 1)
                return arrayType.baseType;
            else
                return ArrayType.v(arrayType.baseType, arrayType.numDimensions - 1);
        }
    }
    
    public void apply(Switch sw)
    {
        ((ValueSwitch) sw).caseArrayRef(this);
    }
}


