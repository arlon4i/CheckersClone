package fi.bb.checkers.ui.fragments;

import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.TextInputField;
import fi.bb.checkers.ui.components.ToggleSelectionField;
import fi.bb.checkers.ui.screens.TutorialScreen;
import fi.bb.checkers.ui.screens.ViewPagerScreen;

public class SelectProvinceFragment extends Fragment implements FieldChangeListener
{
	public static final int FRAGMENT_ID = getUUID();
	private TextInputField search_field;
	private VerticalFieldManager content;
	private LoadThread load_provinces;

	public SelectProvinceFragment()
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));

		logFindAStoreEvent();

		search_field = new TextInputField("Search", false, false, ResourceHelper.color_grey, 0);
		int padding_vertical = (ResourceHelper.convert(30) - search_field.getPreferredHeight()) / 2;
		int padding_horizontal = ResourceHelper.convert(4);
		search_field.setPadding(padding_vertical, padding_horizontal, padding_vertical, padding_horizontal);

		search_field.setChangeListener(this);
		add(search_field);

		content = new VerticalFieldManager();
		add(content);

		refresh();
	}

	private void logFindAStoreEvent()
	{
		Hashtable eventParams = new Hashtable();

		FlurryHelper.addLoginParams(eventParams);
		FlurryHelper.addProvinceParam(eventParams);

		FlurryHelper.logEvent(FlurryHelper.EVENT_FIND_A_STORE, eventParams, true);
	}

	public void refresh()
	{
		// MUST KILL PREVIOUS THREAD. Otherwise it's possible to get 1 or 2 items from the previous thread displayed in the "filtered" results
		if (load_provinces != null && load_provinces.isAlive()) load_provinces.stop();
		content.deleteAll();

		load_provinces = new LoadThread();
		load_provinces.start();
	}

	public void onClose()
	{
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_CONTENT_BLOCK_VIEW);
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_FIND_A_STORE_BLOCK);
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_FIND_A_STORE);
		if (load_provinces != null && load_provinces.isAlive()) load_provinces.stop();
	}

	public void fieldChanged(Field field, int context)
	{
		if (field == search_field)
		{
			if (!search_field.getText().equals(""))
			{
				((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SelectStoreFragment.FRAGMENT_ID, new Object[]{search_field.getText()});
				search_field.setText("");
			}
			//refresh();
		}
		else
		{
			((ViewPagerScreen) UiApplication.getUiApplication().getActiveScreen()).transition(SelectStoreFragment.FRAGMENT_ID, new Object[]{((ToggleSelectionField) field).getObject()});
		}
	}

	protected void onVisibilityChange(boolean visible)
	{
		if (visible)
		{
			if (PersistentStoreHelper.isShowTutorial6()==true)
			{
				TutorialScreen.push(TutorialScreen.FINDSTORE);
				PersistentStoreHelper.setShowTutorial6(false);
			}
		}
		super.onVisibilityChange(visible);
	}

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
			Vector provinces = PersistentStoreHelper.getProvinces();//RuntimeStoreHelper.getProvinces();
			String search_term = search_field.getText().toLowerCase();
			for (int i = 0; i < provinces.size(); i++)
			{
				LocationData province = (LocationData) provinces.elementAt(i);
				if (search_term == null || province.getDesc().toLowerCase().indexOf(search_term) != -1)
				{
					ToggleSelectionField field = new ToggleSelectionField(province);
					field.setChangeListener(SelectProvinceFragment.this);
					synchronized (Application.getEventLock())
					{
						if (!run) return;
						content.add(field);
					}
				}
				if (!run) return;
			}
		};
	}
}
