package fi.bb.checkers.datatypes.comparators;

import net.rim.device.api.util.Comparator;
import fi.bb.checkers.datatypes.CouponCategory;

public class CategoryNameComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		if (o1 instanceof CouponCategory && o2 instanceof CouponCategory)
		{
			String d1 = ((CouponCategory) o1).getName();
			String d2 = ((CouponCategory) o2).getName();
			return d1.compareTo(d2);
		}

		throw new RuntimeException("Comparator objects are not of type CouponCategory");
	}
}