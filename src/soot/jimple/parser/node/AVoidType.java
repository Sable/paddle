package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AVoidType extends PType
{
    private TVoid _void_;

    public AVoidType()
    {
    }

    public AVoidType(
        TVoid _void_)
    {
        setVoid(_void_);

    }
    public Object clone()
    {
        return new AVoidType(
            (TVoid) cloneNode(_void_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAVoidType(this);
    }

    public TVoid getVoid()
    {
        return _void_;
    }

    public void setVoid(TVoid node)
    {
        if(_void_ != null)
        {
            _void_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _void_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_void_);
    }

    void removeChild(Node child)
    {
        if(_void_ == child)
        {
            _void_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_void_ == oldChild)
        {
            setVoid((TVoid) newChild);
            return;
        }

    }
}