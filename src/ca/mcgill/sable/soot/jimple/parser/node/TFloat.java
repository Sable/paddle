package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TFloat extends Token
{
    public TFloat()
    {
        super.setText("float");
    }

    public TFloat(int line, int pos)
    {
        super.setText("float");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TFloat(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTFloat(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TFloat text.");
    }
}