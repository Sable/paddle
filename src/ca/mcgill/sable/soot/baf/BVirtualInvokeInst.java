package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class BVirtualInvokeInst extends AbstractInvokeInst implements VirtualInvokeInst
{
    BVirtualInvokeInst(SootMethod method) { setMethod(method); }
    
    public int getInCount()
    {
        return getMethod().getParameterCount() + 1;
        
    }


    public int getInMachineCount()
    {
        return getMethod().getParameterCount() + 1;
        
    }


    public Object clone() 
    {
        return new  BVirtualInvokeInst(getMethod());
    }

    
    public int getOutCount()
    {
        if(getMethod().getReturnType() instanceof VoidType) 
            return 0;
        else
            return 1;
    }

    public int getOutMachineCount()
    {
        if(getMethod().getReturnType() instanceof VoidType) 
            return 0;
        else
            return 1;
    }

    final String getName() { return "virtualinvoke"; }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseVirtualInvokeInst(this);
    }   
}






