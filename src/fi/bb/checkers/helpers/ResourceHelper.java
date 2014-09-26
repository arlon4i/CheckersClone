package fi.bb.checkers.helpers;

import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.FontFamily;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.utils.BitmapTools;

public class ResourceHelper
{
	public static final int color_background_app = 0xf6f5f5;
	public static final int color_background_inputfields = 0xffffff;
	public static final int color_shoprite_red = 0xda001a;
	public static final int color_checkers_teal = 0x00a19a;
	public static final int color_checkers_splash_teal = 0x039999;
	public static final int color_primary = color_checkers_teal;
	public static final int color_white = 0xffffff;
	public static final int color_black = 0x000000;
	public static final int color_dark_grey = 0x484d4d;
	public static final int color_grey = 0xa7a9ac;
	public static final int color_light_grey = 0xcccccc;
	public static final int color_lighter_grey = 0xd2d2d2;
	// on OS6 there seems to be color noise when resizing images, so the profile pic mask doesn't match the drawer background
	public static final int color_drawer_grey = DeviceInfo.getSoftwareVersion().startsWith("6") ? 0xf0f0e8 : 0xf6f5f5;
	// public static final int color_drawer_grey = 0xf6f5f5;

	public static final int color_primary_same_as_android = 0x00a9a1;
	
	private static FontFamily HelveticaLight;
	private static FontFamily HelveticaMed;
	private static Hashtable ResourceCache = new Hashtable();

	public static void init()
	{
		try
		{
			net.rim.device.api.ui.FontManager.getInstance().load("HelveticaNeue_Lt.ttf", "HelveticaLight", net.rim.device.api.ui.FontManager.APPLICATION_FONT);
			net.rim.device.api.ui.FontManager.getInstance().load("HelveticaNeue_Med.ttf", "HelveticaMed", net.rim.device.api.ui.FontManager.APPLICATION_FONT);
		} catch (Exception e)
		{
			RemoteLogger.log("ResourceHelper", "init: " + e.toString());
		}
	}

	public static FontFamily helveticaLight()
	{
		if (HelveticaLight == null)
		{
			try
			{
				HelveticaLight = FontFamily.forName("HelveticaLight");
			} catch (Exception e)
			{
				RemoteLogger.log("ResourceHelper", "helveticaLight: " + e.toString());
			}
		}
		return HelveticaLight;
	}

	public static FontFamily helveticaMed()
	{
		if (HelveticaMed == null)
		{
			try
			{
				HelveticaMed = FontFamily.forName("HelveticaMed");
			} catch (Exception e)
			{
				RemoteLogger.log("ResourceHelper", "helveticaMed: " + e.toString());
			}
		}
		return HelveticaMed;
	}

	private static double CONVERT_RATIO = (double) Display.getWidth() / (double) 320;
	/**
	 * returns corresponding pixel size for the current screen size, where <code>px</code> is the size for 320x240.
	 * 
	 * @param px
	 * @return
	 */
	// This means we won't need to hard code a size for every single screen size we support, and should work for sizes we don't
	public static int convert(int px)
	{
		double pixels = px * CONVERT_RATIO;

		// round to closest int
		return (int) Math.floor(pixels + 0.5);
	}

	// ------------------------- get from resources ---------------------------

	// return appropriate resolution from resources
	public static Bitmap getImage(String imagename)
	{
		// add extension if it was omitted
		if (!imagename.endsWith(".png") && !imagename.endsWith(".jpg")) imagename += ".png";

		Bitmap image = (Bitmap) ResourceCache.get(imagename);
		if (image != null) return image;

		double ratio = 1;
		String preferred_path;

		// things in the landscape/portrait folders have a '_' prepended so that we don't get flooded with FRIDG errors in the log.
		// These misses also cause a noticable slow down.
		// Using the native portrait and landscape folders won't work because then I would not know which ratio to use to resize the image.
		if (imagename.startsWith("_"))
		{
			if (Display.getWidth() < Display.getHeight())
			{
				preferred_path = "/img/360/";
				// portrait resources are 360
				ratio = (double) Display.getWidth() / (double) 360;
			}
			else
			{
				preferred_path = "/img/640/";
				// landscape resources are 640
				ratio = (double) Display.getWidth() / (double) 640;
			}

			try
			{
				InputStream is = ResourceHelper.class.getResourceAsStream(preferred_path + imagename);
				byte[] imageBytes = IOUtilities.streamToBytes(is);
				image = Bitmap.createBitmapFromBytes(imageBytes, 0, imageBytes.length, 1);
			} catch (Exception e)
			{
			}
		}

		if (image == null)
		{
			image = Bitmap.getBitmapResource(imagename);
			// all other resources are 640
			ratio = (double) Display.getWidth() / (double) 640;
		}

		// resize images to appropriate size because we cannot have that many resources
		image = BitmapTools.resizeTransparentBitmap(image, (int) (image.getWidth() * ratio), (int) (image.getHeight() * ratio), Bitmap.FILTER_BILINEAR, Bitmap.SCALE_STRETCH);
		// cache to avoid resizing the same resource constantly
		ResourceCache.put(imagename, image);

		return image;
	}

	public static Bitmap getImageFromFile(String path)
	{
		if (path == null) return null;

		Bitmap bitmap = null;
		InputStream inputStream = null;
		FileConnection fileConnection = null;

		try
		{
			fileConnection = (FileConnection) Connector.open(path);
			inputStream = fileConnection.openInputStream();
			byte[] data = new byte[(int) fileConnection.fileSize()];
			data = IOUtilities.streamToBytes(inputStream);
			bitmap = Bitmap.createBitmapFromBytes(data, 0, data.length, 1);
		} catch (Exception e)
		{
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (Exception e) {
				}
			}
			
			if (fileConnection != null)
			{
				try
				{
					fileConnection.close();
				}
				catch (Exception e) {
				}
			}
		}

		return bitmap;
	}
}
