/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003, 2004, 2005, 2006 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.jimple.paddle;

import soot.jimple.paddle.queue.Qobj_method_type;
import soot.jimple.paddle.queue.Qobj_type;
import soot.jimple.paddle.queue.Qvar_method_type;
import soot.jimple.paddle.queue.Qvar_type;

/**
 * A default factory for {@link NodeManager}s. 
 *
 * @author Eric Bodden
 */
public class NodeManagerFactory {
	
	/**
	 * Creates a new {@link NodeManager}.
	 */
	public NodeManager createNodeManager(Qvar_method_type locals, Qvar_type globals, Qobj_method_type localallocs, Qobj_type globalallocs) {
		return new NodeManager(locals, globals, localallocs, globalallocs);
	}

}
