package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TNeg extends Token
{
    public TNeg()
    {
        super.setText("neg");
    }

    public TNeg(int line, int pos)
    {
        super.setText("neg");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TNeg(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTNeg(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TNeg text.");
    }
}