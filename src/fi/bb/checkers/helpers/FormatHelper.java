package fi.bb.checkers.helpers;

import javax.microedition.global.Formatter;

public class FormatHelper {

	public static String getPriceFormattedHome(double priceValue)
	{
		Formatter formatter = new Formatter();
		return formatter.formatNumber(priceValue, 2);
	}
}
