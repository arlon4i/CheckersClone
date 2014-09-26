package fi.bb.checkers.utils;

import java.util.Vector;

public class Queue extends Vector
{
	public Queue()
	{
		super();
	}

	public void enqueue(Object obj)
	{
		this.addElement(obj);
	}

	public Object dequeue()
	{
		Object obj = this.firstElement();
		this.removeElementAt(0);
		return obj;
	}
}
