package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class X2PMember extends XPMember
{
    private PMember _pMember_;

    public X2PMember()
    {
    }

    public X2PMember(
        PMember _pMember_)
    {
        setPMember(_pMember_);
    }

    public Object clone()
    {
        throw new RuntimeException("Unsupported Operation");
    }

    public void apply(Switch sw)
    {
        throw new RuntimeException("Switch not supported.");
    }

    public PMember getPMember()
    {
        return _pMember_;
    }

    public void setPMember(PMember node)
    {
        if(_pMember_ != null)
        {
            _pMember_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _pMember_ = node;
    }

    void removeChild(Node child)
    {
        if(_pMember_ == child)
        {
            _pMember_ = null;
        }
    }

    void replaceChild(Node oldChild, Node newChild)
    {
    }

    public String toString()
    {
        return "" +
            toString(_pMember_);
    }
}