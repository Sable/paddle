/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *       based on FastAvailableExpressionsAnalysis from Patrick Lam.
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
 * Modified by the Sable Research Group and others 1997-2002.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.scalar.pre;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** 
 * Performs an UpSafe-analysis on the given graph.<br>
 * An expression is upsafe, if the computation already has been performed on
 * every path from START to the given program-point.<br>
 */
public class UpSafetyAnalysis extends ForwardFlowAnalysis {
  private SideEffectTester sideEffect;

  private Map unitToGenerateMap;

  private FlowSet emptySet;

  /**
   * this constructor should not be used, and will throw a runtime-exception!
   */
  public UpSafetyAnalysis(DirectedGraph dg) {
    /* we have to add super(dg). otherwise Javac complains. */
    super(dg);
    throw new RuntimeException("Don't use this Constructor!");
  }

  /**
   * this constructor automaticly performs the UpSafety-analysis.<br>
   * the result of the analysis is as usual in FlowBefore (getFlowBefore())
   * and FlowAfter (getFlowAfter()).<br>
   * the naive side-effect tester is used to perform kills.
   *
   * @param dg a CompleteUnitGraph
   * @param unitToGen the EquivalentValue of each unit.
   */
  public UpSafetyAnalysis(DirectedGraph dg, Map unitToGen) {
    this(dg, unitToGen, new NaiveSideEffectTester());
  }

  /**
   * this constructor automaticly performs the UpSafety-analysis.<br>
   * the result of the analysis is as usual in FlowBefore (getFlowBefore())
   * and FlowAfter (getFlowAfter()).<br>
   *
   * @param dg a CompleteUnitGraph
   * @param unitToGen the EquivalentValue of each unit.
   * @param sideEffect the SideEffectTester that will be used to perform kills.
   */
  public UpSafetyAnalysis(DirectedGraph dg, Map unitToGen, SideEffectTester
			  sideEffect) {
    super(dg);
    this.sideEffect = sideEffect;
    UnitGraph g = (UnitGraph)dg;
    emptySet = new ToppedSet(new ArraySparseSet());
    unitToGenerateMap = unitToGen;
    doAnalysis();
  }

  protected Object newInitialFlow() {
    Object newSet = emptySet.clone();
    ((ToppedSet)newSet).setTop(true);
    return newSet;
  }

  protected void customizeInitialFlowGraph() {
    // Initialize heads to {}
    Iterator headIt = graph.getHeads().iterator();
    while (headIt.hasNext()) {
      Object newSet = unitToBeforeFlow.get(headIt.next());
      ((ToppedSet)newSet).setTop(false);
    }
  }

  protected void flowThrough(Object inValue, Object unit, Object outValue) {
    FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

    in.copy(out);
    if (((ToppedSet)in).isTop())
      return;

    // Perform generation
    Value add = (Value)unitToGenerateMap.get(unit);
    if (add != null)
      out.add(add, out);

    /* should not be possible */
    if (((ToppedSet)out).isTop()) {
      throw new RuntimeException("trying to kill on topped set!");
    }

    { /* Perform kill */
      Unit u = (Unit)unit;

      /* simulate a snapshotIterator, as we modify the underlying set */
      List outList = new LinkedList();
      outList.addAll(((FlowSet)out).toList());
      Iterator outIt = outList.iterator();

      // iterate over things (avail) in out set.
      while (outIt.hasNext()) {
        EquivalentValue equiVal = (EquivalentValue)outIt.next();
        Value avail = equiVal.getValue();
        if (avail instanceof FieldRef) {
          if (sideEffect.unitCanWriteTo(u, avail))
            out.remove(equiVal, out);
        } else {
          Iterator usesIt = avail.getUseBoxes().iterator();

          // iterate over uses in each avail.
          while (usesIt.hasNext()) {
            Value use = ((ValueBox)usesIt.next()).getValue();

            if (sideEffect.unitCanWriteTo(u, use))
              out.remove(equiVal, out);
          }
        }
      }
    }
  }

  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = (FlowSet) in1;
    FlowSet inSet2 = (FlowSet) in2;

    FlowSet outSet = (FlowSet) out;

    inSet1.intersection(inSet2, outSet);
  }

  protected void copy(Object source, Object dest) {
    FlowSet sourceSet = (FlowSet) source;
    FlowSet destSet = (FlowSet) dest;

    sourceSet.copy(destSet);
  }
}
