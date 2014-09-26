package fi.bb.checkers.ui.fragments;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.LocationHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.SharingHelper;
import fi.bb.checkers.interfaces.InterfaceCouponsFinishedLoading;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.ui.components.CouponField;
import fi.bb.checkers.ui.components.ImageButton;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.screens.SelectionScreen;
import fi.bb.checkers.ui.screens.TutorialScreen;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.VectorUtil;

public class CouponsFragment extends Fragment implements FieldChangeListener, InterfaceCouponsFinishedLoading
{
	public static final int FRAGMENT_ID = getUUID();
	VerticalFieldManager content;
	HorizontalFieldManager banner_manager;

	ImageButton button_search;
	ImageButton button_filter;
	TextImageButton button_redeem;
	TextInputField field_search;

	Vector category_filter;
	LoadThread load_coupons;

	InterfaceCouponsFinishedLoading interfaceListener;

	private int refreshCount;
	private String refreshCountTag;
	private String refreshThreadTag = "REFRESH_THREAD_TOKEN";

	private Font fontNoItemsForSearch = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(16), Ui.UNITS_px);

	String currentLocationId = "";;

	public CouponsFragment()
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		logBrowseCoupons();

		this.interfaceListener = this;

		banner_manager = new HorizontalFieldManager()
		{
			public int getPreferredHeight()
			{
				return ResourceHelper.convert(30);
			}

			public int getPreferredWidth()
			{
				return Display.getWidth();
			}

			protected void sublayout(int width, int height)
			{
				width = getPreferredWidth();
				height = getPreferredHeight();
				super.sublayout(width, height);
				setExtent(width, height);
			}

			protected void paint(Graphics graphics)
			{
				graphics.setColor(0xe2e2e2);
				graphics.fillRect(0, 0, getWidth(), getHeight());

				super.paint(graphics);
			}
		};
		button_search = new ImageButton("icon_search_default", "icon_search_hover");
		button_filter = new ImageButton("icon_filter_default", "icon_filter_hover");
		button_redeem = new TextImageButton("Redeem All", "btn_sml_default", "btn_sml_hover");
		button_redeem.setTextColor(ResourceHelper.color_white);
		button_redeem.setTextColorHover(ResourceHelper.color_primary);
		button_redeem.setTextColorPressed(ResourceHelper.color_primary);

		button_search.setPadding(0, ResourceHelper.convert(5), 0, ResourceHelper.convert(5));
		button_search.setMargin((banner_manager.getPreferredHeight() - button_search.getPreferredHeight()) / 2, 0, 0, 0);
		button_filter.setMargin((banner_manager.getPreferredHeight() - button_filter.getPreferredHeight()) / 2, 0, 0, 0);
		button_redeem.setMargin((banner_manager.getPreferredHeight() - button_redeem.getPreferredHeight()) / 2, 0, 0,
				Display.getWidth() - button_search.getPreferredWidth() - button_filter.getPreferredWidth() - button_redeem.getPreferredWidth() - ResourceHelper.convert(20));

		button_search.setChangeListener(this);
		button_filter.setChangeListener(this);
		button_redeem.setChangeListener(this);

		banner_manager.add(button_search);
		banner_manager.add(button_filter);
		banner_manager.add(button_redeem);
		add(banner_manager);

		content = new VerticalFieldManager(VERTICAL_SCROLL|VERTICAL_SCROLLBAR|NO_HORIZONTAL_SCROLL)
		{
			protected void paint(Graphics graphics)
			{
				super.paint(graphics);

				//graphics.drawBitmap(0, content.getVerticalScroll(), shadow.getWidth(), shadow.getHeight(), shadow, 0, 0);
			}
		};
		content.setScrollListener(this);
		add(content);

		field_search = new TextInputField("Search", false, false, ResourceHelper.color_grey, 0)
		{
			public int getPreferredWidth()
			{
				return Display.getWidth() - ResourceHelper.convert(8);
			}
		};
		field_search.setMargin((banner_manager.getPreferredHeight() - field_search.getPreferredHeight()) / 2, 0, 0, ResourceHelper.convert(4));
		field_search.setChangeListener(this);

		category_filter = PersistentStoreHelper.getCouponCategories();//RuntimeStoreHelper.getCategories();

		//Anje added/changed this new		
		if (RuntimeStoreHelper.getSessionID() != null && PersistentStoreHelper.getSpecialsRegion() != null)
		{
			currentLocationId = PersistentStoreHelper.getSpecialsRegion().getId();
			refresh();
		}
		else
		{
			if (LocationHelper.location != null)
			{
				// get the GPS location
				RuntimeStoreHelper.setProvinceLoggedOutUser(LocationHelper.location);
				currentLocationId = LocationHelper.location.getId();
				refresh();
			}
			else
			{
				if (RuntimeStoreHelper.getProvinceLoggedOutUser() != null)
				{
					currentLocationId = RuntimeStoreHelper.getProvinceLoggedOutUser().getId();
					refresh();
				}
				else
				{
					selectProvinceForGuest();
				}
			}
		}

		/*if (RuntimeStoreHelper.getProvinceLoggedOutUser() != null)
		{
			refresh();
		}
		else
		{
			selectProvinceForGuest();
		}*/
	}

	private void selectProvinceForGuest()
	{
		UiApplication.getUiApplication().invokeLater(new Runnable() {

			public void run() {
				Vector selection = SelectionScreen.doModal("Select a Province", new Vector(), null, PersistentStoreHelper.getProvinces(), false, true);//RuntimeStoreHelper.getProvinces()
				if (selection != null)
				{
					RuntimeStoreHelper.setProvinceLoggedOutUser((LocationData) selection.elementAt(0));
					currentLocationId = ((LocationData) selection.elementAt(0)).getId();
					refresh();
				}
				else
				{
					selectProvinceForGuest();
				}
			}
		});
	}	

	protected void onVisibilityChange(boolean visible)
	{
		if (visible)
		{
			if (PersistentStoreHelper.isShowTutorial3())
			{
				TutorialScreen.push(TutorialScreen.COUPONS);
				PersistentStoreHelper.setShowTutorial3(false);
			}
		}
		super.onVisibilityChange(visible);
	}

	public void onClose()
	{
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_EEZI_COUPON_VIEW_ALL_BUTTON);
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_BROWSE_EEZI_COUPONS);
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_CONTENT_BLOCK_VIEW);

		if (load_coupons != null && load_coupons.isAlive())
		{
			try
			{
				load_coupons.stop();
				load_coupons.join();
			} catch (InterruptedException e)
			{
			}
		}
	}

	public void refresh()
	{
		// create the fields in a separate thread so the screen can open while it populates
		String newRefreshTag = getNewRefreshTag();
		if (load_coupons != null && load_coupons.isAlive())
		{
			try
			{
				load_coupons.stop();
				load_coupons.join();
			} catch (InterruptedException e)
			{
			}
		}

		load_coupons = new LoadThread(newRefreshTag);
		load_coupons.start();
	}

	private String getNewRefreshTag()
	{
		synchronized (refreshThreadTag) 
		{
			refreshCount++;
			refreshCountTag = ""+refreshCount;
		}

		return refreshCountTag;
	}

	private String getRefreshCountTag()
	{
		synchronized (refreshThreadTag) 
		{
			return refreshCountTag;
		}
	}

	public void makeMenu(Menu menu)
	{
		MenuItem item = new MenuItem("Redeem All", 0x00070000, 0)
		{
			public void run()
			{
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
			}
		};
		menu.add(item);

		item = new MenuItem("Search", 0x00070000, 1)
		{
			public void run()
			{
				search();
			}
		};
		menu.add(item);

		item = new MenuItem("Filter", 0x00070000, 2)
		{
			public void run()
			{
				filter();
			}
		};
		menu.add(item);

		String menuImageText = (PersistentStoreHelper.shouldLoadImages() == true)?"Turn off images":"Turn on images";
		item = new MenuItem(menuImageText, 0x00070000, 3)
		{
			public void run()
			{
				PersistentStoreHelper.setshouldLoadImages(!PersistentStoreHelper.shouldLoadImages());
				refresh();
			}
		};
		menu.add(item);

		if (content.getVerticalScroll() > ResourceHelper.convert(10))
		{
			item = new MenuItem("Scroll to Top", 0x00060000, 0)
			{
				public void run()
				{
					content.setVerticalScroll(0);
				}
			};
			menu.add(item);
		}

		int index = content.getFieldWithFocusIndex();
		if (index != -1)
		{
			final CouponField coupon_field = (CouponField) content.getField(index);
			if (RuntimeStoreHelper.getSessionID() != null)
			{
				if (PersistentStoreHelper.getMylist().contains(coupon_field.getCoupon()))
				{
					item = new MenuItem("Remove from List", 0x00050000, 0)
					{
						public void run()
						{
							PersistentStoreHelper.mylistDelete(coupon_field.getCoupon());
							coupon_field.repaint();
						}
					};
					menu.add(item);
				}
				else
				{
					item = new MenuItem("Add to List", 0x00050000, 0)
					{
						public void run()
						{
							if (RuntimeStoreHelper.getSessionID() == null)
							{
								((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
							}
							else
							{
								PersistentStoreHelper.mylistAdd(coupon_field.getCoupon());
								coupon_field.repaint();
							}
						}
					};
					menu.add(item);
				}
			}

			item = new MenuItem("Share", 0x00050000, 2)
			{
				public void run()
				{
					SharingHelper.shareCoupon(coupon_field.getCoupon());
				}
			};
			menu.add(item);
		}
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == button_search)
		{
			search();
		}
		else if (field == button_filter)
		{
			filter();
		}
		else if (field == button_redeem)
		{
			logRedeemAll();
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).profileClick();
		}
		else if (field == field_search)
		{
			refresh();
		}
	}

	private void logRedeemAll()
	{
		Calendar dateNow = Calendar.getInstance();
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		FlurryHelper.addLoginParams(eventParams);	
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(dateNow));
		FlurryHelper.addFirstLaunchParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_REDEEM_ALL_EEZI_COUPONS, eventParams, false);
	}

	private void search()
	{
		if (field_search.getManager() == null)
		{
			logSearch();
			banner_manager.deleteAll();
			banner_manager.add(field_search);
			field_search.setFocus();
		}
		else
		{
			banner_manager.deleteAll();
			banner_manager.add(button_search);
			banner_manager.add(button_filter);
			banner_manager.add(button_redeem);
		}
	}

	private void filter()
	{
		Vector dataset = PersistentStoreHelper.getCouponCategories();

		for (int i = 0; i < dataset.size(); i++)
		{
			if (((CouponCategory)dataset.elementAt(i)).getName().equals("All") == true)
			{
				dataset.removeElementAt(i);
				break;
			}
		}

		Vector selection = SelectionScreen.doModal("Filter Categories", category_filter, category_filter, dataset, true, false);
		if (selection != null)
		{
			if (selection.size()>0)
			{

				logFilter(selection);						
				category_filter = selection;
				refresh();
				if (selection.size() == PersistentStoreHelper.getCouponCategories().size())
				{
					button_filter.setImage("icon_filter_default", "icon_filter_hover");
				}
				else
				{
					button_filter.setImage("icon_filter_applied", "icon_filter_hover");
				}
			}
			else
			{				
				InfoDialog.doModal("", "Please select at least one category", "Okay");
				filter();
			}
		}
	}

	protected boolean keyChar(char ch, int status, int time)
	{
		if (ch == Characters.ESCAPE && field_search.getManager() == banner_manager)
		{
			banner_manager.deleteAll();
			banner_manager.add(button_search);
			banner_manager.add(button_filter);
			banner_manager.add(button_redeem);

			field_search.setText("");
			refresh();

			return true;
		}

		return super.keyChar(ch, status, time);
	}

	// returns true if the coupon belongs to a category that we have filtered on
	private boolean containsSelectedCategory(CampaignData coupon)
	{
		// if all categories are selected
		if (category_filter.size() == PersistentStoreHelper.getCouponCategories().size()) return true;

		for (int i = 0; i < coupon.getCategoryList().size(); i++)
		{
			if (category_filter.contains(coupon.getCategoryList().elementAt(i))) return true;
		}
		return false;
	}

	private void logFilter(Vector categories)
	{
		Hashtable eventParams = new Hashtable();

		String categoriesS = "";
		for (int i=0; i < categories.size(); i++)
		{
			if (i==0)
			{
				categoriesS += ((CouponCategory)categories.elementAt(i)).getId();
			}
			else
			{
				categoriesS += " , "+((CouponCategory)categories.elementAt(i)).getId();
			}
		}

		eventParams.put(FlurryHelper.PARAM_CATEGORY, categoriesS);
		FlurryHelper.addLoginParams(eventParams);	
		FlurryHelper.addProvinceParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_FILTER_EEZI_COUPONS, eventParams, false);
	}

	private void logBrowseCoupons()
	{
		Hashtable eventParams = new Hashtable();

		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);		
		eventParams.put(FlurryHelper.PARAM_TOTAL_SAVINGS_AVAILABLE, PersistentStoreHelper.getTotalSavings());

		FlurryHelper.logEvent(FlurryHelper.EVENT_BROWSE_EEZI_COUPONS, eventParams, true);
	}

	private class LoadThread extends Thread
	{
		private volatile boolean run;
		private String threadTag;

		boolean deletedAll = false;

		public LoadThread(String threadTag)
		{
			run = true;
			this.threadTag = threadTag;
			setPriority(Thread.NORM_PRIORITY);
		}

		public void stop()
		{
			run = false;
		}

		public void run()
		{
			Vector coupons = RuntimeStoreHelper.getCoupons(interfaceListener, true, "Loading", currentLocationId);
			if (coupons == null) return;//dloading in background

			deletedAll = false;
			UiApplication.getUiApplication().invokeLater(new Runnable() {

				public void run() {
					content.deleteAll();
					deletedAll=true;
				}
			});

			while(deletedAll==false && run==true)
			{
				//wait
			}

			if (coupons.size() == 0)
			{
				final LabelField noItems = new LabelField("No Results", ResourceHelper.color_black, 0)
				{
					public int getPreferredHeight()
					{
						return fontNoItemsForSearch.getHeight();
					}
				};
				noItems.setFont(fontNoItemsForSearch);
				noItems.setMargin(ResourceHelper.convert(5), 0, ResourceHelper.convert(10), ResourceHelper.convert(5));
				noItems.setTag(threadTag);

				UiApplication.getUiApplication().invokeLater(new Runnable()
				{
					public void run()
					{
						if (noItems.getTag().equals(getRefreshCountTag()) == true);//this check and implementation of 'refreshcounttag' is to eliminate the duplicates, because of threading
						{
							content.add(noItems);
						}
					}
				});
			}
			else
			{
				if (RuntimeStoreHelper.getSessionID() != null)
				{
					PersistentStoreHelper.checkMyListCoupons();
				}

				for (int i = 0; i < coupons.size(); i++)
				{
					if (!run) return;
					CampaignData coupon = (CampaignData) coupons.elementAt(i);
					final CouponField item = new CouponField(coupon)
					{
						public void clickButton()
						{
							logCouponTapped(getCoupon());
							logSearchResultTapped(getCoupon());
							((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedCouponFragment.FRAGMENT_ID, new Object[]{getCoupon()});
						}
					};
					item.setTag(threadTag);
					item.setOrderTag(""+i);


					if (containsSelectedCategory(coupon))
					{
						UiApplication.getUiApplication().invokeLater(new Runnable()
						{
							public void run()
							{
								if (item.getTag().equals(getRefreshCountTag()) == true);//this check and implementation of 'refreshcounttag' is to eliminate the duplicates, because of threading
								{
									String search_term = field_search.getText().toLowerCase();
									if (search_term == null || item.getCoupon().getName().toLowerCase().indexOf(search_term) != -1)
									{
										content.add(item);//I removed this ordering thing for now, since when it is a big list it loads funny.
									}
								}
							}
						});
					}
				}

				if (content.getFieldCount() == 0)
				{
					final LabelField noItems = new LabelField("No Results", ResourceHelper.color_black, 0)
					{
						public int getPreferredHeight()
						{
							return fontNoItemsForSearch.getHeight();
						}
					};
					noItems.setFont(fontNoItemsForSearch);
					noItems.setMargin(ResourceHelper.convert(5), 0, ResourceHelper.convert(10), ResourceHelper.convert(5));
					noItems.setTag(threadTag);

					UiApplication.getUiApplication().invokeLater(new Runnable()
					{
						public void run()
						{
							if (noItems.getTag().equals(getRefreshCountTag()) == true);//this check and implementation of 'refreshcounttag' is to eliminate the duplicates, because of threading
							{
								content.add(noItems);
							}
						}
					});
				}
			}
		};
	}

	private void logSearch()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		FlurryHelper.addLoginParams(eventParams);			

		FlurryHelper.logEvent(FlurryHelper.EVENT_SEARCH_EEZI_COUPONS, eventParams, false);
	}

	private void logSearchResultTapped(CampaignData coupon)
	{
		if (!field_search.getText().equals(""))//only if result if a search text...
		{
			Hashtable eventParams = new Hashtable();

			eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
			eventParams.put(FlurryHelper.PARAM_CATEGORY, ((CouponCategory)coupon.getCategoryList().elementAt(0)).getId());
			eventParams.put(FlurryHelper.PARAM_PRODUCT_NAME, coupon.getId());
			eventParams.put(FlurryHelper.PARAM_COUPON_MONETARY_VALUE, coupon.getValue());
			FlurryHelper.addLoginParams(eventParams);			

			FlurryHelper.logEvent(FlurryHelper.EVENT_SEARCH_RESULT_EEZI_COUPONS, eventParams, false);	
		}
	}

	private void logCouponTapped(CampaignData coupon)
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_CATEGORY, ((CouponCategory)coupon.getCategoryList().elementAt(0)).getId());
		eventParams.put(FlurryHelper.PARAM_PRODUCT_NAME, coupon.getId());
		eventParams.put(FlurryHelper.PARAM_COUPON_MONETARY_VALUE, coupon.getValue());

		//related products...
		Vector relatedCoupons = getRelatedCoupons(coupon);
		String relatedS = "";
		for (int i = 0; i < relatedCoupons.size(); i++)
		{
			if (i == 0)
			{
				relatedS += ((CampaignData)relatedCoupons.elementAt(i)).getId();
			}
			else
			{
				relatedS += " , " + ((CampaignData)relatedCoupons.elementAt(i)).getId();
			}
		}
		eventParams.put(FlurryHelper.PARAM_RELATED_PRODUCT, relatedS);

		FlurryHelper.addLoginParams(eventParams);			

		FlurryHelper.logEvent(FlurryHelper.EVENT_SEARCH_RESULT_EEZI_COUPONS, eventParams, false);	
	}

	private Vector getRelatedCoupons(CampaignData couponToMatch)//copied from DetailedCouponFragment
	{
		Vector coupons = RuntimeStoreHelper.getCoupons(interfaceListener, true, "Loading", currentLocationId);
		Vector related = new Vector();
		for (int i = 0; i < coupons.size(); i++)
		{
			CampaignData coupon = (CampaignData) coupons.elementAt(i);
			if (!coupon.equals(couponToMatch) && VectorUtil.hasCommonElement(coupon.getCategoryList(), couponToMatch.getCategoryList()))
			{
				related.addElement(coupon);
				if (related.size() == 10) break;
			}
		}

		return related;
	}

	public void onCouponsFinishedLoading(boolean success) {
		if (success == true)
		{
			refresh();
		}
		else
		{
			int choice = CustomDialog.doModal("There was a problem trying to download the coupons...", new String[]{"Cancel", "Retry"}, new int[]{0,1}, 1, false);

			if (choice == 1)
			{
				refresh();
			}
			else 
			{
				close();
			}
		}
	}
}
