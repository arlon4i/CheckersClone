package fi.bb.checkers.ui.components;

import java.util.Calendar;
import java.util.Hashtable;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.TransitionContext;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngineInstance;
import fi.bb.checkers.datatypes.FeaturedData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.imageloader.ImageLoader;
import fi.bb.checkers.imageloader.ImageLoaderInterface;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.ExternalUrlPrompt;
import fi.bb.checkers.ui.fragments.CouponsFragment;
import fi.bb.checkers.ui.fragments.DetailedCouponFragment;
import fi.bb.checkers.ui.fragments.HelpFragment;
import fi.bb.checkers.ui.fragments.SelectProvinceFragment;
import fi.bb.checkers.ui.fragments.SpecialsFragment;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.ui.screens.WebViewScreen;
import fi.bb.checkers.utils.BitmapTools;

public class FeaturedItem extends BaseButton implements ImageLoaderInterface, FieldChangeListener
{
	private FeaturedData item;
	private Bitmap image;
	private int width = 0;
	private int height = 0;

	private String tag;
	private int blockNumber;

	public FeaturedItem(FeaturedData item)
	{
		setChangeListener(this);
		this.item = item;
		ImageLoader.loadImage(item.getImageURL(), this);
	}

	public int getPreferredWidth()
	{
		return width;
	}

	public int getPreferredHeight()
	{
		return height;
	}

	protected void paint(Graphics graphics)
	{
		if (image == null) return;

		graphics.drawBitmap(0, 0, getWidth(), getHeight(), image, 0, 0);

		if (_focus)
			graphics.setColor(ResourceHelper.color_primary);
		else
			graphics.setColor(ResourceHelper.color_lighter_grey);

		int border = ResourceHelper.convert(5);

		// need to draw like this because stroke width doesn't affect the rect
		int line_thickness = ResourceHelper.convert(2);
		for (int i = 0; i < line_thickness; i++)
		{
			graphics.drawRoundRect(i, i, getWidth() - (i * 2), getHeight() - (i * 2), border, border);
		}
	}

