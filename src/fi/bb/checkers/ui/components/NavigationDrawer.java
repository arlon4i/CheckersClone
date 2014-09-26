package fi.bb.checkers.ui.components;

import java.util.Hashtable;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.SharingHelper;
import fi.bb.checkers.ui.fragments.AboutFragment;
import fi.bb.checkers.ui.fragments.ChecklineFragment;
import fi.bb.checkers.ui.fragments.CouponsFragment;
import fi.bb.checkers.ui.fragments.FeedbackFragment;
import fi.bb.checkers.ui.fragments.HelpFragment;
import fi.bb.checkers.ui.fragments.HomeFragment;
import fi.bb.checkers.ui.fragments.SelectProvinceFragment;
import fi.bb.checkers.ui.fragments.SpecialsFragment;
import fi.bb.checkers.ui.fragments.StoreInfoFragment;
import fi.bb.checkers.ui.fragments.TermsFragment;
import fi.bb.checkers.utils.BitmapTools;

public class NavigationDrawer extends VerticalFieldManager implements FieldChangeListener
{
	private Bitmap shadow;
	private ListField item_home;
	private ListField item_coupons;
	private ListField item_specials;
	private ListField item_findstore;
	private ListField item_help;
	private ListField item_terms;
	private ListField item_share;

	private ListField item_help_help;
	private ListField item_help_talktous;

	private ListField item_terms_terms;
	private ListField item_terms_about;

	private ListField item_talktous_preferred;
	private ListField item_talktous_store;
	private ListField item_talktous_checkline;
	private ListField item_talktous_feedback;
	private DrawerListener listener;

