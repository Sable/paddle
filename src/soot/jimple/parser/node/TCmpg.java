package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TCmpg extends Token
{
    public TCmpg()
    {
        super.setText("cmpg");
    }

    public TCmpg(int line, int pos)
    {
        super.setText("cmpg");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TCmpg(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCmpg(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TCmpg text.");
    }
}