package fi.bb.checkers.datatypes.comparators;

import net.rim.device.api.util.Comparator;
import fi.bb.checkers.datatypes.CampaignData;

public class CampaignDataComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		if (o1 instanceof CampaignData && o2 instanceof CampaignData)
		{
			return ((CampaignData)o1).greaterThan(((CampaignData)o2)) ? 1 : -1;
		}

		return 0;
	}
}