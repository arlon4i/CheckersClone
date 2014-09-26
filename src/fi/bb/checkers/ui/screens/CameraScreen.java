package fi.bb.checkers.ui.screens;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.MetaDataControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.ui.components.NegativeMarginVerticalFieldManager;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.utils.BitmapTools;
import fi.bb.checkers.utils.ImageManipulator;

public class CameraScreen extends FullScreen
{
	NegativeMarginVerticalFieldManager manager;
	HorizontalFieldManager button_manager;
	Player player;
	VideoControl video_control;
	Field field_camera;
	BitmapField field_preview;
	Bitmap response = null;
	TextImageButton button_confirm;
	TextImageButton button_discard;

	int deviceOrientation;
	
	private CameraScreen()
	{
		manager = new NegativeMarginVerticalFieldManager(USE_ALL_HEIGHT | USE_ALL_WIDTH);

		try
		{
			player = Manager.createPlayer("capture://video");
			player.realize();
			player.prefetch();
			video_control = (VideoControl) player.getControl("VideoControl");
			field_camera = (Field) video_control.initDisplayMode(GUIControl.USE_GUI_PRIMITIVE, "net.rim.device.api.ui.Field");
			video_control.setDisplayFullScreen(true);
			player.start();

			button_manager = new HorizontalFieldManager();
			button_confirm = new TextImageButton("Confirm", "btn_sml_default", "btn_sml_hover")
			{
				public void clickButton() {
					super.clickButton();
					close();
				}
				
				protected boolean navigationClick(int status, int time)
				{
					close();
					return true;
				}
			};
			button_confirm.setTextColor(ResourceHelper.color_white);
			button_confirm.setTextColorHover(ResourceHelper.color_primary);
			button_confirm.setTextColorPressed(ResourceHelper.color_primary);

			button_discard = new TextImageButton("Retake", "btn_sml_grey_default", "btn_sml_hover")
			{
				public void clickButton() {
					super.clickButton();
					manager.replace(field_preview, field_camera);
					manager.delete(button_manager);
				}
				
				protected boolean navigationClick(int status, int time)
				{
					manager.replace(field_preview, field_camera);
					manager.delete(button_manager);
					return true;
				}
			};
			button_discard.setTextColor(ResourceHelper.color_primary);
			button_discard.setTextColorHover(ResourceHelper.color_primary);
			button_discard.setTextColorPressed(ResourceHelper.color_primary);

			button_manager.add(button_confirm);
			button_manager.add(button_discard);

			int margin_top = -button_manager.getPreferredHeight() * 2;
			int margin_left = (Display.getWidth() - button_manager.getPreferredWidth()) / 2;
			button_manager.setMargin(margin_top, 0, 0, margin_left);
			manager.add(field_camera);
			// manager.add(button_manager);
		} catch (Exception e)
		{
			RemoteLogger.log("CameraScreen", e.toString());
			Dialog.alert(e.toString());
		}

		add(manager);
	}

	private void takeScreenshot()
	{
		try
		{
			//TODO also play with this
			/*
			 * int direction = Display.DIRECTION_NORTH;
			Ui.getUiEngineInstance().setAcceptableDirections(direction);
			
			so on entering this screen change to north and west or whater landscape is...
			and when leave screen undo....
			 */
			deviceOrientation = Display.getOrientation();
			
			String imageType = "encoding=jpeg&width=640&height=480&quality=normal";
			byte[] imageBytes = video_control.getSnapshot(imageType);
			Bitmap image = Bitmap.createBitmapFromBytes(imageBytes, 0, imageBytes.length, 2);
			//Bitmap image = getImageWithOrientation(imageBytes);		
			response = image;
			Bitmap preview_image = BitmapTools.resizeTransparentBitmap(image, Display.getWidth(), Display.getHeight(), Bitmap.FILTER_LANCZOS, Bitmap.SCALE_TO_FIT);
			field_preview = new BitmapField(preview_image);

			manager.replace(field_camera, field_preview);
			manager.add(button_manager);
		} catch (MediaException e)
		{
			InfoDialog.doModal("Error", "Device does not support in-app snapshots.", "Okay");
		} catch (SecurityException e)
		{
			InfoDialog.doModal("Error", "Application has not been granted permission for this.", "Okay");
		} catch (Throwable e)
		{
			RemoteLogger.log("CameraScreen", "takeScreenshot: " + e.toString());
			// Give the message in the Event log
		}
	}

	private Bitmap getImageWithOrientation(byte[] imageBytes)
	{
		EncodedImage fullImage = EncodedImage.createEncodedImage(imageBytes, 0, imageBytes.length);
		MetaDataControl metaDataControl = fullImage.getMetaData();
		String orientation = "";
		
		try
		{
			orientation = metaDataControl.getKeyValue("orientation");//this is a device specific orientation applied to capture photo of video... 
			//so nothing to do  with actual orientation of screen.
			//orientation == 6 && device is portrait do math
			//orientation == 6 && device is landscape ignore
			//orientation == 3 && device is portrait ignore
			//orientation == 3 && device is landscape do math
		}
		catch (Exception e)
		{}
		
		RemoteLogger.log("CAMERA_DEBUG", "image_orientation: " + orientation);
		
		if (deviceOrientation==Display.ORIENTATION_PORTRAIT)
		{
			RemoteLogger.log("CAMERA_DEBUG", "device orientation portrait");	
		}
		else if (deviceOrientation==Display.ORIENTATION_LANDSCAPE)
		{
			RemoteLogger.log("CAMERA_DEBUG", "device orientation landscape");
		}
		else
		{
			RemoteLogger.log("CAMERA_DEBUG", "device orientation square");
		}
		
		Bitmap bitmap1 = Bitmap.createBitmapFromBytes(imageBytes, 0, -1, 2);
		
		Bitmap bitmap2 = bitmap1;
		
		if (orientation != null)
		{
			if (orientation.equals("6"))
			{
				RemoteLogger.log("CAMERA_DEBUG","orientation.equals(\"6\") -90");
				bitmap2 = ImageManipulator.rotate(bitmap1, -90);
			}
			else if (orientation.equals("3"))
			{
				RemoteLogger.log("CAMERA_DEBUG","orientation.equals(\"3\") -180");
				bitmap2 = ImageManipulator.rotate(bitmap1, -180);
			}
		}
		
		return bitmap2;
	}
	
	protected boolean navigationClick(int status, int time)
	{
		if (field_camera.getManager() != null)
		{
			takeScreenshot();
			return true;
		}
		return super.navigationClick(status, time);
	}

	protected boolean keyChar(char c, int status, int time)
	{
		if (c == Characters.ESCAPE)
		{
			if (field_camera.getManager() != null)
			{
				close();
			}
			else
			{
				manager.replace(field_preview, field_camera);
				manager.delete(button_manager);
			}
			response = null;
			return true;
		}
		return super.keyChar(c, status, time);
	}

	public void close()
	{
		player.deallocate();
		player.close();
		super.close();
	}

	/**
	 * Launches a screen to take a photo via the devices camera, creating confirm/discard buttons from the argument bitmaps. If confirm or confirm_focus is null, the confirm button will be a native button. If discard or discard_focus is null, the discard button will be a native button.
	 * 
	 * <p>Returns the confirmed image, or null if screen is closed</p>
	 * 
	 * @return
	 */
	public static Bitmap doModal()
	{
		CameraScreen screen = new CameraScreen();
		UiApplication.getUiApplication().pushModalScreen(screen);
		return screen.response;
	}

}