	public void setImage(Bitmap image)
	{
		if (getOSVersion() >= 6)
		{
			this.image = BitmapTools.resizeTransparentBitmap(image, Display.getWidth(), 0, Bitmap.FILTER_BILINEAR, Bitmap.SCALE_TO_FILL);
			height = this.image.getHeight();
			width = this.image.getWidth();
		}
		else
		{
			this.image = BitmapTools.resizeImage(image, Display.getWidth(), ResourceHelper.convert(150));
			height = this.image.getHeight();
			width = this.image.getWidth();
		}
		
		updateLayout();

		Manager manager = getManager();
		if (manager != null)
		{
			int index = getIndex();
			manager.delete(this);
			manager.insert(this, index);
		}
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public String getTag()
	{
		return this.tag;
	}

	public void setBlockNumber(int blockNumber)
	{
		this.blockNumber = blockNumber;
	}

	public int getBlockNumber()
	{
		return this.blockNumber;
	}

	public void fieldChanged(Field arg0, int arg1)
	{
		logContentBlockViewed(getBlockNumber());

		if (item.getAction().equalsIgnoreCase("URL") || item.getAction().equalsIgnoreCase("URL_FB"))
		{
			//ExternalUrlPrompt.prompt(item.getActionDetail());			
			pushURLScreen(item.getActionDetail());
		}
		else if (item.getAction().equalsIgnoreCase("URL_YOUTUBE"))//Probs change to URL_YOUTUBE_BB
		{
			RemoteLogger.log("platformversion", DeviceInfo.getPlatformVersion());
			RemoteLogger.log("softwaremversion", DeviceInfo.getSoftwareVersion());

			if (getOSVersion() >= 7)
			{
				pushVideoScreen("https://www.youtube.com/embed/"+item.getActionDetail());
			}
			else
			{
				ExternalUrlPrompt.prompt(getURLForYoutubeVideo(item.getActionDetail()));
			}
		}
		else if (item.getAction().equalsIgnoreCase("EEZICOUPONS"))
		{
			if (item.getActionDetail().equalsIgnoreCase("LIST"))
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(CouponsFragment.FRAGMENT_ID, null);
			else
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedCouponFragment.FRAGMENT_ID, new Object[]{item.getActionDetail()});
		}
		else if (item.getAction().equalsIgnoreCase("SPECIALS"))
		{
			if (item.getActionDetail().equalsIgnoreCase("LIST")) ((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SpecialsFragment.FRAGMENT_ID, null);
			// else
			// ((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedSpecialsFragment.FRAGMENT_ID, new Object[]{item.getActionDetail()});

		}
		else if (item.getAction().equalsIgnoreCase("FINDASTORE"))
		{
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SelectProvinceFragment.FRAGMENT_ID, null);
		}
		else if (item.getAction().equalsIgnoreCase("HELP"))
		{
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(HelpFragment.FRAGMENT_ID, null);
		}
		else if (item.getAction().equalsIgnoreCase("THEME_SPECIALS"))
		{
			if (getTag() != null)
			{
				logThemeClicked(getTag(), getBlockNumber());
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).pushThemedSpecialsScreen(getTag());
			}
		}
	}
	
	private int getOSVersion()
	{
		String osVersion = DeviceInfo.getPlatformVersion();

		String firstpart = "";
		int osNumber = 0;

		try
		{
			firstpart = osVersion.substring(0, osVersion.indexOf("."));
			osNumber = Integer.parseInt(firstpart);
		}
		catch (Exception e)
		{
		}
		
		return osNumber;
	}

	private void logThemeClicked(String themeId, int blockNumber) {

		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);
		eventParams.put(FlurryHelper.PARAM_THEME_ID, themeId);
		eventParams.put(FlurryHelper.PARAM_BLOCK_NUMBER, ""+blockNumber);
		FlurryHelper.addFirstLaunchParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_THEME_VIEWED, eventParams, true);
	}

	private void logContentBlockViewed(int blockNumber)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
		FlurryHelper.addLoginParams(eventParams);	
		FlurryHelper.addProvinceParam(eventParams);
		eventParams.put(FlurryHelper.PARAM_CAMPAIGN_ID, item.getAction() + " || " +item.getActionDetail() + " || " + item.getImageURL());

		FlurryHelper.logEvent(FlurryHelper.EVENT_CONTENT_BLOCK_VIEW, eventParams, true);
	}

	private String getHTMLForYoutubeVideo(String url)
	{
		//String html = "<html><body bgcolor=\"#000000\" style=\"margin:0; padding:0\"><embed src=\"http://www.youtube.com/embed/R1QP0ugSlaY\" width=\""+ Display.getWidth() +"\" height=\"" + Display.getHeight() + "></body></html>";
		String html = "<html><body bgcolor=\"#000000\" style=\"margin:0; padding:0\"><iframe id=\"ytplayer\" type=\"text/html\" style=\"display:block;\"  width=\""+ Display.getWidth() +"\" height=\"" + Display.getHeight() + "\" src=\"" + url + "\" frameborder=\"0\"></body></html>";//dont allow fullscreen
		RemoteLogger.log("ANJE_URL", "|" + html + "|");
		return html;
	}

	private String getURLForYoutubeVideo(String videoID)
	{
		RemoteLogger.log("VIDEOURL", "http://m.youtube.com/watch?v=" + videoID);
		return "http://m.youtube.com/watch?v="+videoID;
	}

	private void pushVideoScreen(String url)
	{
		String html = getHTMLForYoutubeVideo(url);

		UiEngineInstance engine = Ui.getUiEngineInstance();
		WebViewScreen screen = new WebViewScreen(html, true);

		engine.setTransition(null, screen, UiEngineInstance.TRIGGER_PUSH, getTransitionContextIn());
		engine.setTransition(screen, null, UiEngineInstance.TRIGGER_POP, getTransitionContextOut());

		UiApplication.getUiApplication().pushScreen(screen);
	}

	private void pushURLScreen(String url)
	{
		UiEngineInstance engine = Ui.getUiEngineInstance();
		WebViewScreen screen = new WebViewScreen(url, false);

		engine.setTransition(null, screen, UiEngineInstance.TRIGGER_PUSH, getTransitionContextIn());
		engine.setTransition(screen, null, UiEngineInstance.TRIGGER_POP, getTransitionContextOut());

		UiApplication.getUiApplication().pushScreen(screen);
	}

	private TransitionContext getTransitionContextIn()
	{
		TransitionContext transitionContextIn = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextIn = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextIn.setIntAttribute(TransitionContext.ATTR_DURATION, 1000);
		transitionContextIn.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_UP);

		return transitionContextIn;
	}

	private TransitionContext getTransitionContextOut()
	{
		TransitionContext transitionContextOut = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextOut = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transitionContextOut.setIntAttribute(TransitionContext.ATTR_DURATION, 1000);
		transitionContextOut.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_DOWN);
		transitionContextOut.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_OUT);

		return transitionContextOut;
	}
}
