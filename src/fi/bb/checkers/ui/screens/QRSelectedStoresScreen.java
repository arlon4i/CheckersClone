package fi.bb.checkers.ui.screens;

import java.io.IOException;
import java.io.InputStream;
import fi.bb.checkers.MainApplication;
import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.TransitionContext;
import net.rim.device.api.ui.UiApplication;

public class QRSelectedStoresScreen extends BottomUpDrawer
{
	public QRSelectedStoresScreen()
	{
		super("QR code at selected stores");
		
		BrowserFieldConfig browserConfig = new BrowserFieldConfig();
		browserConfig.setProperty(BrowserFieldConfig.VIEWPORT_WIDTH, new Integer(Display.getWidth()));
		BrowserField browser_field = new BrowserField(browserConfig);		
		add(browser_field);

		InputStream is = getClass().getResourceAsStream("/qrselectedstores.html");

		try
		{
			byte[] data = new byte[is.available()];
			is.read(data);
			browser_field.displayContent(new String(data), "");
		} catch (IOException e)
		{
			
		}
		finally
		{
			if (is!=null)
			{
				try
				{
					is.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	public static void push()
	{
		MainApplication app = (MainApplication) UiApplication.getUiApplication();
		app.slideScreen(new QRSelectedStoresScreen(), TransitionContext.DIRECTION_UP, TransitionContext.DIRECTION_DOWN, 2000);
	}
}
