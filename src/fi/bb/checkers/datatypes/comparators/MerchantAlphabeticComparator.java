package fi.bb.checkers.datatypes.comparators;

import net.rim.device.api.util.Comparator;
import fi.bb.checkers.datatypes.MerchantData;

public class MerchantAlphabeticComparator implements Comparator
{

	public int compare(Object o1, Object o2)
	{
		if (o1 instanceof MerchantData && o2 instanceof MerchantData) return ((MerchantData) o1).getName().compareTo(((MerchantData) o2).getName());

		throw new RuntimeException("Comparator objects are not of type MerchantData");
	}

}
