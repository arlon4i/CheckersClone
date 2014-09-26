package fi.bb.checkers.ui.components;

import fi.bb.checkers.MainApplication;
import fi.bb.checkers.helpers.ResourceHelper;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.ScrollChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class HScrollableTextInputField extends HorizontalFieldManager
{
	private String placeHolder;
	private boolean isPassword;
	boolean focusable;
	private int defaultColor = Color.BLACK;
	private boolean showBoarder = true;
	private EditField editField;

	public HScrollableTextInputField(String _placeHolder, boolean _isPassword)
	{
		super(NO_HORIZONTAL_SCROLL);
		this.placeHolder = _placeHolder;
		this.isPassword = _isPassword;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;
		editField =  new EditField()
		{
			public int getPreferredWidth()
			{
				return Display.getWidth();//TODO this is wrong... should be whatever space is left, and this should be transferred to the main manager
			}

			public int getPreferredHeight()
			{
				return getFont().getHeight();
			}

			protected void layout(int width, int height)
			{
				super.layout(getPreferredWidth(), getPreferredHeight());

				setExtent(getPreferredWidth(), getPreferredHeight());
			}

			public void paint(Graphics g)
			{
				g.setColor(Color.LIGHTGRAY);

				if (showBoarder)
				{
					g.drawRect(0, 0, getWidth(), getPreferredHeight());
				}
				String fieldText = "";

				// Populate text
				if (getTextLength() < 1)
				{
					fieldText = placeHolder;
					if (!showBoarder)
					{
						g.setColor(ResourceHelper.color_grey);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					g.drawText(fieldText, 0, 0);
				}
				else
				{
					if (isEditable() && defaultColor == Color.BLACK)
					{
						g.setColor(Color.BLACK);
					}
					else if (isEditable() && defaultColor != Color.BLACK)
					{
						g.setColor(defaultColor);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					if (isPassword)
					{
						for (int i = 0; i < getTextLength() - 1; i++)
						{
							fieldText += "*";
						}
						fieldText += getText().substring(getTextLength() - 1);
					}
					else
					{
						fieldText = getText();
					}

					g.drawText(fieldText, 0, 0);

				}
			}

			protected void onUnfocus()
			{
				((MainApplication) UiApplication.getUiApplication()).hideKeyboard();
				super.onUnfocus();
			}
		};
		editField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

		HorizontalFieldManager manager = new HorizontalFieldManager(HORIZONTAL_SCROLL);
		manager.add(editField);
		setHorizontalScrollListener(manager);
		add(manager);
	}

	public HScrollableTextInputField(String placeHolder, boolean isPassword, boolean showboarder)
	{
		this(placeHolder, isPassword, showboarder, Color.BLACK, 0);
	}

	public HScrollableTextInputField(String _placeHolder, long style, boolean _isPassword)
	{
		super(NO_HORIZONTAL_SCROLL);
		this.placeHolder = _placeHolder;
		this.isPassword = _isPassword;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;
		editField =  new EditField(style)
		{
			public int getPreferredWidth()
			{
				return Display.getWidth();
			}

			public int getPreferredHeight()
			{
				return getFont().getHeight();
			}

			protected void layout(int width, int height)
			{
				super.layout(getPreferredWidth(), getPreferredHeight());

				setExtent(getPreferredWidth(), getPreferredHeight());
			}

			public void paint(Graphics g)
			{
				g.setColor(Color.LIGHTGRAY);

				if (showBoarder)
				{
					g.drawRect(0, 0, getWidth(), getPreferredHeight());
				}
				String fieldText = "";

				// Populate text
				if (getTextLength() < 1)
				{
					fieldText = placeHolder;
					if (!showBoarder)
					{
						g.setColor(ResourceHelper.color_grey);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					g.drawText(fieldText, 0, 0);
				}
				else
				{
					if (isEditable() && defaultColor == Color.BLACK)
					{
						g.setColor(Color.BLACK);
					}
					else if (isEditable() && defaultColor != Color.BLACK)
					{
						g.setColor(defaultColor);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					if (isPassword)
					{
						for (int i = 0; i < getTextLength() - 1; i++)
						{
							fieldText += "*";
						}
						fieldText += getText().substring(getTextLength() - 1);
					}
					else
					{
						fieldText = getText();
					}

					g.drawText(fieldText, 0, 0);

				}
			}

			protected void onUnfocus()
			{
				((MainApplication) UiApplication.getUiApplication()).hideKeyboard();
				super.onUnfocus();
			}
		};
		editField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

		HorizontalFieldManager manager = new HorizontalFieldManager(HORIZONTAL_SCROLL);
		manager.add(editField);
		setHorizontalScrollListener(manager);
		add(manager);
	}

	public HScrollableTextInputField(String _placeHolder, boolean _isPassword, int _defaultColor)
	{
		super(NO_HORIZONTAL_SCROLL);
		this.placeHolder = _placeHolder;
		this.isPassword = _isPassword;
		this.defaultColor = _defaultColor;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;
		editField =  new EditField()
		{
			public int getPreferredWidth()
			{
				return Display.getWidth();
			}

			public int getPreferredHeight()
			{
				return getFont().getHeight();
			}

			protected void layout(int width, int height)
			{
				super.layout(getPreferredWidth(), getPreferredHeight());

				setExtent(getPreferredWidth(), getPreferredHeight());
			}

			public void paint(Graphics g)
			{
				g.setColor(Color.LIGHTGRAY);

				if (showBoarder)
				{
					g.drawRect(0, 0, getWidth(), getPreferredHeight());
				}
				String fieldText = "";

				// Populate text
				if (getTextLength() < 1)
				{
					fieldText = placeHolder;
					if (!showBoarder)
					{
						g.setColor(ResourceHelper.color_grey);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					g.drawText(fieldText, 0, 0);
				}
				else
				{
					if (isEditable() && defaultColor == Color.BLACK)
					{
						g.setColor(Color.BLACK);
					}
					else if (isEditable() && defaultColor != Color.BLACK)
					{
						g.setColor(defaultColor);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					if (isPassword)
					{
						for (int i = 0; i < getTextLength() - 1; i++)
						{
							fieldText += "*";
						}
						fieldText += getText().substring(getTextLength() - 1);
					}
					else
					{
						fieldText = getText();
					}

					g.drawText(fieldText, 0, 0);

				}
			}

			protected void onUnfocus()
			{
				((MainApplication) UiApplication.getUiApplication()).hideKeyboard();
				super.onUnfocus();
			}
		};
		editField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

		HorizontalFieldManager manager = new HorizontalFieldManager(HORIZONTAL_SCROLL);
		manager.add(editField);
		setHorizontalScrollListener(manager);
		add(manager);
	}

	public HScrollableTextInputField(String _placeHolder, boolean _isPassword, boolean showboarder, int color, long style)
	{
		super(NO_HORIZONTAL_SCROLL);
		this.placeHolder = _placeHolder;
		this.isPassword = _isPassword;
		this.showBoarder = showboarder;
		this.defaultColor = color;
		this.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));

		focusable = true;

		editField =  new EditField(style)
		{
			public int getPreferredWidth()
			{
				return Display.getWidth();
			}

			public int getPreferredHeight()
			{
				return getFont().getHeight();
			}

			protected void layout(int width, int height)
			{
				super.layout(getPreferredWidth(), getPreferredHeight());

				setExtent(getPreferredWidth(), getPreferredHeight());
			}

			public void paint(Graphics g)
			{
				g.setColor(Color.LIGHTGRAY);

				if (showBoarder)
				{
					g.drawRect(0, 0, getWidth(), getPreferredHeight());
				}
				String fieldText = "";

				// Populate text
				if (getTextLength() < 1)
				{
					fieldText = placeHolder;
					if (!showBoarder)
					{
						g.setColor(ResourceHelper.color_grey);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					g.drawText(fieldText, 0, 0);
				}
				else
				{
					if (isEditable() && defaultColor == Color.BLACK)
					{
						g.setColor(Color.BLACK);
					}
					else if (isEditable() && defaultColor != Color.BLACK)
					{
						g.setColor(defaultColor);
					}
					else
					{
						g.setColor(Color.GRAY);
					}

					if (isPassword)
					{
						for (int i = 0; i < getTextLength() - 1; i++)
						{
							fieldText += "*";
						}
						fieldText += getText().substring(getTextLength() - 1);
					}
					else
					{
						fieldText = getText();
					}

					g.drawText(fieldText, 0, 0);

				}
			}

			protected void onUnfocus()
			{
				((MainApplication) UiApplication.getUiApplication()).hideKeyboard();
				super.onUnfocus();
			}
		};

		editField.setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));
		setFont(ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(17), Ui.UNITS_px));

		HorizontalFieldManager manager = new HorizontalFieldManager(HORIZONTAL_SCROLL);
		manager.add(editField);
		setHorizontalScrollListener(manager);
		add(manager);
	}

	private void setHorizontalScrollListener(final HorizontalFieldManager fieldManager)
	{
		fieldManager.setScrollListener(new ScrollChangeListener() {

			public void scrollChanged(Manager manager, int newHorizontalScroll,
					int newVerticalScroll) {
				if (editField.getFont().getAdvance(editField.getText()) < editField.getPreferredWidth())
				{
					fieldManager.setHorizontalScroll(0);
				}
			}
		});
	}

	public int getPreferredWidth()
	{
		return Display.getWidth();
	}
	
	public void setFocusable(boolean focusableFlag)
	{
		focusable = focusableFlag;
	}

	public boolean isFocusable()
	{
		return focusable;
	}

	protected void displayFieldFullMessage()
	{

	}

	public String getText() {
		return editField.getText();
	}
}
