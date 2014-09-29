package fi.bb.checkers.ui.fragments;

import java.util.Hashtable;
import java.util.Vector;

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
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.CampaignData;
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
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.screens.SelectionScreen;
import fi.bb.checkers.ui.screens.TutorialScreen;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.AsyncTask;

public class SpecialsFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();
	//private LocationData initial_province;
	private LocationData previous_province;
	private Vector dataset;
	private Vector province_selection;
	private VerticalFieldManager content;
	private HorizontalFieldManager banner_manager;

	private ImageButton button_search;
	private ImageButton button_filter;
	private TextInputField field_search;
	//private ToggleSelectionField toggleFieldShowImages;

	private LoadThread load_specials;

	private int refreshCount;
	private String refreshCountTag;
	private String refreshThreadTag = "REFRESH_THREAD_TOKEN";
	private boolean closeImmediately = false;
	private Font fontNoItemsForSearch = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(16), Ui.UNITS_px);

	public SpecialsFragment()
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		logBrowseSpecials();

		refreshCount = -1;

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

		/*toggleFieldShowImages = new ToggleSelectionField("Show images");
		toggleFieldShowImages.setPrefferedWidth(ResourceHelper.convert(150));*/

		button_search.setMargin((banner_manager.getPreferredHeight() - button_search.getPreferredHeight()) / 2, 0, 0, 0);
		button_filter.setMargin((banner_manager.getPreferredHeight() - button_filter.getPreferredHeight()) / 2, 0, 0, 0);

		//toggleFieldShowImages.setMargin((banner_manager.getPreferredHeight() - toggleFieldShowImages.getPreferredHeight()) / 2, 0, 0, 0);

		button_search.setChangeListener(this);
		button_filter.setChangeListener(this);

		//toggleFieldShowImages.setChangeListener(this);

		banner_manager.add(button_search);
		banner_manager.add(button_filter);

		//banner_manager.add(toggleFieldShowImages);

		add(banner_manager);

		content = new VerticalFieldManager(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL)
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
		button_search.setPadding(0, ResourceHelper.convert(5), 0, ResourceHelper.convert(5));
		field_search.setMargin((banner_manager.getPreferredHeight() - field_search.getPreferredHeight()) / 2, 0, 0, ResourceHelper.convert(4));
		field_search.setChangeListener(this);

		province_selection = new Vector();

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
		
		//TEMP testing
		/*if (RuntimeStoreHelper.getProvinceLoggedOutUser() != null)
		{
			loc = RuntimeStoreHelper.getProvinceLoggedOutUser();
		}
		else
		{
			loc = null; //ask user to choose location
		}*/

		if (loc != null)
		{
			initialSpecialsSetup(loc);
		}
		else
		{
			selectProvinceForGuest();
		}
	}
	
	private void initialSpecialsSetup(LocationData loc)
	{
		previous_province = loc;
		province_selection.addElement(loc);

		RemoteLogger.log("TEST_SPECIALS_RUNTIME", "specials fragment: RuntimeStoreHelper.getLastLocationIdUsedForSpecialsSearch(): " + RuntimeStoreHelper.getLastLocationIdUsedForSpecialsSearch() + " loc: " + loc.getId() + ":" + loc.getDesc());

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
				if (selection != null)
				{

					province_selection = selection;
					RuntimeStoreHelper.setProvinceLoggedOutUser((LocationData) province_selection.elementAt(0));
					initialSpecialsSetup((LocationData) province_selection.elementAt(0));
				}
				else
				{
					//selectProvinceForGuest();
					closeImmediately = true;
					close();
				}
			}
		});
	}

	protected void onVisibilityChange(boolean visible)
	{
		if (visible && closeImmediately == false)
		{
			if (PersistentStoreHelper.isShowTutorial4())
			{
				TutorialScreen.push(TutorialScreen.SPECIALS);
				PersistentStoreHelper.setShowTutorial4(false);
			}
		}
		super.onVisibilityChange(visible);
	}

	public void onClose()
	{
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_SPECIALS_VIEW_ALL);
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_BROWSE_SPECIALS);
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_CONTENT_BLOCK_VIEW);

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
	}

	public void refresh()
	{
		// create the fields in a separate thread so the screen can open while it populates
		String newRefreshTag = getNewRefreshTag();
		if (load_specials != null)
		{
			if (load_specials.isAlive())
			{
				try
				{
					load_specials.stop();
					load_specials.join();
				} catch (InterruptedException e)
				{
				}
			}
		}

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

	public void makeMenu(Menu menu)
	{
		MenuItem item = new MenuItem("Search", 0x00070000, 0)
		{
			public void run()
			{
				search();
			}
		};
		menu.add(item);

		item = new MenuItem("Filter", 0x00070000, 1)
		{
			public void run()
			{
				filter();
			}
		};
		menu.add(item);

		String menuImageText = (PersistentStoreHelper.shouldLoadImages() == true)?"Turn off images":"Turn on images";
		item = new MenuItem(menuImageText, 0x00070000, 2)
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
			final SpecialField special_field = (SpecialField) content.getField(index);
			item = new MenuItem("Share", 0x00050000, 1)
			{
				public void run()
				{
					SharingHelper.shareSpecial(special_field.getSpecial());
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
		else if (field == field_search)
		{
			refresh();
		}
		/*else if (field == toggleFieldShowImages)
		{
			toggleFieldShowImages.setChecked(!toggleFieldShowImages.isChecked());
			RemoteLogger.log("TOGGLE IMAGES", "bool: |" + toggleFieldShowImages.isChecked()+"|");
		}*/
	}

	private void search()
	{
		banner_manager.deleteAll();
		banner_manager.add(field_search);
		field_search.setFocus();

		logSearch();
	}

	private void filter()
	{
		Vector defaultstate = new Vector();
		//defaultstate.addElement(initial_province);
		defaultstate.addElement(previous_province);

		Vector selection = SelectionScreen.doModal("Change Province", this.province_selection, defaultstate, PersistentStoreHelper.getProvinces(), false, true);//RuntimeStoreHelper.getProvinces()
		if (selection != null)
		{

			this.province_selection = selection;
			LocationData loc = (LocationData) province_selection.elementAt(0);

			if (loc.equals(previous_province))
			{
				button_filter.setImage("icon_filter_default", "icon_filter_hover");
			}
			else
			{
				button_filter.setImage("icon_filter_applied", "icon_filter_hover");
			}
			previous_province = loc;//update previous province to the new one selected
			logFilter(loc);
			new DownloadTask().execute(new Object[]{loc.getId()});
		}
	}

	private void logBrowseSpecials()
	{
		Hashtable eventParams = new Hashtable();

		FlurryHelper.addProvinceParam(eventParams);
		FlurryHelper.addLoginParams(eventParams);		

		FlurryHelper.logEvent(FlurryHelper.EVENT_BROWSE_SPECIALS, eventParams, true);
	}

	private void logFilter(LocationData loc)
	{
		Hashtable eventParams = new Hashtable();

		FlurryHelper.addLoginParams(eventParams);	
		eventParams.put(FlurryHelper.PARAM_PROVINCE, loc.getId());

		FlurryHelper.logEvent(FlurryHelper.EVENT_FILTER_SPECIALS, eventParams, false);
	}

	private void logSearch()
	{
		Hashtable eventParams = new Hashtable();
		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");	

		FlurryHelper.logEvent(FlurryHelper.EVENT_SEARCH_SPECIALS, eventParams, false);
	}

	private void logSearchResultTapped(CampaignData special)
	{
		if (!field_search.getText().equals(""))//only if result if a search text...
		{
			Hashtable eventParams = new Hashtable();

			eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
			eventParams.put(FlurryHelper.PARAM_PRODUCT_NAME, special.getId());
			eventParams.put(FlurryHelper.PARAM_COUPON_MONETARY_VALUE, special.getValue());

			FlurryHelper.logEvent(FlurryHelper.EVENT_SEARCH_RESULT_SPECIALS, eventParams, false);
		}
	}

	protected boolean keyChar(char ch, int status, int time)
	{
		if (ch == Characters.ESCAPE && field_search.getManager() == banner_manager)
		{
			banner_manager.deleteAll();
			banner_manager.add(button_search);
			banner_manager.add(button_filter);

			field_search.setText("");
			refresh();

			return true;
		}
		/*else if (ch == Characters.ESCAPE)
		{
			logViewAllSpecials();
		}*/

		return super.keyChar(ch, status, time);
	}

	protected void onUndisplay()
	{
		super.onUndisplay();
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
					if (!run) return;
					CampaignData special = (CampaignData) dataset.elementAt(i);
					final SpecialField item = new SpecialField(special, FIELD_LEFT)
					{
						public void clickButton()
						{
							logSearchResultTapped(getSpecial());
							((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(DetailedSpecialFragment.FRAGMENT_ID, new Object[]{getSpecial(), dataset});
						}
					};
					item.setTag(threadTag);
					item.setOrderTag(""+i);

					UiApplication.getUiApplication().invokeLater(new Runnable()
					{
						public void run()
						{
							if (item.getTag().equals(getRefreshCountTag()) == true);//this check and implementation of 'refreshcounttag' is to eliminate the duplicates, because of threading
							{
								String search_term = field_search.getText().toLowerCase();
								if (search_term == null || item.getSpecial().getName().toLowerCase().indexOf(search_term) != -1)
								{
									/*int orderIndex = Integer.parseInt(item.getOrderTag());
								if (orderIndex >= content.getFieldCount())
								{
									content.add(item);
								}
								else
								{
									content.insert(item, orderIndex);
								}*/
									content.add(item);//I removed this ordering thing for now, since when it is a big list loads funny.
								}	
							}
						}
					});
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
				RemoteLogger.log("SpecialsFragment", "DownloadTask: " + e.toString());
				return e;
			}

		}
	}
}
