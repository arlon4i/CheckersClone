package fi.bb.checkers.ui.fragments;

import java.io.IOException;
import java.io.InputStream;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.logger.RemoteLogger;

public class InfoFragment extends Fragment
{
	public static final int FRAGMENT_ID = getUUID();
	public InfoFragment(String text, boolean isHtml)
	{
		super(VERTICAL_SCROLL | HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		if (isHtml)
		{
			BrowserFieldConfig browserConfig = new BrowserFieldConfig();
			browserConfig.setProperty(BrowserFieldConfig.VIEWPORT_WIDTH, new Integer(Display.getWidth()));

			BrowserField text_field = new BrowserField(browserConfig);
			//text_field.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));

			/*shadow = BitmapTools.resizeTransparentBitmap(shadow, getWidth(), ResourceHelper.convert(5), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
			text_field.setMargin(shadow.getHeight(), 0, 0, 0);// top padding of 0 doesn't work on os7, because the browserfield seems to supersede the framgent's paint method, and ignores the shadow.*/
			
			add(text_field);

			InputStream is = getClass().getResourceAsStream("/" + text);

			// Note the break html appended to the end in order to introduce a space at the end of the specials disclaimers

			try
			{
				byte[] data = new byte[is.available()];
				is.read(data);
				text_field.displayContent(new String(data), "");
			} catch (IOException e)
			{
				RemoteLogger.log("InfoFragment", e.toString());
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
		else
		{
			// note the end of line problem
			RichTextField detailField = new RichTextField(text + "\n", Color.BLACK);
			detailField.setMargin(10, 10, 10, 10);
			detailField.setEditable(false);
			add(detailField);
		}
		add(new NullField(FOCUSABLE));
	}
}
