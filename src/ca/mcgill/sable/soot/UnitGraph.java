package ca.mcgill.sable.soot;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
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

 - Modified on March 24, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Add some edges to the flow graph regarding exceptions.
 
 - Modified on March 15, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Added a pseudo topological order iterator (and its reverse).
   Moved in Patrick's getPath code.
   
 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Re-organized the timers.

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on September 22, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Added support for exception edge inclusion.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Many changes.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/



import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class UnitGraph implements DirectedGraph
{
    List heads;
    List tails;

    private Map unitToSuccs;
    private Map unitToPreds;        
    SootMethod method;
    int size;


    UnitBody body;
    Chain unitChain;
    

    public UnitBody getBody()
    {
        return body;
    }

    UnitGraph(UnitBody unitBody, boolean addExceptionEdges)
    {
        body = unitBody;
        unitChain = body.getUnits();
        method = getBody().getMethod();
        
        if(Main.isVerbose)
            System.out.println("[" + method.getName() + 
                               "]     Constructing StmtGraph...");
      
        if(Main.isProfilingOptimization)
            Main.graphTimer.start();
      
        
        // Build successors
        {
            Map classToHandler = new HashMap(); // list of exceptions being caught, and their handlers
            
            unitToSuccs = new HashMap(size * 2 + 1, 0.7f);
            unitToPreds = new HashMap(size * 2 + 1, 0.7f);


            // Add regular successors
            {
                Iterator unitIt = unitChain.iterator();
                Unit currentUnit, nextUnit;
                
                nextUnit = unitIt.hasNext() ? (Unit) unitIt.next(): null;
                
                while(nextUnit != null) {
                    currentUnit = nextUnit;
                    nextUnit = unitIt.hasNext() ? (Unit) unitIt.next(): null;
                    
                    List successors = new ArrayList();
                    
                    
                    // Put the next statement as the successor
                    if( currentUnit.fallsThrough() ) {
                        if(nextUnit != null)
                            successors.add(nextUnit);
                        else
                            throw new RuntimeException("last unit of a method should not fall through");
                    }
                        
                    if( currentUnit.branches() ) {
                        Iterator targetIt = currentUnit.getUnitBoxes().iterator();
                        while(targetIt.hasNext()) {
                            successors.add(((UnitBox) targetIt.next()).getUnit());
                        }
                    }
                    

                    // Store away successors
                    unitToSuccs.put(currentUnit, successors);
                }
            }

            // Add exception based successors
            if(addExceptionEdges)
                {
                    Map beginToHandler = new HashMap();
                    
                    Iterator trapIt = body.getTraps().iterator();
                    
                    while(trapIt.hasNext())
                    {
                        Trap trap = (Trap) trapIt.next();

                        Unit beginUnit = (Unit) trap.getBeginUnit();
                        Unit handlerUnit = (Unit) trap.getHandlerUnit();
                        Unit endUnit = (Unit) trap.getEndUnit();
                        Iterator unitIt = unitChain.iterator(beginUnit);
                        
                        beginToHandler.put(beginUnit, handlerUnit);
                        
                        Unit u;
                        do  {
                            u = (Unit) unitIt.next();
                            
                            ((List) unitToSuccs.get(u)).add(handlerUnit);
                            
                        } while(u != endUnit);
                    
                    }
                    
                    // Add edges from the predecessors of begin statements directly to the handlers
                    // This is necessary because sometimes the first statement of try block
                    // is not even fully executed before an exception is thrown
                    {
                        Iterator unitIt = body.getUnits().iterator();
                        
                        while(unitIt.hasNext())
                        {
                            Unit u = (Unit) unitIt.next();
                            
                            List succs = ((List) unitToSuccs.get(u));
                            
                            List succsClone = new ArrayList();
                            succsClone.addAll(succs);
                                // need to clone it because you are potentially 
                                // modifying it
                                
                            Iterator succIt = succsClone.iterator();
                                
                            while(succIt.hasNext())
                            {
                                Unit succ = (Unit) succIt.next();
                                
                                if(beginToHandler.containsKey(succ))
                                {
                                    // Add an edge from s to succ
                                    
                                    Unit handler = (Unit) beginToHandler.get(succ);
                                    
                                    if(!succs.contains(handler))
                                        succs.add(handler);
                                }
                            }
                        }
                    }
                }

            // Make successors unmodifiable
            {
                Iterator unitIt = body.getUnits().iterator();
                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();
        
                    unitToSuccs.put(s, Collections.unmodifiableList((List) unitToSuccs.get(s)));
                }
            }
        }


        // Build predecessors
        {
            Map stmtToPredList = new HashMap(size * 2 + 1, 0.7f);

            // initialize the pred sets to empty
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    stmtToPredList.put(unitIt.next(), new ArrayList());
                }
            }

            // Modify preds set for each successor for this statement
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();
                    Iterator succIt = ((List) unitToSuccs.get(s)).iterator();

                    while(succIt.hasNext())
                    {
                        List predList = (List) stmtToPredList.get(succIt.next());
                        predList.add(s);
                    }
                }
            }


            // Convert pred lists to arrays
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();

                    List predList = (List) stmtToPredList.get(s);
                    unitToPreds.put(s, Collections.unmodifiableList(predList));
                }
            }

        }

        // Build tails
        {
            List tailList = new ArrayList();

            // Build the set
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();

                    List succs = (List) unitToSuccs.get(s);

                    if(succs.size() == 0)
                        tailList.add(s);
                }
            }

            tails = Collections.unmodifiableList(tailList);
        }

        // Build heads
        {
            List headList = new ArrayList();

            // Build the set
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();
                    List preds = (List) unitToPreds.get(s);

                    if(preds.size() == 0)
                        headList.add(s);
                }
            }

            heads = Collections.unmodifiableList(headList);
        }

        if(Main.isProfilingOptimization)
            Main.graphTimer.end();
    }

    public List getHeads()
    {
        return heads;
    }

    public List getTails()
    {
        return tails;
    }

    public List getPredsOf(Unit s)
    {
        if(!unitToPreds.containsKey(s))
            throw new RuntimeException("Invalid stmt" + s);

        return (List) unitToPreds.get(s);
    }

    public List getSuccsOf(Unit s)
    {
        if(!unitToSuccs.containsKey(s))
            throw new RuntimeException("Invalid stmt" + s);

        return (List) unitToSuccs.get(s);
    }

    public Iterator iterator()
    {
        return unitChain.iterator();
    }

    public int size()
    {
        return size;
    }  
    
}























