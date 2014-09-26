package fi.bb.checkers.ui.screens;

import java.io.IOException;
import java.util.Vector;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.checkers.datatypes.LocationData;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.helpers.ServerHelper;
import fi.bb.checkers.prompts.InfoDialog;
import fi.bb.checkers.prompts.LoadingDialog;
import fi.bb.checkers.ui.components.LabelField;
import fi.bb.checkers.ui.components.HyperlinkButton;
import fi.bb.checkers.ui.components.TextImageButton;
import fi.bb.checkers.ui.components.ToggleSelectionField;
import fi.bb.checkers.utils.AsyncTask;
import fi.bb.checkers.utils.VectorUtil;

public class SelectionScreen extends MainScreen implements FieldChangeListener
{
	boolean multiple_selection;
	Vector dataset;
	Vector defaultstate;
	Vector selection;
	TextImageButton button_done;
	TextImageButton button_reset;
	ToggleSelectionField select_all;
	HorizontalFieldManager button_manager;

	private SelectionScreen(String title, Vector selection, Vector defaultstate, Vector dataset, boolean multiple_selection, boolean show_disclaimer)
	{
		super(Manager.VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBanner(new BannerField(title));

		this.multiple_selection = multiple_selection;
		this.dataset = dataset;
		this.defaultstate = defaultstate;
		this.selection = new Vector();
		VectorUtil.copy(selection, this.selection);

		if (show_disclaimer)
		{
			if (selection.size() > 0)
			{
				add(new DisclaimerField((LocationData) selection.elementAt(0)));
			}
		}

		CouponCategory category_selectall = new CouponCategory();
		category_selectall.setId("0");
		category_selectall.setName("Select All");
		select_all = new ToggleSelectionField(category_selectall);
		select_all.setChangeListener(this);
		if (multiple_selection) add(select_all);

		for (int i = 0; i < dataset.size(); i++)
		{
			Object obj = dataset.elementAt(i);

			ToggleSelectionField item = new ToggleSelectionField(obj);
			item.setChecked(this.selection.contains(obj));
			item.setChangeListener(this);
			add(item);
		}

		button_done = new TextImageButton("Done", "btn_sml_default", "btn_sml_hover");
		button_done.setChangeListener(this);
		button_done.setMargin(ResourceHelper.convert(8), 0, ResourceHelper.convert(10), ResourceHelper.convert(8));
		button_done.setTextColor(ResourceHelper.color_white);
		button_done.setTextColorHover(ResourceHelper.color_primary);
		button_done.setTextColorPressed(ResourceHelper.color_primary);

		button_reset = new TextImageButton("Reset", "btn_sml_grey_default", "btn_sml_hover");
		button_reset.setChangeListener(this);
		button_reset.setMargin(ResourceHelper.convert(8), 0, ResourceHelper.convert(10), ResourceHelper.convert(8));
		button_reset.setTextColor(ResourceHelper.color_primary);
		button_reset.setTextColorHover(ResourceHelper.color_primary);
		button_reset.setTextColorPressed(ResourceHelper.color_primary);

		button_manager = new HorizontalFieldManager();
		button_manager.add(button_done);
		add(button_manager);

		if (selection.size() == dataset.size())
		{
			select_all.setChecked(true);
		}

		if (isChanged())
		{
			button_done.setText("Apply");
			if (button_reset.getManager() == null) button_manager.add(button_reset);
		}
		else
		{
			button_done.setText("Done");
			if (button_reset.getManager() != null) button_manager.delete(button_reset);
		}
	}

	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);

		MenuItem item;

