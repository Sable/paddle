package soot.jimple.toolkits.annotation.arraycheck;

import java.util.*;

/** BoundedPriorityList keeps a list in a priority queue.
 * The order is decided by the initial list. 
 */
class BoundedPriorityList
{
    private List fulllist;
    private LinkedList worklist; 

    public BoundedPriorityList(List list)
    {
	this.fulllist = list;
	this.worklist = new LinkedList(list);
    }

    public boolean isEmpty()
    {
	return worklist.isEmpty();
    }

    public Object removeFirst()
    {
	return worklist.removeFirst();
    }

    public void add(Object toadd)
    {
	/* it is not added to the end, but keep it in the order */
	int index = fulllist.indexOf(toadd);

	int i = 0;
	int size = worklist.size();

	for (; i<size; i++)
	{
	    Object tocomp = worklist.get(i);
	    int tmpidx = fulllist.indexOf(tocomp);
	    if (index < tmpidx)
		break;
	}

	worklist.add(i, toadd);
    }
}