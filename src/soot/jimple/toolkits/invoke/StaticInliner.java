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
import soot.toolkits.graph.*;
import java.util.*;
import soot.util.*;

public class StaticInliner extends SceneTransformer
{
    private static StaticInliner instance = new StaticInliner();
    private StaticInliner() {}

    public static StaticInliner v() { return instance; }

    public String getDefaultOptions() 
    {
        return "insert-null-checks insert-redundant-casts allowed-modifier-changes:unsafe";
    }
    
    protected void internalTransform(String phaseName, Map options)
    {
        InvokeGraphBuilder.v().transform(phaseName + ".igb");

        boolean enableNullPointerCheckInsertion = Options.getBoolean(options, "insert-null-checks");
        boolean enableRedundantCastInsertion = Options.getBoolean(options, "insert-redundant-casts");
        String modifierOptions = Options.getString(options, "allowed-modifier-changes");

        HashMap instanceToStaticMap = new HashMap();

        InvokeGraph graph = Scene.v().getActiveInvokeGraph();
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();

        DirectedGraph mg = graph.newMethodGraph();
        ArrayList sitesToInline = new ArrayList();

        Iterator it = ReversePseudoTopologicalOrderer.v().newList(mg).iterator();

        while (it.hasNext())
        {
            SootMethod container = (SootMethod)it.next();

            if (!container.isConcrete())
                continue;

            if (graph.getSitesOf(container).size() == 0)
                continue;

            JimpleBody b = (JimpleBody)container.getActiveBody();
                
            List unitList = new ArrayList(); unitList.addAll(b.getUnits());
            Iterator unitIt = unitList.iterator();

            while (unitIt.hasNext())
            {
                Stmt s = (Stmt)unitIt.next();
                if (!s.containsInvokeExpr())
                    continue;
                
                InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();

                List targets = graph.getTargetsOf(ie);

                if (targets.size() != 1)
                    continue;

                SootMethod target = (SootMethod)targets.get(0);

                if (!AccessManager.ensureAccess(container, target, modifierOptions))
                    continue;

                if (!target.getDeclaringClass().isApplicationClass() || !target.isConcrete())
                    continue;

                List l = new ArrayList();
                l.add(target); l.add(s); l.add(container);
                sitesToInline.add(l);
            }
        }

        SiteInliner.inlineSites(sitesToInline, options);
    }
}
