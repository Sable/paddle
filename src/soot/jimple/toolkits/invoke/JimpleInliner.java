/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.invoke;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.scalar.*;
import java.util.*;
import soot.util.*;

public class JimpleInliner
{
    public static void inlineAll(Body b)
    {
        Chain units = b.getUnits();
        ArrayList unitsList = new ArrayList(); unitsList.addAll(units);
        Iterator it = unitsList.iterator();

        while (it.hasNext())
        {
            Stmt s = (Stmt)it.next();
            if (s instanceof InvokeStmt)
            {
                InvokeExpr ie = (InvokeExpr)((InvokeStmt)s).getInvokeExpr();
                if (ie instanceof StaticInvokeExpr || ie instanceof VirtualInvokeExpr)
                    inlineInvoke(ie.getMethod(), s, b.getMethod());
            }
        }
    }

    public static void inlineInvoke(SootMethod inlinee, Stmt toInline, 
                                    SootMethod container)
    {
        // DEBUG
//          System.out.println("inlining: "+inlinee + " into "+container);

        Body containerB = (JimpleBody)container.getActiveBody();
        Chain containerUnits = containerB.getUnits();

        if (!(inlinee.getDeclaringClass().isApplicationClass() ||
              inlinee.getDeclaringClass().isLibraryClass()))
            return;

        Body inlineeB = (JimpleBody)inlinee.getActiveBody();
        Chain inlineeUnits = inlineeB.getUnits();

        Stmt exitPoint = (Stmt)containerUnits.getSuccOf(toInline);

        HashMap oldLocalsToNew = new HashMap();
        HashMap oldUnitsToNew = new HashMap();

        // First, clone all of the inlinee's units & locals.
        {
            Stmt cursor = toInline;
            for (Iterator it = inlineeUnits.iterator();
                 it.hasNext(); )
            {
                Stmt curr = (Stmt)it.next();
                Stmt currPrime = (Stmt)curr.clone();

                containerUnits.insertAfter(currPrime, cursor);
                cursor = currPrime;

                oldUnitsToNew.put(curr, currPrime);
            }

            for (Iterator it = inlineeB.getLocals().iterator(); 
                 it.hasNext(); )
            {
                Local l = (Local)it.next();
                Local lPrime = (Local)l.clone();

                containerB.getLocals().add(lPrime);
                oldLocalsToNew.put(l, lPrime);
            }
        }

        // Backpatch the newly-inserted units using newly-constructed maps.
        {
            Iterator it = containerUnits.iterator
                (containerUnits.getSuccOf(toInline), 
                 containerUnits.getPredOf(exitPoint));

            while (it.hasNext())
            {
                Stmt patchee = (Stmt)it.next();

                Iterator duBoxes = patchee.getUseAndDefBoxes().iterator();
                while (duBoxes.hasNext())
                {
                    ValueBox box = (ValueBox)duBoxes.next();
                    Local lPrime = (Local)(oldLocalsToNew.get(box.getValue()));
                    if (lPrime != null)
                        box.setValue(lPrime);
                }

                Iterator unitBoxes = patchee.getUnitBoxes().iterator();
                while (unitBoxes.hasNext())
                {
                    UnitBox box = (UnitBox)unitBoxes.next();
                    Unit uPrime = (Unit)(oldUnitsToNew.get(box.getUnit()));
                    if (uPrime != null)
                        box.setUnit(uPrime);
                    else
                        throw new RuntimeException("inlined stmt has no clone!");
                }                
            }
        }

        // Handle identity stmt's and returns.
        {
            Iterator it = containerUnits.iterator
                (containerUnits.getSuccOf(toInline), 
                 containerUnits.getPredOf(exitPoint));
            ArrayList cuCopy = new ArrayList();

            while (it.hasNext())
            {
                cuCopy.add(it.next());
            }

            it = cuCopy.iterator();
            while (it.hasNext())
            {
                Stmt s = (Stmt)it.next();

                if (s instanceof IdentityStmt)
                {
                    IdentityRef rhs = (IdentityRef)((IdentityStmt)s).getRightOp();
                    if (rhs instanceof CaughtExceptionRef)
                        continue;
                    else if (rhs instanceof ThisRef)
                    {
                        InvokeExpr ie = (InvokeExpr)toInline.getInvokeExpr();
                        if (!(ie instanceof InstanceInvokeExpr))
                            throw new RuntimeException("thisref with no receiver!");

                        containerUnits.swapWith(s, Jimple.v().newAssignStmt(((IdentityStmt)s).getLeftOp(),
                                                                            ((InstanceInvokeExpr)ie).getBase()));
                    }
                    else if (rhs instanceof ParameterRef)
                    {
                        ParameterRef pref = (ParameterRef)rhs;
                        InvokeExpr ie = (InvokeExpr)toInline.getInvokeExpr();
                        containerUnits.swapWith(s, Jimple.v().newAssignStmt(((IdentityStmt)s).getLeftOp(),
                                                                            ie.getArg(pref.getIndex())));
                    }
                }
                else if (s instanceof ReturnStmt)
                {
                    if (toInline instanceof InvokeStmt)
                    {
                        // munch, munch.
                        containerUnits.swapWith(s, Jimple.v().newGotoStmt(exitPoint));
                        continue;
                    }

                    if (!(toInline instanceof AssignStmt))
                        throw new RuntimeException
                            ("invoking stmt neither InvokeStmt nor AssignStmt!??!?!");
                    Value ro = ((ReturnStmt)s).getOp();
                    Value lhs = ((AssignStmt)toInline).getLeftOp();
                    AssignStmt as = Jimple.v().newAssignStmt(lhs, ro);
                    containerUnits.insertBefore(as, s);
                    containerUnits.swapWith(s, Jimple.v().newGotoStmt(exitPoint));
                }
                else if (s instanceof ReturnVoidStmt)
                    containerUnits.swapWith(s, Jimple.v().newGotoStmt(exitPoint));
            }
        }

        // Copy the traps.
        {
            Iterator trapsIt = inlineeB.getTraps().iterator();

            while (trapsIt.hasNext())
            {
                Trap t = (Trap)trapsIt.next();

                containerB.getTraps().addFirst(Jimple.v().newTrap
                                               (t.getException(),
                                                (Stmt)oldUnitsToNew.get(t.getBeginUnit()),
                                                (Stmt)oldUnitsToNew.get(t.getEndUnit()),
                                                (Stmt)oldUnitsToNew.get(t.getHandlerUnit())));
            }
        }

        // Remove the original statement toInline.
        containerUnits.remove(toInline);

        // Resolve name collisions.
        LocalNameStandardizer.v().transform(containerB, "ji.lns");
    }
}