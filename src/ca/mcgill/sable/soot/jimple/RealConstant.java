package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public abstract class RealConstant extends NumericConstant
{
    // PTC 1999/06/28
    public abstract IntConstant cmpl(RealConstant c);

    public abstract IntConstant cmpg(RealConstant c);
}