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

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Renamed the uses of Hashtable to HashMap.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;

public class LiveLocals
{
    Map stmtToLocals;
    //Map stmtToLocalsBefore;
    
    public LiveLocals(StmtGraphBody graphBody)
    {
        LiveLocalsAnalysis analysis = new LiveLocalsAnalysis(graphBody);  
        StmtGraph graph = graphBody.getStmtGraph(); 
        
        // Build stmtToLocals map
        {
            stmtToLocals = new HashMap(graph.size() * 2 + 1, 0.7f);
            
            Iterator stmtIt = graph.iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                FlowSet set = (FlowSet) analysis.getFlowBeforeStmt(s);
                stmtToLocals.put(s, Collections.unmodifiableList(set.toList()));
                
            }        
        }  
        
    }
    
    /*
    public List getLiveLocalsBefore(Stmt s)
    {
        FSet set = (FSet) analysis.getValueBeforeStmt(s);

        return set.toList();
    }
      */
        
    public List getLiveLocalsAfter(Stmt s)
    {
        return (List) stmtToLocals.get(s);
    }
}

class LiveLocalsAnalysis extends BackwardFlowAnalysis
{
    FlowSet emptySet;
    Map stmtToGenerateSet;
    Map stmtToPreserveSet;
    
    LiveLocalsAnalysis(StmtGraphBody graphBody)
    {
        super(graphBody.getStmtGraph());
        
        StmtGraph g = graphBody.getStmtGraph();
        
        // Generate list of locals and empty set
        {
            List locals = graphBody.getLocals();
            FlowUniverse localUniverse = new FlowUniverse(locals.toArray());
            
            emptySet = PackSet.v(localUniverse);
        }
                   
        // Create preserve sets.
        {
            stmtToPreserveSet = new HashMap(g.size() * 2 + 1, 0.7f);
            
            Iterator stmtIt = g.iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                FlowSet killSet = (FlowSet) emptySet.clone();
                
                Iterator boxIt = s.getDefBoxes().iterator();
                
                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();
                    
                    if(box.getValue() instanceof Local)
                        killSet.add(box.getValue(), killSet);
                }
                
                // Store complement           
                    killSet.complement(killSet);       
                    stmtToPreserveSet.put(s, killSet);
            }
        }
            
        // Create generate sets
        {
            stmtToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);
            
            Iterator stmtIt = g.iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                FlowSet genSet = (FlowSet) emptySet.clone();
                
                Iterator boxIt = s.getUseBoxes().iterator();
                
                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();
                    
                    if(box.getValue() instanceof Local)
                        genSet.add(box.getValue(), genSet);
                }
                        
                stmtToGenerateSet.put(s, genSet);
            }
        }
        
        doAnalysis();
    }
    
    protected Flow getInitialFlow()
    {
        return emptySet;
    }
    
    protected void flowThrough(Flow inValue, Stmt stmt, Flow outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Perform kill
            in.intersection((FlowSet) stmtToPreserveSet.get(stmt), out);
        
        // Perform generation
            out.union((FlowSet) stmtToGenerateSet.get(stmt), out);        
    }
    
    protected void merge(Flow in1, Flow in2, Flow out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;
            
        FlowSet outSet = (FlowSet) out;
        
        inSet1.union(inSet2, outSet);
    }
}