package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AQuotedName extends PName
{
    private TQuotedName _quotedName_;

    public AQuotedName()
    {
    }

    public AQuotedName(
        TQuotedName _quotedName_)
    {
        setQuotedName(_quotedName_);

    }
    public Object clone()
    {
        return new AQuotedName(
            (TQuotedName) cloneNode(_quotedName_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAQuotedName(this);
    }

    public TQuotedName getQuotedName()
    {
        return _quotedName_;
    }

    public void setQuotedName(TQuotedName node)
    {
        if(_quotedName_ != null)
        {
            _quotedName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _quotedName_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_quotedName_);
    }

    void removeChild(Node child)
    {
        if(_quotedName_ == child)
        {
            _quotedName_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_quotedName_ == oldChild)
        {
            setQuotedName((TQuotedName) newChild);
            return;
        }

    }
}