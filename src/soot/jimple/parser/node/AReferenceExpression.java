package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AReferenceExpression extends PExpression
{
    private PReference _reference_;

    public AReferenceExpression()
    {
    }

    public AReferenceExpression(
        PReference _reference_)
    {
        setReference(_reference_);

    }
    public Object clone()
    {
        return new AReferenceExpression(
            (PReference) cloneNode(_reference_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAReferenceExpression(this);
    }

    public PReference getReference()
    {
        return _reference_;
    }

    public void setReference(PReference node)
    {
        if(_reference_ != null)
        {
            _reference_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _reference_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_reference_);
    }

    void removeChild(Node child)
    {
        if(_reference_ == child)
        {
            _reference_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_reference_ == oldChild)
        {
            setReference((PReference) newChild);
            return;
        }

    }
}