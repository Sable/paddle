package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AFullMethodBody extends PMethodBody
{
    private TLBrace _lBrace_;
    private final LinkedList _declaration_ = new TypedLinkedList(new Declaration_Cast());
    private final LinkedList _statement_ = new TypedLinkedList(new Statement_Cast());
    private final LinkedList _catchClause_ = new TypedLinkedList(new CatchClause_Cast());
    private TRBrace _rBrace_;

    public AFullMethodBody()
    {
    }

    public AFullMethodBody(
        TLBrace _lBrace_,
        List _declaration_,
        List _statement_,
        List _catchClause_,
        TRBrace _rBrace_)
    {
        setLBrace(_lBrace_);

        {
            Object temp[] = _declaration_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._declaration_.add(temp[i]);
            }
        }

        {
            Object temp[] = _statement_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._statement_.add(temp[i]);
            }
        }

        {
            Object temp[] = _catchClause_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._catchClause_.add(temp[i]);
            }
        }

        setRBrace(_rBrace_);

    }

    public AFullMethodBody(
        TLBrace _lBrace_,
        XPDeclaration _declaration_,
        XPStatement _statement_,
        XPCatchClause _catchClause_,
        TRBrace _rBrace_)
    {
        setLBrace(_lBrace_);

        if(_declaration_ != null)
        {
            while(_declaration_ instanceof X1PDeclaration)
            {
                this._declaration_.addFirst(((X1PDeclaration) _declaration_).getPDeclaration());
                _declaration_ = ((X1PDeclaration) _declaration_).getXPDeclaration();
            }
            this._declaration_.addFirst(((X2PDeclaration) _declaration_).getPDeclaration());
        }

        if(_statement_ != null)
        {
            while(_statement_ instanceof X1PStatement)
            {
                this._statement_.addFirst(((X1PStatement) _statement_).getPStatement());
                _statement_ = ((X1PStatement) _statement_).getXPStatement();
            }
            this._statement_.addFirst(((X2PStatement) _statement_).getPStatement());
        }

        if(_catchClause_ != null)
        {
            while(_catchClause_ instanceof X1PCatchClause)
            {
                this._catchClause_.addFirst(((X1PCatchClause) _catchClause_).getPCatchClause());
                _catchClause_ = ((X1PCatchClause) _catchClause_).getXPCatchClause();
            }
            this._catchClause_.addFirst(((X2PCatchClause) _catchClause_).getPCatchClause());
        }

        setRBrace(_rBrace_);

    }
    public Object clone()
    {
        return new AFullMethodBody(
            (TLBrace) cloneNode(_lBrace_),
            cloneList(_declaration_),
            cloneList(_statement_),
            cloneList(_catchClause_),
            (TRBrace) cloneNode(_rBrace_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFullMethodBody(this);
    }

    public TLBrace getLBrace()
    {
        return _lBrace_;
    }

    public void setLBrace(TLBrace node)
    {
        if(_lBrace_ != null)
        {
            _lBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lBrace_ = node;
    }

    public LinkedList getDeclaration()
    {
        return _declaration_;
    }

    public void setDeclaration(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _declaration_.add(temp[i]);
        }
    }

    public LinkedList getStatement()
    {
        return _statement_;
    }

    public void setStatement(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _statement_.add(temp[i]);
        }
    }

    public LinkedList getCatchClause()
    {
        return _catchClause_;
    }

    public void setCatchClause(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _catchClause_.add(temp[i]);
        }
    }

    public TRBrace getRBrace()
    {
        return _rBrace_;
    }

    public void setRBrace(TRBrace node)
    {
        if(_rBrace_ != null)
        {
            _rBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _rBrace_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_lBrace_)
            + toString(_declaration_)
            + toString(_statement_)
            + toString(_catchClause_)
            + toString(_rBrace_);
    }

    void removeChild(Node child)
    {
        if(_lBrace_ == child)
        {
            _lBrace_ = null;
            return;
        }

        if(_declaration_.remove(child))
        {
            return;
        }

        if(_statement_.remove(child))
        {
            return;
        }

        if(_catchClause_.remove(child))
        {
            return;
        }

        if(_rBrace_ == child)
        {
            _rBrace_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        for(ListIterator i = _declaration_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        for(ListIterator i = _statement_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        for(ListIterator i = _catchClause_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(_rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

    }

    private class Declaration_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PDeclaration node = (PDeclaration) o;

            if((node.parent() != null) &&
                (node.parent() != AFullMethodBody.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AFullMethodBody.this))
            {
                node.parent(AFullMethodBody.this);
            }

            return node;
        }
    }

    private class Statement_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PStatement node = (PStatement) o;

            if((node.parent() != null) &&
                (node.parent() != AFullMethodBody.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AFullMethodBody.this))
            {
                node.parent(AFullMethodBody.this);
            }

            return node;
        }
    }

    private class CatchClause_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PCatchClause node = (PCatchClause) o;

            if((node.parent() != null) &&
                (node.parent() != AFullMethodBody.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AFullMethodBody.this))
            {
                node.parent(AFullMethodBody.this);
            }

            return node;
        }
    }
}