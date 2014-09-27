package fi.bb.checkers.ui.screens;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.SimpleSortingVector;
import fi.bb.checkers.MainApplication;
import fi.bb.checkers.datatypes.FeaturedData;
import fi.bb.checkers.datatypes.comparators.FeaturedDataComparator;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.RuntimeStoreHelper;
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.network.PINException;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.utils.AsyncTask;

public class SplashScreen extends MainScreen
{
	public SplashScreen()
	{
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		getMainManager().setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_checkers_splash));

		BitmapField splash_logo = new BitmapField(ResourceHelper.getImage("splash"), Field.FIELD_HCENTER);
		splash_logo.setMargin((Display.getHeight() - splash_logo.getBitmapHeight())/2, 0, 0, 0);

		add(splash_logo);

		new StartupTask().execute(null);
	}

	private class StartupTask extends AsyncTask
	{
		protected void onPostExecute(Object result)
		{
			if (!"success".equals(result))
			{
				InfoDialog.doModal("Error", (String) result, "Okay");
				PersistentStoreHelper.setUsername("");
				PersistentStoreHelper.setPIN("");

				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						new StartupTask().execute(null);
					}
				});

			}
			else
			{
				//When open app, force to not load images when on BIS connection (user can still change this setting, when using the app)
				/*if (WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED)
				{
					PersistentStoreHelper.setshouldLoadImages(true);
				}
				else if (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B) == true)
				{
					PersistentStoreHelper.setshouldLoadImages(false);
				}
				else 
				{
					PersistentStoreHelper.setshouldLoadImages(true);
				}*/

				//New image rule, don't show images by default, user should select "turn images on"
				PersistentStoreHelper.setshouldLoadImages(false);

				if (RuntimeStoreHelper.getSessionID() != null)
				{
					ViewPagerScreen.push();
				}
				else
				{
					if (PersistentStoreHelper.showWelcome())
					{
						((MainApplication) UiApplication.getUiApplication()).slideScreen(new WelcomeScreen());
					}
					else
					{
						ViewPagerScreen.push();
					}
				}
				close();
			}
		}

		public Object doInBackground(Object[] params)
		{
			long startTime;
			long endTime;
			try
			{
				if (PersistentStoreHelper.hasLoginDetails())
				{
					try
					{
						tryLogin(PersistentStoreHelper.getUsername(), PersistentStoreHelper.getPIN());
					}
					catch (PINException e)
					{
						RemoteLogger.log("SplashScreen", "1StartupTaskPINException e: " + e.toString());	
						handlePINException();
					}
				}
				else
				{ // see if theres an update, call doesnt handle login exceptions
					String[] update_response = ServerHelper.getUpdateUrl();
					RemoteLogger.log("SplashScreen", "update_response: " + update_response);
					if (update_response != null)
					{
						promptUpdate(update_response);
					}
				}

				int retry = 2;
				boolean updatePersistentDataSuccess = doPersistentDataUpdateSuccess();

				if (updatePersistentDataSuccess == false)
				{
					while( (retry > 0) && (updatePersistentDataSuccess==false))
					{
						retry--;
						updatePersistentDataSuccess = doPersistentDataUpdateSuccess();
					}
				}

				if (updatePersistentDataSuccess == false)//stop splash loading process here, since this data is needed throughout the app, and only updated monthly, and at least once on app first launch
				{
					return "Couldn't load app data, please make sure you have a good internet connection.";
				}

				//ServerHelper.getCouponList();
				ServerHelper.getTotalSavings();
				RemoteLogger.log("SAVINGS", "savings in cents: |" + PersistentStoreHelper.getTotalSavings() + "|");

				// --------- sharepoint ------------
				startTime = System.currentTimeMillis();
				ServerHelper.getFeaturedData();
				endTime = System.currentTimeMillis();
				RemoteLogger.log("GET_FEATURED", "time: mili: " + (endTime-startTime) + " seconds: " + ((endTime-startTime)/1000));

				startTime = System.currentTimeMillis();
				SimpleSortingVector vector = new SimpleSortingVector();
				vector.setSortComparator(new FeaturedDataComparator());

				Calendar todayC = Calendar.getInstance();
				todayC.set(Calendar.HOUR, 0);
				todayC.set(Calendar.MINUTE, 0);
				todayC.set(Calendar.SECOND, 0);
				todayC.set(Calendar.MILLISECOND, 0);

				Calendar checkDateC;

				for (int i = 0; i < RuntimeStoreHelper.getFeaturedList().size(); i++)
				{
					if (((FeaturedData)RuntimeStoreHelper.getFeaturedList().elementAt(i)).getAction().equals("THEME_SPECIALS"))
					{
						//Rule: if no date ---> ((date==null OR (date!=null AND date=="")) show date
						// 		if date ---> check that date is today, or before today
						if((((FeaturedData)RuntimeStoreHelper.getFeaturedList().elementAt(i)).getLiveDate() == null) || ((((FeaturedData)RuntimeStoreHelper.getFeaturedList().elementAt(i)).getLiveDate() != null) && (((FeaturedData)RuntimeStoreHelper.getFeaturedList().elementAt(i)).getLiveDate().equals(""))))
						{
							vector.addElement(RuntimeStoreHelper.getFeaturedList().elementAt(i));
						}
						else
						{
							try
							{
								checkDateC = Calendar.getInstance();
								checkDateC.setTime(new Date(HttpDateParser.parse(((FeaturedData)RuntimeStoreHelper.getFeaturedList().elementAt(i)).getLiveDate())));
								checkDateC.set(Calendar.HOUR, 0);
								checkDateC.set(Calendar.MINUTE, 0);
								checkDateC.set(Calendar.SECOND, 0);
								checkDateC.set(Calendar.MILLISECOND, 0);

								if (mayShowFeaturedItem(todayC, checkDateC) == true)
								{
									vector.addElement(RuntimeStoreHelper.getFeaturedList().elementAt(i));
								}
							}
							catch (Exception e)
							{
							}
						}
					}
					else
					{
						vector.addElement(RuntimeStoreHelper.getFeaturedList().elementAt(i));
					}

				}
				vector.reSort();
				RuntimeStoreHelper.setFeaturedList(vector);
				endTime = System.currentTimeMillis();
				RemoteLogger.log("SORT_FEATURED_LIST", "time: mili: " + (endTime-startTime) + " seconds: " + ((endTime-startTime)/1000));

			} catch (Exception e)
			{
				RemoteLogger.log("SplashScreen", "2StartupTask: " + e.toString());
				//e.printStackTrace();
				return e.getMessage();
			}

			return "success";
		}
	}

	private void tryLogin(String userName, String PIN)throws IOException, PINException
	{
		RemoteLogger.log("SplashScreen", "tryLogin");
		String[] update_response = ServerHelper.login(userName, PIN);
		RemoteLogger.log("SplashScreen", "update_response: " + update_response);

		if (update_response != null)
		{
			try
			{
				promptUpdate(update_response);
			}
			catch (Exception e) {
				RemoteLogger.log("CHECKERS", "STARTUP get user promptUpdate: e: " + e.getMessage());	
			}
		}

		try
		{
			ServerHelper.getUserDetails();
		}
		catch (Exception e) {
			RemoteLogger.log("CHECKERS", "STARTUP get user details: e: " + e.getMessage());	
		}

	}

	private void handlePINException()
	{	
		// catch error 026, pin incorrect // see if theres an update, call doesnt handle login exceptions
		String[] update_response = ServerHelper.getUpdateUrl();
		if (update_response != null)
		{
			promptUpdate(update_response);
		}
	}

	private boolean doPersistentDataUpdateSuccess()
	{
		boolean persistentDataFine = true;
		try
		{
			if (PersistentStoreHelper.shouldUpdateProvincesPersistentData() == true)
			{
				ServerHelper.getProvinces();
			}

			if (PersistentStoreHelper.shouldUpdateCouponCategoriesPersistentData() == true)
			{
				ServerHelper.getCouponCategories();
			}

			if (PersistentStoreHelper.shouldUpdateSpecialsPersistentData() == true)
			{
				ServerHelper.getSpecialCategories();
			}

			if (PersistentStoreHelper.shouldUpdateFeedbackTypesPersistentData() == true)
			{
				ServerHelper.getFeedbackTypes();	
			}

			if (PersistentStoreHelper.shouldUpdateTitlesPersistentData() == true)
			{
				ServerHelper.getTitles();
			}

			//Check if this is the first time and one of the persistent data should retry...
			//because if it's not the first time, and there is available persistent data, no retry is neccessary, next launch can retry...
			if ( (PersistentStoreHelper.shouldUpdateProvincesPersistentData() == true) && ( (PersistentStoreHelper.getProvinces() == null) || ((PersistentStoreHelper.getProvinces() != null) && (PersistentStoreHelper.getProvinces().size() == 0) ) ) )
			{
				persistentDataFine = false;
			}
			else if ( (PersistentStoreHelper.shouldUpdateCouponCategoriesPersistentData() == true) && ( (PersistentStoreHelper.getCouponCategories() == null) || ((PersistentStoreHelper.getCouponCategories() != null) && (PersistentStoreHelper.getCouponCategories().size() == 0) ) ) )
			{
				persistentDataFine = false;
			}
			else if ( (PersistentStoreHelper.shouldUpdateSpecialsPersistentData() == true) && ( (PersistentStoreHelper.getSpecialCategories() == null) || ((PersistentStoreHelper.getSpecialCategories() != null) && (PersistentStoreHelper.getSpecialCategories().size() == 0) ) ) )
			{
				persistentDataFine = false;
			}
			else if ( (PersistentStoreHelper.shouldUpdateFeedbackTypesPersistentData() == true) && ( (PersistentStoreHelper.getFeedbackTypes() == null) || ((PersistentStoreHelper.getFeedbackTypes() != null) && (PersistentStoreHelper.getFeedbackTypes().size() == 0) ) ) )
			{
				persistentDataFine = false;
			}
			else if ( (PersistentStoreHelper.shouldUpdateTitlesPersistentData() == true) && ( (PersistentStoreHelper.getTitles() == null) || ((PersistentStoreHelper.getTitles() != null) && (PersistentStoreHelper.getTitles().size() == 0) ) ) )
			{
				persistentDataFine = false;
			}

			//again this will only be false when first app launch has a fail in one of the calls above
			return persistentDataFine;
		}
		catch (Exception e)
		{
			RemoteLogger.log("doPersistentDataUpdateSuccess", "Exception e: " + e.getMessage());
			return false;
		}
	}

	private boolean mayShowFeaturedItem(Calendar todayC, Calendar checkDateC)
	{
		if (todayC.getTime().getTime() == checkDateC.getTime().getTime())//date is today
		{
			return true;
		}
		else if (checkDateC.before(todayC) == true)//if back in time
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void promptUpdate(final String[] update_response)
	{
		RemoteLogger.log("StartUpTask", "promptUpdate: update_response: " + update_response);
		UiApplication.getUiApplication().invokeAndWait(new Runnable()
		{
			public void run()
			{
				RemoteLogger.log("StartUpTask", "promptUpdate: update_response[0]: " + update_response[0]);
				int choice = CustomDialog.doModal(update_response[0], new String[]{"Remind me later", "Update now"}, new int[]{Dialog.CANCEL, Dialog.YES});
				if (choice == Dialog.YES)
				{
					Browser.getDefaultSession().displayPage(update_response[1]);
					System.exit(0);
				}
			}
		});
	}
}