	public NavigationDrawer()
	{
		super(VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLL | USE_ALL_HEIGHT);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_drawer_grey));
		shadow = BitmapTools.resizeTransparentBitmap(ResourceHelper.getImage("right_gradient"), ResourceHelper.convert(5), getPreferredHeight(), Bitmap.FILTER_LANCZOS, Bitmap.SCALE_STRETCH);
		buildDefault();
	}

	protected void sublayout(int maxWidth, int maxHeight)
	{
		super.sublayout(getPreferredWidth(), getPreferredHeight());
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	protected void paint(Graphics graphics)
	{
		super.paint(graphics);

		graphics.drawBitmap(getWidth() - shadow.getWidth(), getVerticalScroll(), shadow.getWidth(), shadow.getHeight(), shadow, 0, 0);
	}

	public int getPreferredWidth()
	{
		return Display.getWidth() - ResourceHelper.convert(50);
	}

	public int getPreferredHeight()
	{
		return Display.getHeight();
	}

	protected void onVisibilityChange(boolean visible)
	{
		if (visible)
		{
			if (item_talktous_store != null && item_talktous_store.getManager() != null)
			{
				if ((RuntimeStoreHelper.getSessionID() != null && RuntimeStoreHelper.getUserData().getPreferredStore() != null) == (item_talktous_preferred.getManager() == null))
				{
					// have a preferred store and not showing || no preferred store and is showing
					buildTalktous();
				}
			}
		}
		super.onVisibilityChange(visible);
	}

	private void buildDefault()
	{
		deleteAll();

		item_home = new ListField("Home", "icon_home_default", "icon_home_hover");
		item_coupons = new ListField("EeziCoupons", "icon_eezicoupons_default", "icon_EeziCoupons_hover");
		item_specials = new ListField("Specials", "icon_specials_default", "icon_specials_hover");
		item_findstore = new ListField("Find a Store", "icon_find-a-store_default", "icon_find-a-store_hover");
		item_help = new ListField("Help & Contact Us", "icon_help-&-contact-us_default", "icon_help-&-contact-us_hover");
		item_terms = new ListField("About & Terms", "icon_terms_default", "icon_terms_hover");
		item_share = new ListField("Share this App", "ic_share_default", "ic_share_hover");

		item_home.setChangeListener(this);
		item_coupons.setChangeListener(this);
		item_specials.setChangeListener(this);
		item_findstore.setChangeListener(this);
		item_help.setChangeListener(this);
		item_terms.setChangeListener(this);
		item_share.setChangeListener(this);

		add(new ListHeading("QUICK NAVIGATION"));
		add(item_home);

		add(new ListHeading("SHOPPING"));
		add(item_coupons);
		add(item_specials);
		add(item_findstore);

		add(new ListHeading("OTHER"));
		add(item_help);
		add(item_terms);
		add(item_share);
	}

	private void buildTerms()
	{
		deleteAll();

		item_terms_terms = new ListField("Terms", null, null);
		item_terms_about = new ListField("About", null, null);

		item_terms_terms.setChangeListener(this);
		item_terms_about.setChangeListener(this);

		add(new ListHeading("ABOUT & TERMS"));
		add(item_terms_about);
		add(item_terms_terms);
	}

	private void buildHelp()
	{
		deleteAll();

		item_help_help = new ListField("Help", null, null);
		item_help_talktous = new ListField("Talk to Us", null, null);

		item_help_help.setChangeListener(this);
		item_help_talktous.setChangeListener(this);

		add(new ListHeading("HELP & CONTACT US"));
		add(item_help_help);
		add(item_help_talktous);
	}

	private void buildTalktous()
	{
		deleteAll();

		item_talktous_preferred = new ListField("My Preferred Store", null, null);
		item_talktous_store = new ListField("Contact a Store", null, null);
		item_talktous_checkline = new ListField("Speak to a Checkline Consultant", null, null);
		item_talktous_feedback = new ListField("Send Us a Message", null, null);

		item_talktous_preferred.setChangeListener(this);
		item_talktous_store.setChangeListener(this);
		item_talktous_checkline.setChangeListener(this);
		item_talktous_feedback.setChangeListener(this);

		add(new ListHeading("TALK TO US"));
		if (RuntimeStoreHelper.getSessionID() != null && RuntimeStoreHelper.getUserData().getPreferredStore() != null) add(item_talktous_preferred);
		add(item_talktous_store);
		add(item_talktous_checkline);
		add(item_talktous_feedback);
	}

	/**
	 * Collapses the submenus. Returns true if a collapse occurred.
	 * 
	 * @return
	 */
	public boolean collapse()
	{
		if (item_help_help != null && item_help_help.getManager() != null)
		{
			buildDefault();
			return true;
		}

		if (item_talktous_store != null && item_talktous_store.getManager() != null)
		{
			buildHelp();
			return true;
		}

		if (item_terms_terms != null && item_terms_terms.getManager() != null)
		{
			buildDefault();
			return true;
		}

		return false;
	}

	public void setDrawerListener(DrawerListener listener)
	{
		this.listener = listener;
	}

	private class ListHeading extends HorizontalFieldManager
	{
		public ListHeading(String title)
		{
			super(NON_FOCUSABLE);
			setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_lighter_grey));

			LabelField label = new LabelField(title, ResourceHelper.color_dark_grey, 0);
			label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

			int margin = (getPreferredHeight() - label.getPreferredHeight()) / 2;
			label.setMargin(margin, 0, margin, margin);
			add(label);
		}

		public int getPreferredHeight()
		{
			return ResourceHelper.convert(20);
		}

		public int getPreferredWidth()
		{
			return NavigationDrawer.this.getPreferredWidth();
		}

		protected void sublayout(int maxWidth, int maxHeight)
		{
			super.sublayout(getPreferredWidth(), getPreferredHeight());
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		protected void paint(Graphics graphics)
		{
			super.paint(graphics);

			graphics.setColor(ResourceHelper.color_lighter_grey);
			graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
		}
	}

	public void fieldChanged(Field arg0, int arg1)
	{
		int action = -1;
		Object[] params = null;
		if (arg0 == item_home)
		{
			action = HomeFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_coupons)
		{
			action = CouponsFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_specials)
		{
			action = SpecialsFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_findstore)
		{
			action = SelectProvinceFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_help)
		{
			buildHelp();
		}
		else if (arg0 == item_terms)
		{
			buildTerms();
		}
		else if (arg0 == item_share)
		{
			SharingHelper.shareApp();
		}
		else if (arg0 == item_terms_about)
		{
			action = AboutFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_terms_terms)
		{
			action = TermsFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_help_help)
		{
			action = HelpFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_help_talktous)
		{
			buildTalktous();
		}
		else if (arg0 == item_talktous_preferred)
		{
			action = StoreInfoFragment.FRAGMENT_ID;
			params = new Object[]{RuntimeStoreHelper.getUserData().getPreferredStore(), "true"};
		}
		else if (arg0 == item_talktous_store)
		{
			action = SelectProvinceFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_talktous_checkline)
		{
			logSpeakToConsultantClicked();
			
			action = ChecklineFragment.FRAGMENT_ID;
		}
		else if (arg0 == item_talktous_feedback)
		{
			action = FeedbackFragment.FRAGMENT_ID;
		}

		if (action != -1 && listener != null)
		{
			listener.itemClicked(action, params);
		}
	}

	private void logSpeakToConsultantClicked() 
	{
		Hashtable eventParams = new Hashtable();
		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addFirstLaunchParam(eventParams);
		
		FlurryHelper.logEvent(FlurryHelper.EVENT_SPEAK_TO_A_CONSULTANT, eventParams, false);
	}

	public interface DrawerListener
	{
		public abstract void itemClicked(int action, Object[] params);
	}
}
