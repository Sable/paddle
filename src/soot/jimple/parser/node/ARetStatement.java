package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class ARetStatement extends PStatement
{
    private TRet _ret_;
    private PImmediate _immediate_;
    private TSemicolon _semicolon_;

    public ARetStatement()
    {
    }

    public ARetStatement(
        TRet _ret_,
        PImmediate _immediate_,
        TSemicolon _semicolon_)
    {
        setRet(_ret_);

        setImmediate(_immediate_);

        setSemicolon(_semicolon_);

    }
    public Object clone()
    {
        return new ARetStatement(
            (TRet) cloneNode(_ret_),
            (PImmediate) cloneNode(_immediate_),
            (TSemicolon) cloneNode(_semicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseARetStatement(this);
    }

    public TRet getRet()
    {
        return _ret_;
    }

    public void setRet(TRet node)
    {
        if(_ret_ != null)
        {
            _ret_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _ret_ = node;
    }

    public PImmediate getImmediate()
    {
        return _immediate_;
    }

    public void setImmediate(PImmediate node)
    {
        if(_immediate_ != null)
        {
            _immediate_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _immediate_ = node;
    }

    public TSemicolon getSemicolon()
    {
        return _semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(_semicolon_ != null)
        {
            _semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _semicolon_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_ret_)
            + toString(_immediate_)
            + toString(_semicolon_);
    }

    void removeChild(Node child)
    {
        if(_ret_ == child)
        {
            _ret_ = null;
            return;
        }

        if(_immediate_ == child)
        {
            _immediate_ = null;
            return;
        }

        if(_semicolon_ == child)
        {
            _semicolon_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_ret_ == oldChild)
        {
            setRet((TRet) newChild);
            return;
        }

        if(_immediate_ == oldChild)
        {
            setImmediate((PImmediate) newChild);
            return;
        }

        if(_semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

    }
}