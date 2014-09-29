package fi.bb.checkers.ui.screens;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
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
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.helpers.SharingHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.ImageButton;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.SpecialField;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.fragments.DetailedSpecialFragment;
import fi.bb.checkers.ui.fragments.SpecialsFragment;
import fi.bb.checkers.utils.AsyncTask;
import fi.bb.checkers.utils.StringUtil;

public class ThemedSpecialsScreen extends MainScreen implements FieldChangeListener
{	
	private Vector dataset;

	private VerticalFieldManager content;
	private HorizontalFieldManager banner_manager;

	private ImageButton button_search;
	private TextInputField field_search;
	//private TextImageButton button_done;
	private TextImageButton button_view_all_specials;

	protected Bitmap shadow = ResourceHelper.getImage("top_gradient");

	Vector category_filter;
	LoadThread load_specials;

	String themedSpecialsCategoryId;

	VerticalFieldManager mainVerticalManager;

	int titleWidth;

	TitleField titleField;
	Font titleFont = ResourceHelper.helveticaMed().getFont(Font.PLAIN, ResourceHelper.convert(18), Ui.UNITS_px);

	private int refreshCount;
	private String refreshCountTag;
	private String refreshThreadTag = "REFRESH_THREAD_TOKEN";

	private Font fontNoItemsForSearch = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(16), Ui.UNITS_px);

	public ThemedSpecialsScreen(String categoryId)
	{
		super(0);

		this.themedSpecialsCategoryId = categoryId;

		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		setTitle(createTitleBar());

		mainVerticalManager = new VerticalFieldManager(VERTICAL_SCROLL|NO_HORIZONTAL_SCROLLBAR);
		add(mainVerticalManager);

		button_view_all_specials = new TextImageButton("View All Specials", "btn_default", "btn_grey_default");
		button_view_all_specials.setTextColor(ResourceHelper.color_white);
		button_view_all_specials.setTextColorHover(ResourceHelper.color_primary);
		button_view_all_specials.setTextColorPressed(ResourceHelper.color_primary);		
		button_view_all_specials.setMargin(ResourceHelper.convert(10), 0, ResourceHelper.convert(10), ResourceHelper.convert(10));
		button_view_all_specials.setChangeListener(this);

		content = new VerticalFieldManager(VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR | NO_HORIZONTAL_SCROLL);
		//content.setBackground(BackgroundFactory.createSolidBackground(0xe2e2e2));

		mainVerticalManager.add(content);
		mainVerticalManager.add(button_view_all_specials);

		LocationData loc;

		if (RuntimeStoreHelper.getSessionID() != null && PersistentStoreHelper.getSpecialsRegion() != null)
		{
			// if logged in user
			loc = PersistentStoreHelper.getSpecialsRegion();
		}
		else if (LocationHelper.location != null)
		{
			// get the GPS location
			loc = LocationHelper.location;
		}
		else
		{
			if (RuntimeStoreHelper.getProvinceLoggedOutUser() != null)
			{
				loc = RuntimeStoreHelper.getProvinceLoggedOutUser();
			}
			else
			{
				loc = null; //ask user to choose location
			}
		}

		if (loc != null)
		{
			initialThemedSpecialsSetup(loc);
		}
		else
		{
			selectProvinceForGuest();
		}
	}

	private void initialThemedSpecialsSetup(LocationData loc)
	{
		if ((RuntimeStoreHelper.getLastLocationIdUsedForSpecialsSearch() != null) && (RuntimeStoreHelper.getLastLocationIdUsedForSpecialsSearch().equals(loc.getId()) == true))
		{
			dataset = RuntimeStoreHelper.getSpecials();
			refresh();
		}
		else
		{
			new DownloadTask().execute(new Object[]{loc.getId()});
		}
	}

	private void selectProvinceForGuest()
	{
		UiApplication.getUiApplication().invokeLater(new Runnable() {

			public void run() {
				Vector selection = SelectionScreen.doModal("Select a Province", new Vector(), null, PersistentStoreHelper.getProvinces(), false, true);//RuntimeStoreHelper.getProvinces()
				if (selection != null &&  selection.size() > 0)
				{
					Vector province_selection = selection;
					RuntimeStoreHelper.setProvinceLoggedOutUser((LocationData) province_selection.elementAt(0));
					initialThemedSpecialsSetup((LocationData) province_selection.elementAt(0));
				}
				else
				{
					//selectProvinceForGuest();
					close();
				}
			}
		});
	}

	private Field createTitleBar()
	{
		VerticalFieldManager titleManager = new VerticalFieldManager();

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
		button_search.setMargin((banner_manager.getPreferredHeight() - button_search.getPreferredHeight()) / 2, 0, 0, 0);
		button_search.setChangeListener(this);

		/*button_done = new TextImageButton("Done", "btn_sml_default", "btn_sml_hover");
		button_done.setTextColor(ResourceHelper.color_white);
		button_done.setTextColorHover(ResourceHelper.color_primary);
		button_done.setTextColorPressed(ResourceHelper.color_primary);
		button_done.setMargin((banner_manager.getPreferredHeight() - button_done.getPreferredHeight()) / 2, 0, 0, 0);
		button_done.setChangeListener(this);*/

		String title = getCategoryName(themedSpecialsCategoryId);
		titleWidth = titleFont.getAdvance(title);

		titleField = new TitleField();
		titleField.setTitle(title);

		int titleLeftMargin = ((banner_manager.getPreferredWidth() - titleWidth)/2) - button_search.getPreferredWidth() - ResourceHelper.convert(5*2);
		titleField.setMargin((banner_manager.getPreferredHeight() - titleField.getPreferredHeight()) / 2, 0, 0, titleLeftMargin);

		banner_manager.add(button_search);
		banner_manager.add(titleField);
		//banner_manager.add(button_done);

		field_search = new TextInputField("Search", false, false, ResourceHelper.color_grey, 0)
		{
			public int getPreferredWidth()
			{
				return Display.getWidth() - ResourceHelper.convert(8);
			}
		};
		button_search.setPadding(0, ResourceHelper.convert(5), 0, ResourceHelper.convert(5));
		field_search.setMargin((banner_manager.getPreferredHeight() - field_search.getPreferredHeight()) / 2, 0, 0, ResourceHelper.convert(5));
		field_search.setChangeListener(this);

		titleManager.add(banner_manager);
		//titleManager.add(seperator);

		return titleManager;
	}

	private String getCategoryName(String categoryId)
	{
		if (PersistentStoreHelper.getSpecialCategories() != null)
		{
			for (int i = 0; i < PersistentStoreHelper.getSpecialCategories().size(); i++)
			{
				if (((CouponCategory)PersistentStoreHelper.getSpecialCategories().elementAt(i)).getId().equals(categoryId) == true)
				{
					return ((CouponCategory)PersistentStoreHelper.getSpecialCategories().elementAt(i)).getName();
				}
			}
		}
		return "";
	}

	public void fieldChanged(Field field, int context) 
	{
		if (field == button_search)
		{
			search();
		}
		else if (field == field_search)
		{
			refresh();
		}
		else if (field == button_view_all_specials)
		{
			logViewAllSpecialsClicked();
			close();
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SpecialsFragment.FRAGMENT_ID, null);
		}
		/*else if (field == button_done)
		{
			close();
		}*/
	}

	private void logViewAllSpecialsClicked()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		eventParams.put(FlurryHelper.PARAM_THEME_ID, themedSpecialsCategoryId);
		FlurryHelper.addLoginParams(eventParams);		
		eventParams.put(FlurryHelper.PARAM_TIME, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));

		FlurryHelper.logEvent(FlurryHelper.EVENT_THEME_VIEW_ALL_SPECIALS , eventParams, false);
	}

	public void refresh()
	{
		// create the fields in a separate thread so the screen can open while it populates
		String newRefreshTag = getNewRefreshTag();
		if (load_specials != null && load_specials.isAlive())
		{
			try
			{
				load_specials.stop();
				load_specials.join();
			} catch (InterruptedException e)
			{
			}
		}
		content.deleteAll();

		load_specials = new LoadThread(newRefreshTag);
		load_specials.start();
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

	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);

		MenuItem item = new MenuItem("Search", 0x00070000, 0)
		{
			public void run()
			{
				search();
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

		String menuImageText = (PersistentStoreHelper.shouldLoadImages() == true)?"Turn off images":"Turn on images";
		item = new MenuItem(menuImageText, 0x00050000, 1)
		{
			public void run()
			{
				PersistentStoreHelper.setshouldLoadImages(!PersistentStoreHelper.shouldLoadImages());
				refresh();
			}
		};
		menu.add(item);

		int index = content.getFieldWithFocusIndex();
		if (index != -1)
		{
			final SpecialField special_field = (SpecialField) content.getField(index);
			item = new MenuItem("Share", 0x00050000, 2)
			{
				public void run()
				{
					SharingHelper.shareSpecial(special_field.getSpecial());
				}
			};
			menu.add(item);
		}
	}

	private void search()
	{
		banner_manager.deleteAll();
		banner_manager.add(field_search);
		field_search.setFocus();
	}

	private class TitleField extends Field
	{
		private String title = "";

		public TitleField()
		{
			setFont(titleFont);
		}

		public void setTitle(String title)
		{
			String[] lines = StringUtil.ellipsize(getFont(), title, getPreferredWidth(), 1);
			if (lines.length != 0) title = lines[0];

			this.title = title;
			invalidate();
		}

		protected void layout(int width, int height)
		{
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		public int getPreferredWidth()
		{
			return titleWidth;
		}

		public int getPreferredHeight()
		{
			return ResourceHelper.convert(30);
		}

		protected void paint(Graphics graphics)
		{
			graphics.setColor(0xe2e2e2);
			graphics.fillRect(0, 0, getWidth(), getHeight());

			int x = (getWidth() - getFont().getAdvance(title)) / 2;
			int y = (getHeight() - getFont().getHeight()) / 2;
			graphics.setColor(ResourceHelper.color_black);
			graphics.drawText(title, x, y);
		}
	};

	private class LoadThread extends Thread
	{
		private volatile boolean run;
		private String threadTag;
		private boolean deletedAll = false;
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
			if (dataset == null) 
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
				return;
			}

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

			if (dataset.size() == 0)
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
				for (int i = 0; i < dataset.size(); i++)
				{
					CampaignData special = (CampaignData) dataset.elementAt(i);
					final SpecialField item = new SpecialField(special, FIELD_LEFT)
					{
						public void clickButton()
						{
							FragmentContainerScreen fcs = new FragmentContainerScreen("Specials Info", new DetailedSpecialFragment(getSpecial(), dataset, false), true);
							fcs.setIsThemedSpecialDetailScreen(true);
							UiApplication.getUiApplication().pushScreen(fcs);
						}
					};
					item.setTag(threadTag);
					item.setOrderTag(""+i);

					if (containsCategory(special) == true)
					{
						UiApplication.getUiApplication().invokeLater(new Runnable()
						{
							public void run()
							{									
								if (item.getTag().equals(getRefreshCountTag()) == true);//this check and implementation of 'refreshcounttag' is to eliminate the duplicates, because of threading
								{
									String search_term = "";

									if (field_search != null)
									{
										search_term = field_search.getText().toLowerCase();
									}

									int orderIndex = Integer.parseInt(item.getOrderTag());									
									if (search_term == null || ((search_term != null) && ((CampaignData)dataset.elementAt(orderIndex)).getName().toLowerCase().indexOf(search_term) != -1))
									{
										if (orderIndex >= content.getFieldCount())
										{
											content.add(item);
										}
										else
										{
											content.insert(item, orderIndex);
										}
									}
								}
							}
						});
					}
					if (!run) return;
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

	private boolean containsCategory(CampaignData special)
	{

		for (int i=0; i < special.getCategoryList().size(); i++)
		{
			if (((CouponCategory)special.getCategoryList().elementAt(i)).getId().equals(themedSpecialsCategoryId) == true)
			{
				return true;
			}
		}
		return false;
	}

	private class DownloadTask extends AsyncTask
	{
		LoadingDialog prompt;
		protected void onPreExecute()
		{
			super.onPreExecute();
			prompt = LoadingDialog.push("Loading");
		}

		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			prompt.close();

			if (result instanceof Vector)
			{
				dataset = (Vector) result;
				refresh();
			}
			else
			{
				String msg = ((Exception) result).getMessage();
				if (msg.length() == 0) msg = "An unexpected error occured.";
				InfoDialog.doModal("Error", msg, "Okay");

				dataset.removeAllElements();
				refresh();
			}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				return ServerHelper.getSpecialList((String) params[0]);

			} catch (Exception e)
			{
				RemoteLogger.log("ThemedSpecials", "DownloadTask: " + e.toString());
				return e;
			}

		}
	}

	public void close() {

		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_THEME_VIEWED);
		super.close();
	}

	protected boolean keyChar(char ch, int status, int time)
	{
		if (ch == Characters.ESCAPE && field_search.getManager() == banner_manager)
		{
			banner_manager.deleteAll();
			banner_manager.add(button_search);
			banner_manager.add(titleField);
			//banner_manager.add(button_done);

			field_search.setText("");
			refresh();

			return true;
		}

		return super.keyChar(ch, status, time);
	}
}
