package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TCmpgt extends Token
{
    public TCmpgt()
    {
        super.setText(">");
    }

    public TCmpgt(int line, int pos)
    {
        super.setText(">");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TCmpgt(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCmpgt(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TCmpgt text.");
    }
}