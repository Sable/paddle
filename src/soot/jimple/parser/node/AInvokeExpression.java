package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AInvokeExpression extends PExpression
{
    private PInvokeExpr _invokeExpr_;

    public AInvokeExpression()
    {
    }

    public AInvokeExpression(
        PInvokeExpr _invokeExpr_)
    {
        setInvokeExpr(_invokeExpr_);

    }
    public Object clone()
    {
        return new AInvokeExpression(
            (PInvokeExpr) cloneNode(_invokeExpr_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAInvokeExpression(this);
    }

    public PInvokeExpr getInvokeExpr()
    {
        return _invokeExpr_;
    }

    public void setInvokeExpr(PInvokeExpr node)
    {
        if(_invokeExpr_ != null)
        {
            _invokeExpr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _invokeExpr_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_invokeExpr_);
    }

    void removeChild(Node child)
    {
        if(_invokeExpr_ == child)
        {
            _invokeExpr_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_invokeExpr_ == oldChild)
        {
            setInvokeExpr((PInvokeExpr) newChild);
            return;
        }

    }
}