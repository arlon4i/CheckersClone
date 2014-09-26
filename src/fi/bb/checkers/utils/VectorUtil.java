package fi.bb.checkers.utils;

import java.util.Vector;

public class VectorUtil
{
	public static void copy(Vector from, Vector to)
	{
		to.removeAllElements();

		for (int i = 0; i < from.size(); i++)
		{
			to.addElement(from.elementAt(i));
		}
	}

	public static boolean hasCommonElement(Vector arg0, Vector arg1)
	{
		for (int i = 0; i < arg0.size(); i++)
		{
			if (arg1.contains(arg0.elementAt(i))) return true;
		}

		return false;
	}
}
