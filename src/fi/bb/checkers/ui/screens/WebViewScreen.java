package fi.bb.checkers.ui.screens;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.browser.field2.BrowserFieldListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;

import org.w3c.dom.Document;

import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.network.HttpInterface;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.Actionbar;

/**
 * used to display URL, URL_YOUTUBE and URL_FB, from featured items on homescreen
 * @author anje
 *
 */
public class WebViewScreen extends MainScreen
{
	Actionbar actionbar;
	LoadingDialog prompt;

	public WebViewScreen(String data, boolean isHtml)
	{
		super((isHtml==false)?(NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLLBAR | VERTICAL_SCROLL | HORIZONTAL_SCROLL):(NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLLBAR | NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL));
		setBackground(BackgroundFactory.createSolidBackground(Color.BLACK));

		if (isHtml==false)//so url
		{
			actionbar = new Actionbar("", false, false, true);
			add(actionbar);
		}

		BrowserFieldConfig browserConfig = new BrowserFieldConfig();
		browserConfig.setProperty(BrowserFieldConfig.VIEWPORT_WIDTH, new Integer(Display.getWidth()));

		BrowserField browserField = new BrowserField(browserConfig);
		browserField.setPadding(0, 0, 0, 0);
		browserField.setMargin(0, 0, 0, 0);

		add(browserField);

		/*if (isHtml==false)
		{
			prompt = LoadingDialog.push("Loading");
		}*/
		prompt = LoadingDialog.push("Loading");

		String url = "";
		String html = "";

		if (isHtml == true)//video
		{
			html = data;

			BrowserFieldListener browserListener = new BrowserFieldListener() {

				public void documentLoaded(BrowserField browserField, Document document) throws Exception {

					closeLoadingDialog();
					super.documentLoaded(browserField, document);
				}
			};
			browserField.addListener(browserListener);

			try
			{
				browserField.displayContent(html, url);
			}
			catch (Exception e) 
			{
				closeLoadingDialog();
				RemoteLogger.log("WebViewScreen", "browserField.displayContent(html,\"\") e: " + e.getMessage());
				e.printStackTrace();
			}
		}	
		else//url
		{
			url = data + HttpInterface.getConnectionString();
			RemoteLogger.log("WebViewScreen", "url: " + url);

			BrowserFieldListener browserListener = new BrowserFieldListener() {

				public void documentLoaded(BrowserField browserField, Document document) throws Exception {

					try
					{
						RemoteLogger.log("WebViewScreen", "documentLoaded browserField.getDocumentTitle(): |" + browserField.getDocumentTitle() + "|");
						actionbar.setTitle(browserField.getDocumentTitle());
					}
					catch (Exception e)
					{
						actionbar.setTitle("");
					}

					closeLoadingDialog();
					super.documentLoaded(browserField, document);
				}
			};
			browserField.addListener(browserListener);

			try
			{
				browserField.requestContent(url);
			}
			catch (Exception e)
			{
				closeLoadingDialog();
			}
		}
	}

	private void closeLoadingDialog()
	{
		synchronized (Application.getEventLock())
		{
			if(prompt != null)
			{
				try
				{
					prompt.close();
					prompt = null;
				}
				catch (Exception e)
				{
					RemoteLogger.log("DEBUG_WBE_SCREEN", "prompt close:" + e.getMessage());
				}
			}
		}
	}

	public boolean onClose() {
		closeLoadingDialog();//When closing while loading?
		return super.onClose();
	}
}