		if (button_done != null)
		{
			if (buttonManagerContainsField(button_done) == true)
			{
				item = new MenuItem(button_done.getText(), 0x00070000, 0)//gettext since it may be apply or done
				{
					public void run()
					{
						done();
					}
				};
				menu.add(item);
			}
		}
	}

	private boolean buttonManagerContainsField(Field field)
	{
		for (int i=0; i < button_manager.getFieldCount(); i++)
		{
			if ((button_manager.getField(i) instanceof TextImageButton) && ((TextImageButton)button_manager.getField(i) == button_done))
			{
				return true;
			}
		}
		return false;
	}

	private void done() {
		if (defaultstate == null || isChanged())
		{
			close();
		}
		else
		{
			selection = null;
			close();
		}
	}

	public void fieldChanged(Field item, int context)
	{
		if (item == button_done)
		{
			done();
		}
		else if (item == button_reset)
		{
			button_done.setText("Done");
			if (button_reset.getManager() != null) button_manager.delete(button_reset);
			VectorUtil.copy(defaultstate, selection);

			for (int i = 0; i < getFieldCount(); i++)
			{
				Field field = getField(i);
				if (field instanceof ToggleSelectionField && field != select_all)
				{
					((ToggleSelectionField) field).setChecked(selection.contains(((ToggleSelectionField) field).getObject()));
				}
			}

			if (selection.size() == dataset.size())
			{
				select_all.setChecked(true);
			}
			else
			{
				select_all.setChecked(false);
			}
		}
		else if (item == select_all)
		{
			select_all.setChecked(!select_all.isChecked());

			for (int i = 0; i < getFieldCount(); i++)
			{
				Field field = getField(i);
				if (field instanceof ToggleSelectionField && field != select_all)
				{
					((ToggleSelectionField) field).setChecked(select_all.isChecked());
					selection.removeElement(((ToggleSelectionField) field).getObject());
					if (select_all.isChecked())
					{
						selection.addElement(((ToggleSelectionField) field).getObject());
					}
				}
			}

			if (isChanged())
			{
				button_done.setText("Apply");
				if (button_reset.getManager() == null) button_manager.add(button_reset);
			}
			else
			{
				button_done.setText("Done");
				if (button_reset.getManager() != null) button_manager.delete(button_reset);
			}
		}
		else
		{
			if (!multiple_selection)
			{
				selection.removeAllElements();
				for (int i = 0; i < getFieldCount(); i++)
				{
					Field field = getField(i);
					if (field instanceof ToggleSelectionField)
					{
						((ToggleSelectionField) field).setChecked(field == item);
						if (field == item)
						{
							selection.addElement(((ToggleSelectionField) field).getObject());
						}
					}
				}
			}
			else
			{
				ToggleSelectionField list_item = (ToggleSelectionField) item;
				if (list_item.isChecked())
				{
					list_item.setChecked(false);
					selection.removeElement(list_item.getObject());
				}
				else
				{
					list_item.setChecked(true);
					selection.addElement(list_item.getObject());
				}

				if (selection.size() == dataset.size())
				{
					select_all.setChecked(true);
				}
				else
				{
					select_all.setChecked(false);
				}
			}

			if (isChanged())
			{
				button_done.setText("Apply");
				if (button_reset.getManager() == null) button_manager.add(button_reset);
			}
			else
			{
				button_done.setText("Done");
				if (button_reset.getManager() != null) button_manager.delete(button_reset);
			}
		}
	}

	protected boolean keyChar(char c, int status, int time)
	{
		if (c == Characters.ESCAPE)
		{
			selection = null;
			close();
			return true;
		}
		return super.keyChar(c, status, time);
	}

	private boolean isChanged()
	{
		if (defaultstate == null) return false; // disabled reset

		if (selection.size() != defaultstate.size()) return true;

		for (int i = 0; i < selection.size(); i++)
		{
			if (!defaultstate.contains(selection.elementAt(i))) return true;
		}

		return false;
	}

	private class DisclaimerField extends HorizontalFieldManager
	{
		public DisclaimerField(final LocationData location)
		{
			LabelField label = new LabelField(location.getDesc(), ResourceHelper.color_grey, 0);
			label.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px));
			label.setMargin((getPreferredHeight() - label.getPreferredHeight()) / 2, 0, 0, ResourceHelper.convert(10));

			HyperlinkButton button = new HyperlinkButton("Disclaimer", ResourceHelper.convert(15))
			{
				public void clickButton()
				{
					new LoadDisclaimerTask().execute(new Object[]{location});
				}
			};

			button.setMargin((getPreferredHeight() - button.getPreferredHeight()) / 2, 0, 0, getPreferredWidth() - label.getPreferredWidth() - button.getPreferredWidth() - ResourceHelper.convert(20));

			add(label);
			add(button);
		}

		public int getPreferredHeight()
		{
			return ResourceHelper.convert(32);
		}

		public int getPreferredWidth()
		{
			return Display.getWidth();
		}

		protected void sublayout(int width, int height)
		{
			super.sublayout(getPreferredWidth(), getPreferredHeight());
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		protected void paint(Graphics graphics)
		{
			graphics.setColor(ResourceHelper.color_lighter_grey);
			graphics.fillRect(0, 0, getWidth(), getHeight());

			super.paint(graphics);
		}
	}

	private class BannerField extends Field
	{
		private final String text;
		public BannerField(String title)
		{
			text = title;
			setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(20), Ui.UNITS_px));
		}
		public int getPreferredHeight()
		{
			return ResourceHelper.convert(30);
		}

		public int getPreferredWidth()
		{
			return Display.getWidth();
		}

		protected void layout(int width, int height)
		{
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		protected void paint(Graphics graphics)
		{
			int x = (getWidth() - getFont().getAdvance(text)) / 2;
			int y = (getHeight() - getFont().getHeight()) / 2;

			graphics.setColor(ResourceHelper.color_black);
			graphics.drawText(text, x, y);

			graphics.setColor(ResourceHelper.color_light_grey);
			graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
		}
	}

	private class LoadDisclaimerTask extends AsyncTask
	{
		LoadingDialog dialog;
		protected void onPreExecute()
		{
			super.onPreExecute();
			dialog = LoadingDialog.push("Loading");
		}

		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			dialog.close();

			if (result instanceof IOException)
			{
				InfoDialog.doModal("Error", ((IOException) result).getMessage(), "Okay");
			}
			else if (result instanceof String)
			{
				InfoDialog.doModal("", (String) result, "Okay");
			}
		}

		public Object doInBackground(Object[] params)
		{
			try
			{
				return ServerHelper.getDisclaimer((LocationData) params[0]);
			} catch (IOException e)
			{
				return e;
			}
		}
	}

	public static Vector doModal(String title, Vector selection, Vector defaultstate, Vector dataset, boolean multiple_selection, boolean show_disclaimer)
	{
		SelectionScreen screen = new SelectionScreen(title, selection, defaultstate, dataset, multiple_selection, show_disclaimer);
		UiApplication.getUiApplication().pushModalScreen(screen);

		return screen.selection;
	}
}
