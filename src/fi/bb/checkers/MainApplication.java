package fi.bb.checkers;

import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TransitionContext;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngineInstance;
import net.rim.device.api.ui.VirtualKeyboard;

import com.flurry.blackberry.FlurryAgent;

import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.SharingHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.network.HttpInterface;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.ui.screens.SplashScreen;
import fi.bb.checkers.utils.FileClass;

//public class MainApplication extends PushUIApplication
public class MainApplication extends UiApplication
{
	private static final String BLACKBERRY_PUSH_APPLICATION_ID = "4827-6oo89h8l8R7D5804y479r36f0063i49l145";
	private static final String BLACKBERRY_PUSH_URL = "https://cp4827.pushapi.eval.blackberry.com";
	private static final int BLACKBERRY_PUSH_PORT = 35050;
	private static final String PUSH_WOOSH_APPLICATION_ID = "50F76-D1834";

	public MainApplication()
	{
		//super(BLACKBERRY_PUSH_APPLICATION_ID, BLACKBERRY_PUSH_URL, BLACKBERRY_PUSH_PORT, PUSH_WOOSH_APPLICATION_ID, "widdle_icon.png", "uaicon.png", "uaiconAlert.png", "/cash.mp3");
	}
	
	public void activate() {
		super.activate();
	}
	
	public void deactivate() {		
		super.deactivate();
	}

