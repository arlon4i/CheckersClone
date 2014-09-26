package fi.bb.checkers.prompts;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.ui.component.Dialog;

public class ExternalUrlPrompt
{
	public static void prompt(String url)
	{
		// Message should probably change to give a bit more detail
		int choice = CustomDialog.doModal("You are about to leave the app.", new String[]{"Continue", "Cancel"}, new int[]{Dialog.YES, Dialog.CANCEL}, Dialog.YES, false);
		if (choice == Dialog.YES)
		{
			Browser.getDefaultSession().displayPage(url);
		}
	}
}
