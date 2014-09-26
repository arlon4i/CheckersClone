package fi.bb.checkers.datatypes.comparators;

import net.rim.device.api.util.Comparator;
import fi.bb.checkers.datatypes.MerchantData;

public class MerchantDistanceComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		if (o1 instanceof MerchantData && o2 instanceof MerchantData)
		{
			double d1 = ((MerchantData) o1).getDistance();
			double d2 = ((MerchantData) o2).getDistance();
			if (d1 < d2) return 1;
			if (d1 > d2) return -1;
			return 0;
		}

		throw new RuntimeException("Comparator objects are not of type MerchantData");
	}
}
