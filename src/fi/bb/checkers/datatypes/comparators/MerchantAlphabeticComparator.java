package fi.bb.checkers.datatypes.comparators;

import net.rim.device.api.util.Comparator;
import fi.bb.checkers.datatypes.MerchantData;

public class MerchantAlphabeticComparator implements Comparator
{

	public int compare(Object o1, Object o2)
	{
		/* if (o1 instanceof MerchantData && o2 instanceof MerchantData) return ((MerchantData) o1).getName().compareTo(((MerchantData) o2).getName()); */
		
		if(o1 instanceof MerchantData && o2 instanceof MerchantData) {
			int compareResult = ((MerchantData) o1).getBrand().compareTo(((MerchantData) o2).getBrand());
			
			if(compareResult == 0) {
				if((((MerchantData) o1).getName().indexOf("Liquor") != (-1)) && (((MerchantData) o2).getName().indexOf("Liquor") == (-1))) {
					compareResult = 1;
				} else if((((MerchantData) o2).getName().indexOf("Liquor") != (-1)) && (((MerchantData) o1).getName().indexOf("Liquor") == (-1))) {
					compareResult = -1;
				} else {
					compareResult = ((MerchantData) o1).getName().compareTo(((MerchantData) o2).getName());
				}
			}
			
			return compareResult;
		}

		throw new RuntimeException("Comparator objects are not of type MerchantData");
	}

}
