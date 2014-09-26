package fi.bb.checkers.ui.fragments;

import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.SimpleSortingVector;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.datatypes.MerchantData;
import fi.bb.checkers.datatypes.comparators.MerchantAlphabeticComparator;
import fi.bb.checkers.datatypes.comparators.MerchantDistanceComparator;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.LocationHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.interfaces.InterfaceStoreChanged;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.StoreItemField;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.screens.ViewPagerScreen;
import fi.bb.checkers.utils.AsyncTask;

public class SelectStoreFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();
	private Vector stores;
	private LoadThread load_stores;
	private TextInputField search_field;
	private VerticalFieldManager content;

	private String startText;

	boolean fromProfileScreen;
	InterfaceStoreChanged storeChangedInterface;

	public SelectStoreFragment(LocationData province, InterfaceStoreChanged storeChangedInterface)
	{
		this(province, null, storeChangedInterface);
	}

	public SelectStoreFragment(LocationData province)
	{
		this(province, null, null);
	}

	public SelectStoreFragment(String text)
	{
		this(null, text, null);
	}

	public SelectStoreFragment(LocationData province, String text, InterfaceStoreChanged storeChangedInterface)
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		this.fromProfileScreen = (storeChangedInterface==null)?false:true;
		this.storeChangedInterface = storeChangedInterface;

		if (fromProfileScreen == false)
		{
			search_field = new TextInputField("Search", false, false, ResourceHelper.color_grey, 0);
			int padding_vertical = (ResourceHelper.convert(30) - search_field.getPreferredHeight()) / 2;
			int padding_horizontal = ResourceHelper.convert(4);
			search_field.setPadding(padding_vertical, padding_horizontal, padding_vertical, padding_horizontal);

			search_field.setChangeListener(this);
			add(search_field);

			startText = "";

			if (text!=null && !text.equals(""))
			{
				startText = text;
			}
		}

		content = new VerticalFieldManager();
		add(content);

		downloadTask.execute(new Object[]{province});
	}

	public void refresh()
	{
		if (load_stores != null && load_stores.isAlive()) load_stores.stop();
		content.deleteAll();

		load_stores = new LoadThread();
		load_stores.start();
	}

	private void logSearch()
	{
		Hashtable eventParams = new Hashtable();

		eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
		FlurryHelper.addLoginParams(eventParams);
		FlurryHelper.addProvinceParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_SEARCH_STORES, eventParams, false);
	}

	private void logSearchResultTapped(MerchantData merchant)
	{
		if (!search_field.getText().equals(""))//only if result if a search text...
		{
			Hashtable eventParams = new Hashtable();

			eventParams.put(FlurryHelper.PARAM_TAPPED, "1");
			eventParams.put(FlurryHelper.PARAM_STORE_ID, merchant.getId());

			try//when all provinces, merchant province is null
			{
				eventParams.put(FlurryHelper.PARAM_PROVINCE, merchant.getProvince());
			}
			catch (Exception e)
			{}

			FlurryHelper.logEvent(FlurryHelper.EVENT_SEARCH_RESULTS_FIND_A_STORE, eventParams, false);	
		}
	}

	public void onClose()
	{
		if (load_stores != null && load_stores.isAlive()) load_stores.stop();
	}

	public void fieldChanged(Field field, int context)
	{
		if ((search_field != null) && (field == search_field))
		{
			//logSearch();//unsure about when to log since there is no search button, took it out as it is not logged in iOS
			refresh();
		}
		else
		{
			if (fromProfileScreen == false)
			{
				try
				{
					logSearchResultTapped(((MerchantData)((StoreItemField) field).getObject()));
					((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(StoreInfoFragment.FRAGMENT_ID, new Object[]{((StoreItemField) field).getObject()});
				} catch (Throwable e)
				{
					RemoteLogger.log("SelectStoreFragment", e.toString());
				}
			}
			else
			{
				if (storeChangedInterface != null)
				{
					storeChangedInterface.onStoreChanged(((MerchantData)((StoreItemField) field).getObject()));
				}
			}			
		}
	}

	private AsyncTask downloadTask = new AsyncTask()
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
				stores = (Vector) result;
				if (fromProfileScreen == false)
				{
					if (!startText.equals(""))
					{
						search_field.setText(startText);//this onfieldchange should  call refresh now...
						search_field.setFocus();
					}
					else
					{
						refresh();
					}
				}
				else
				{
					refresh();
				}
			}
			else
			{
				String msg = ((Exception) result).getMessage();
				if (msg.length() == 0) msg = "An unexpected error occured.";
				InfoDialog.doModal("Error", msg, "Okay");
				close();
			}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				SimpleSortingVector validstores = new SimpleSortingVector();
				validstores.setSort(false);
				Vector allstores;
				if (params[0] == null)
				{
					allstores = ServerHelper.getStores(null);//get all provinces' stores
				}
				else
				{
					allstores = ServerHelper.getStores((LocationData) params[0]);
				}

				for (int i = 0; i < allstores.size(); i++)
				{
					validstores.addElement(allstores.elementAt(i));
				}

				if (LocationHelper.coords != null)
				{
					validstores.setSortComparator(new MerchantDistanceComparator());
				}
				else
				{
					validstores.setSortComparator(new MerchantAlphabeticComparator());
				}
				validstores.reSort();

				return validstores;
			} catch (Exception e)
			{
				return e.getMessage();
			}
		}
	};

	private class LoadThread extends Thread
	{
		private volatile boolean run;
		public LoadThread()
		{
			run = true;
			setPriority(Thread.MIN_PRIORITY);
		}

		public void stop()
		{
			run = false;
		}

		public void run()
		{
			if(fromProfileScreen == false)
			{
				String search_term = search_field.getText().toLowerCase();
				for (int i = 0; i < stores.size(); i++)
				{
					MerchantData store = (MerchantData) stores.elementAt(i);

					if (search_term == null || store.getName().toLowerCase().indexOf(search_term) != -1)
					{
						StoreItemField field = new StoreItemField(store);
						field.setChangeListener(SelectStoreFragment.this);

						synchronized (Application.getEventLock())
						{
							if (!run) return;
							content.add(field);
						}
					}
					if (!run) return;
				}
			}
			else
			{
				for (int i = 0; i < stores.size(); i++)
				{
					MerchantData store = (MerchantData) stores.elementAt(i);

					StoreItemField field = new StoreItemField(store);
					field.setChangeListener(SelectStoreFragment.this);

					synchronized (Application.getEventLock())
					{
						if (!run) return;
						content.add(field);
					}
					if (!run) return;
				}
			}
		};
	}
}
