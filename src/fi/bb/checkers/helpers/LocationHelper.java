package fi.bb.checkers.helpers;

import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.gps.BlackBerryLocation;
import net.rim.device.api.ui.component.Dialog;
import rimx.location.simplelocation.SimpleLocationProvider;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.CustomDialog;

public class LocationHelper
{
	public static QualifiedCoordinates coords;
	public static LocationData location;
	private static boolean show_prompt = true;
	private static LocationThread thread;

	public static boolean isGPSEnabled()
	{
		try
		{
			LocationProvider lp = LocationProvider.getInstance(null);
			if (lp != null && lp.getState() == LocationProvider.AVAILABLE)
			{
				return true;
			}
		} catch (Exception e)
		{
			RemoteLogger.log("LocationHelper", "isGPSEnabled: " + e.toString());
		}

		return false;
	}

	public static void determineLocation()
	{
		try
		{
			if (location != null || (thread != null && thread.isAlive())) return;

			if (PersistentStoreHelper.showGPS())
			{
				int choice = CustomDialog.doModal(
						"Thank you for using the Checkers Mobile App.\n\nWe would like to use your current location. This will allow us to provide you with the latest Specials and EeziCoupons so you can start saving.",
						new String[]{"Skip", "Okay"}, new int[]{Dialog.CANCEL, Dialog.YES}, 1);

				PersistentStoreHelper.setShowGPS(false);
				PersistentStoreHelper.setUseGPS(choice == Dialog.YES);
				// Would be logical to remove/deny permission on cancel since a user can now say cancel and still use gps. BB doesn't appear to provide that functionality though.
			}

			ApplicationPermissionsManager permission_manager = ApplicationPermissionsManager.getInstance();
			int permssion = permission_manager.getPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA);

			// only prompt once per run, if the user as indicated they want to use gps
			if (PersistentStoreHelper.useGPS() && show_prompt && permssion != ApplicationPermissions.VALUE_ALLOW)
			{
				ApplicationPermissions application_permission = new ApplicationPermissions();
				application_permission.addPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA);
				permission_manager.invokePermissionsRequest(application_permission);
			}
			show_prompt = false;

			// even if the user said not to use gps, use it if they set the permission in the settings (as per the help screen instruction for enabling gps)
			permssion = permission_manager.getPermission(ApplicationPermissions.PERMISSION_LOCATION_DATA);
			if (permssion == ApplicationPermissions.VALUE_ALLOW)
			{
				thread = new LocationThread();
				thread.start();
			}
		} catch (Exception e)
		{
			// Don't want this to break the app
			RemoteLogger.log("LocationHelper", "determineLocation: " + e.toString());
		}
	}

	private static class LocationThread extends Thread
	{
		public void run()
		{
			try
			{
				SimpleLocationProvider provider = new SimpleLocationProvider(SimpleLocationProvider.MODE_OPTIMAL);
				provider.setRetryFactor(5);
				provider.setGPSTimeout(60);
				provider.setGeolocationTimeout(60);

				BlackBerryLocation location = provider.getLocation(120);
				coords = location.getQualifiedCoordinates();
				RemoteLogger.log("LocationHelper", "Coordindates Received: " + coords.getLatitude() + "," + coords.getLongitude());
				LocationHelper.location = ServerHelper.getCurrentProvince(coords.getLatitude(), coords.getLongitude());

				RemoteLogger.log("LocationHelper", "Location Selected: " + LocationHelper.location.getDesc());
			} catch (Exception e)
			{
				RemoteLogger.log("LocationHelper", "LocationThread: " + e.toString());
			}
		}
	};
}