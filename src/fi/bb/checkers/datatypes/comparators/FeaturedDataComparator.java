package fi.bb.checkers.datatypes.comparators;

import net.rim.device.api.util.Comparator;
import fi.bb.checkers.datatypes.FeaturedData;

public class FeaturedDataComparator implements Comparator
{

	public int compare(Object o1, Object o2)
	{
		if (o1 instanceof FeaturedData && o2 instanceof FeaturedData)
		{
			if (((FeaturedData) o1).getOrder() < ((FeaturedData) o2).getOrder()) return -1;
			if (((FeaturedData) o1).getOrder() > ((FeaturedData) o2).getOrder()) return 1;
		}
		return 0;
	}
}