	public void exit(int status)
	{
		PersistentStoreHelper.setAppFirstLaunch(false);
		RemoteLogger.log("MainApplication", "APP EXIT : status " + status);
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_OPEN_APP);//just to make sure it is ended, should have ended on homescreen i.e. viewpager.
		FlurryAgent.onDestroyApp();
		System.exit(status);
	}

	public static void main(String[] args)
	{		
		MainApplication app = new MainApplication();

		app.promptPermissions();
		if (args.length > 0 && args[0].equals("autostartup"))
		{
			// Create background process on device restart, no UI
			app.enterEventDispatcher();
		}
		else
		{
			SharingHelper.init();
			FlurryHelper.init();

			//Open app event
			Hashtable eventParams = new Hashtable();
			FlurryHelper.logEvent(FlurryHelper.EVENT_OPEN_APP, eventParams, true);//will set params later in home...
			//Open app event

			FileConnection file = null;
			try
			{
				file = (FileConnection) Connector.open(FileClass.FILEPATH);
				if (!file.exists()) file.mkdir();
				file.close();
			} catch (Exception e)
			{
				RemoteLogger.log("MainApplication", "Error creating file application folder");
			}

			FlurryAgent.appendToReportUrl(HttpInterface.getConnectionString());
			FlurryAgent.onStartApp(FlurryHelper.FLURRY_KEY);

			int direction = Display.DIRECTION_NORTH;
			Ui.getUiEngineInstance().setAcceptableDirections(direction);

			app.showGUI();
		}
	}

	public void showGUI()
	{
		//beforeShowGUI();//TODO add back if pushwoosh is implemented again

		SplashScreen splashscreen = new SplashScreen();

		fadeScreen(splashscreen, false);

		// Prompt for app permissions
		promptPermissions();

		// Enter event dispatcher
		enterEventDispatcher();

		// Handle any inbound notifications
		//handleNotifications(); //TODO add back if pushwoosh is implemented again
	}

	/**
	 * Prompt for app permissions
	 */
	private void promptPermissions()
	{
		ApplicationPermissionsManager apm = ApplicationPermissionsManager.getInstance();
		ApplicationPermissions ap = apm.getApplicationPermissions();

		boolean permissionsOK;
		if (ap.getPermission(ApplicationPermissions.PERMISSION_FILE_API) == ApplicationPermissions.VALUE_ALLOW
				&& ap.getPermission(ApplicationPermissions.PERMISSION_INTERNET) == ApplicationPermissions.VALUE_ALLOW
				&& ap.getPermission(ApplicationPermissions.PERMISSION_WIFI) == ApplicationPermissions.VALUE_ALLOW)
		{
			permissionsOK = true;
		}
		else
		{
			ap.addPermission(ApplicationPermissions.PERMISSION_FILE_API);
			ap.addPermission(ApplicationPermissions.PERMISSION_INTERNET);
			ap.addPermission(ApplicationPermissions.PERMISSION_WIFI);

			permissionsOK = apm.invokePermissionsRequest(ap);
		}

		if (!permissionsOK)
		{
			invokeAndWait(new Runnable()
			{
				public void run()
				{
					try
					{
						InfoDialog.doModal("", "Insufficient Permissions to run Checkers. The application will now exit.", "Okay");
					} catch (Exception e)
					{
					}
					requestForeground();
				}
			});
			System.exit(0);
		}
	}

	public void slideScreen(Screen theScreen)
	{
		TransitionContext transition = new TransitionContext(TransitionContext.TRANSITION_SLIDE);

		transition.setIntAttribute(TransitionContext.ATTR_DURATION, 200);
		transition.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_LEFT);
		transition.setIntAttribute(TransitionContext.ATTR_STYLE, TransitionContext.STYLE_PUSH);

		UiEngineInstance engine = Ui.getUiEngineInstance();
		engine.setTransition(null, theScreen, UiEngineInstance.TRIGGER_PUSH, transition);

		transition = new TransitionContext(TransitionContext.TRANSITION_SLIDE);
		transition.setIntAttribute(TransitionContext.ATTR_DURATION, 200);
		transition.setIntAttribute(TransitionContext.ATTR_DIRECTION, TransitionContext.DIRECTION_RIGHT);
		transition.setIntAttribute(TransitionContext.ATTR_STYLE, TransitionContext.STYLE_PUSH);

		engine.setTransition(theScreen, null, UiEngineInstance.TRIGGER_POP, transition);
		super.pushScreen(theScreen);
	}

	/**
	 * 
	 * @param theScreen
	 * @param modal
	 */
	public void fadeScreen(Screen theScreen, boolean modal)
	{
		TransitionContext transition = new TransitionContext(TransitionContext.TRANSITION_FADE);
		transition.setIntAttribute(TransitionContext.ATTR_DURATION, 200);
		transition.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_IN);

		UiEngineInstance engine = Ui.getUiEngineInstance();
		engine.setTransition(null, theScreen, UiEngineInstance.TRIGGER_PUSH, transition);

		transition = new TransitionContext(TransitionContext.TRANSITION_FADE);
		transition.setIntAttribute(TransitionContext.ATTR_DURATION, 200);
		transition.setIntAttribute(TransitionContext.ATTR_KIND, TransitionContext.KIND_OUT);

		engine.setTransition(theScreen, null, UiEngineInstance.TRIGGER_POP, transition);

		if (modal)
		{
			super.pushModalScreen(theScreen);
		}
		else
		{
			super.pushScreen(theScreen);
		}

	}

	public void hideKeyboard()
	{
		try
		{
			VirtualKeyboard keyboard = getActiveScreen().getVirtualKeyboard();
			keyboard.setVisibility(VirtualKeyboard.HIDE);
		} catch (Exception e)
		{
		}
	}

	public void showKeyboard()
	{
		try
		{
			VirtualKeyboard keyboard = getActiveScreen().getVirtualKeyboard();
			keyboard.setVisibility(VirtualKeyboard.SHOW);
		} catch (Exception e)
		{
		}
	}

	/** @return TRUE if the virtual keyboard is hidden, or not supported */
	public boolean isKeyboardHidden()
	{
		if (VirtualKeyboard.isSupported())
		{
			VirtualKeyboard kb = getActiveScreen().getVirtualKeyboard();
			if (kb != null)
			{
				int visibility = kb.getVisibility();
				return ((visibility == VirtualKeyboard.HIDE) || (visibility == VirtualKeyboard.HIDE_FORCE));
			}
		}
		return true;
	}

	protected void handleNotification(String arg0)
	{
		try
		{
			RemoteLogger.log("PushWhoosh", "handleNotification: " + arg0);
		} catch (Exception e)
		{
		}
	}

	public void onCustomDataReceive(String arg0)
	{
		try
		{
			RemoteLogger.log("PushWhoosh", "onCustomDataReceive: " + arg0);
		} catch (Exception e)
		{
		}
	}

	/*public void onEvent(Event arg0)
	{
		try
		{
			RemoteLogger.log("PushWhoosh", "onEvent: " + arg0.toString());
		} catch (Exception e)
		{
		}
	}

	public void onPushServiceStatusChange(PushStatus arg0)
	{
		try
		{
			RemoteLogger.log("PushWhoosh", "onPushServiceStatusChange: " + getStatusMessage(arg0));
		} catch (Exception e)
		{
		}
	}

	private String getStatusMessage(PushStatus status)
	{
		String statusMessage = "";
		switch (status.getStatus())
		{
		// bb statuses
		case PushStatus.STATUS_BB_ACTIVE :
			statusMessage += "Application registered with BlackBerry. Registering with Pushwoosh...";
			break;
		case PushStatus.STATUS_BB_FAILED :
			statusMessage += "Subscription status failed.\n";
			switch (status.getReason())
			{
			case PushStatus.REASON_NETWORK_ERROR :
				statusMessage += "Communication failed due to network error.\n";
				break;
			case PushStatus.REASON_SIM_CHANGE :
				statusMessage += "SIM card change.\n";
				break;
			case PushStatus.REASON_API_CALL :
				statusMessage += "Status change was initiated by API call.\n";
				break;
			}
			statusMessage += status.getError();
			break;
		case PushStatus.STATUS_BB_NOT_REGISTERED :
			statusMessage += "Application didn't register for push messages with BlackBerry. Unregistering with Pushwoosh...";
			break;
		case PushStatus.STATUS_BB_PENDING :
			statusMessage += "Push communications requested but is not confirmed yet.";
			break;
			// pushwoosh
		case PushStatus.STATUS_PUSHWOOSH_ACTIVE :
			statusMessage += "Application is actively listening.";
			break;
		case PushStatus.STATUS_PUSHWOOSH_NOT_REGISTERED :
			statusMessage += "Application didn't register for push messages.";
			break;
		case PushStatus.STATUS_PUSHWOOSH_FAILED :
			statusMessage += "Fail.\n";
			switch (status.getReason())
			{
			case PushStatus.REASON_NETWORK_ERROR :
				statusMessage += "Communication failed due to network error.\n";
				break;
			case PushStatus.REASON_SIM_CHANGE :
				statusMessage += "SIM card change.\n";
				break;
			case PushStatus.REASON_API_CALL :
				statusMessage += "Status change was initiated by API call.\n";
				break;
			}
			statusMessage += status.getError();
			break;
		}
		return statusMessage;
	}*/

}
