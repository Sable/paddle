/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Baf, a Java(TM) bytecode analyzer framework.                      *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
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
 The reference version is: $BafVersion: 0.4 $

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

 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Changed caseDefault to defaultCase, to avoid name conflicts (and conform
   to the standard).
   Added caseNullType.
   Added caseErroneousType.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
 package ca.mcgill.sable.soot.baf;

public class TypeSwitch implements ca.mcgill.sable.util.Switch 
{
    Object result;
    
    public void caseArrayType(ArrayType t)
    {
        defaultCase(t);
    }
    
    public void caseBooleanType(BooleanType t)
    {
        defaultCase(t);
    }
    
    public void caseByteType(ByteType t)
    {
        defaultCase(t);
    }
    
    public void caseCharType(CharType t)
    {
        defaultCase(t);
    }
    
    public void caseDoubleType(DoubleType t)
    {
        defaultCase(t);
    }
    
    public void caseFloatType(FloatType t)
    {
        defaultCase(t);
    }
    
    public void caseIntType(IntType t)
    {
        defaultCase(t);
    }
    
    public void caseLongType(LongType t)
    {
        defaultCase(t);
    }
    
    public void caseRefType(RefType t)
    {
        defaultCase(t);
    }
    
    public void caseShortType(ShortType t)
    {
        defaultCase(t);
    }
    
    public void caseStmtAddressType(StmtAddressType t)
    {
        defaultCase(t);
    }
    
    public void caseUnknownType(UnknownType t)
    {
        defaultCase(t);
    }
    
    public void caseVoidType(VoidType t)
    {
        defaultCase(t);
    }

    public void caseNullType(NullType t)
    {
        defaultCase(t);
    }
    
    public void caseErroneousType(ErroneousType t)
    {
        defaultCase(t);
    }
    
    public void defaultCase(Type t)
    {
        
    }
        
    /** @deprecated Replaced by defaultCase
        @see defaultCase(Type) **/
    public void caseDefault(Type t)
    {
    }
      
    public void setResult(Object result)
    {
        this.result = result;
    }
    
    public Object getResult()
    {
        return this.result;
    }
}