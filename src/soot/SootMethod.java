/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot;

import soot.util.*;
import java.util.*;
import soot.baf.*;

public class SootMethod extends AbstractHost
{
    String name;
    List parameterTypes;
    Type returnType;

    boolean isDeclared;
    SootClass declaringClass;

    int modifiers;
    boolean isPhantom;
    
    List exceptions = new ArrayList();

    Body activeBody;
    
    /**
     * Hooks for coffi.  Do not use!
     */

    public soot.coffi.ClassFile coffiClass;

    /**
     * Hooks for coffi.  Do not use!
     */

    public soot.coffi.method_info coffiMethod;


    public SootMethod(String name, List parameterTypes, Type returnType)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
        this.returnType = returnType;
    }

    public SootMethod(String name, List parameterTypes, Type returnType, int modifiers)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);

        this.returnType = returnType;
        this.modifiers = modifiers;
    }

    public SootMethod(String name, List parameterTypes, Type returnType, int modifiers,
                      List thrownExceptions)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);

        this.returnType = returnType;
        this.modifiers = modifiers;

        this.exceptions.addAll(thrownExceptions);
    }

    public void setSource(soot.coffi.ClassFile coffiClass,
        soot.coffi.method_info coffiMethod)
    {
        this.coffiClass = coffiClass;
        this.coffiMethod = coffiMethod;
    }

    public String getName()
    {
        return name;
    }

    public SootClass getDeclaringClass() throws NotDeclaredException
    {
        if(!isDeclared)
            throw new NotDeclaredException(getName());

        return declaringClass;
    }

    public boolean isDeclared()
    {
        return isDeclared;
    }

    public boolean isPhantom()
    {
        return isPhantom;
    }
    
    /**
        Not phantom, abstract or native.
     */
     
    public boolean isConcrete()
    {
        return !isPhantom() && !isAbstract() && !isNative();
    }
    
    public void setPhantom(boolean value)
    {
        isPhantom = value;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public int getModifiers()
    {
        return modifiers;
    }

    public void setModifiers(int modifiers)
    {
        this.modifiers = modifiers;
    }

    public Type getReturnType()
    {
        return returnType;
    }

    public void setReturnType(Type t)
    {
        returnType = t;
    }

    public int getParameterCount()
    {
        return parameterTypes.size();
    }

    public Type getParameterType(int n)
    {
        return (Type) parameterTypes.get(n);
    }

    /**
     * Returns a backed list of the parameter types of this method.
     */

    public List getParameterTypes()
    {
        return parameterTypes;
    }

    /**
        Retrieves the active body for this method.
     */

    public Body getActiveBody() 
    {
        if (declaringClass.isContextClass() || declaringClass.isPhantomClass())
            throw new RuntimeException("cannot get active body for context or signature class!");

        if(!hasActiveBody())
            throw new RuntimeException("no active body present for method " + getSignature());
            
        return activeBody;
    }
    
    /**
        Sets the active body for this method. 
     */
     
    public void setActiveBody(Body body)
    {
        if (declaringClass.isContextClass() || declaringClass.isPhantomClass())
            throw new RuntimeException("cannot set active body for context or signature class!");

        if(!isConcrete())
            throw new RuntimeException("cannot set body for non-concrete method!");
            
        if (body.getMethod() != this)
            body.setMethod(this);

        activeBody = body;
    }

    public boolean hasActiveBody()
    {
        return activeBody != null;
    }
    
    public void releaseActiveBody()
    {
        activeBody = null;
    }
    
    public void addException(SootClass e) throws AlreadyThrowsException
    {
        if(exceptions.contains(e))
            throw new AlreadyThrowsException(e.getName());

        exceptions.add(e);
    }

    public void removeException(SootClass e) throws DoesNotThrowException
    {
        if(!exceptions.contains(e))
            throw new DoesNotThrowException(e.getName());
    }

    public boolean throwsException(SootClass e)
    {
        return exceptions.contains(e);
    }

    /**
     * Returns a backed list of the exceptions thrown by this method.
     */

    public List getExceptions()
    {
        return exceptions;
    }

    public void setParameterTypes(List parameterTypes)
    {
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
    }


    /**
     * For convenience.
     */

    public boolean isStatic()
    {
        return Modifier.isStatic(this.getModifiers());
    }

    /**
     * For more convenience.
     */
    public boolean isPrivate()
    {
        return Modifier.isPrivate(this.getModifiers());
    }

    public boolean isAbstract()
    {
        return Modifier.isAbstract(this.getModifiers());
    }

    public boolean isNative()
    {
        return Modifier.isNative(this.getModifiers());
    }

    /**
     * Returns true if this method is synchronized.
     */
    public boolean isSynchronized()
    {
        return Modifier.isSynchronized(this.getModifiers());
    }
    
    /**
        Returns the Soot signature of this method.  Used to refer to methods unambiguously.
     */

    public String getSignature()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<" + getDeclaringClass().getName() + ": ");
        buffer.append(getReturnType().toString() + " " + getName());
        buffer.append("(");

        Iterator typeIt = getParameterTypes().iterator();

        if(typeIt.hasNext())
        {
            buffer.append(typeIt.next());

            while(typeIt.hasNext())
            {
                buffer.append(",");
                buffer.append(typeIt.next());
            }
        }

        buffer.append(")>");

        return buffer.toString();
    }

    
    /**
        Returns the Soot subsignature of this method.  Used to refer to methods unambiguously.
     */

    public String getSubSignature()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getReturnType().toString() + " " + getName());
        buffer.append("(");

        Iterator typeIt = getParameterTypes().iterator();

        if(typeIt.hasNext())
        {
            buffer.append(typeIt.next());

            while(typeIt.hasNext())
            {
                buffer.append(",");
                buffer.append(typeIt.next());
            }
        }

        buffer.append(")");

        return buffer.toString();
    }

    public String toString()
    {
        return getSignature();
    }

    /**
        Returns the declaration of this method.  Used at the tops of textual body representations (before the {}'s containing the code
        for representation.)
     */

    public String getDeclaration()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(Modifier.toString(this.getModifiers()));

        if(buffer.length() != 0)
            buffer.append(" ");

        buffer.append(this.getReturnType().toString() + " " + this.getName() + "");
        buffer.append("(");

        Iterator typeIt = this.getParameterTypes().iterator();

        if(typeIt.hasNext())
        {
            buffer.append(typeIt.next());

            while(typeIt.hasNext())
            {
                buffer.append(", ");
                buffer.append(typeIt.next());
            }
        }

        buffer.append(")");

        // Print exceptions
        {
            Iterator exceptionIt = this.getExceptions().iterator();

            if(exceptionIt.hasNext())
            {
                buffer.append(" throws ");
                buffer.append("" + ((SootClass) exceptionIt.next()).getName() + "");

                while(exceptionIt.hasNext())
                {
                    buffer.append(", ");
                    buffer.append("" + ((SootClass) exceptionIt.next()).getName() + "");
                }
            }

        }

        return buffer.toString();
    }

}

