package fi.bb.checkers.ui.components;

import fi.bb.checkers.helpers.ResourceHelper;

public class Checkbox extends ImageButton
{
	private boolean checked = false;

	public Checkbox()
	{
		this(false, 0);
	}

	public Checkbox(boolean checked)
	{
		this(checked, 0);
	}

	public Checkbox(boolean checked, long style)
	{
		super("check-box_default.jpg", "check-box_hover.jpg", ResourceHelper.convert(20), ResourceHelper.convert(20), style);

		setChecked(checked);
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
		if (checked)
		{
			setImage("check-box_selected.jpg", "check-box_ticked_hover.jpg");
		}
		else
		{
			setImage("check-box_default.jpg", "check-box_hover.jpg");
		}
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void clickButton()
	{
		super.clickButton();
		setChecked(!isChecked());
	}
}
