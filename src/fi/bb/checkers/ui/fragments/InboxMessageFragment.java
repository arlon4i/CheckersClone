package fi.bb.checkers.ui.fragments;

import java.util.Calendar;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.InboxMessage;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.ui.components.LabelField;

public class InboxMessageFragment extends Fragment
{
	public static final int FRAGMENT_ID = getUUID();
	private InboxMessage inbox_message;

	private Calendar startView;
	private boolean newMessage;
	
	public InboxMessageFragment(InboxMessage inbox_message)
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		
		startView = Calendar.getInstance();
		newMessage = inbox_message.read;
		
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));
		int padding = ResourceHelper.convert(10);
		setPadding(padding, padding, padding, padding);

		this.inbox_message = inbox_message;
		Font font = ResourceHelper.helveticaLight().getFont(Font.PLAIN, ResourceHelper.convert(15), Ui.UNITS_px);
		LabelField label_title = new LabelField(inbox_message.title, ResourceHelper.color_black, 0);
		LabelField label_content = new LabelField(inbox_message.description, ResourceHelper.color_grey, 0);

		label_title.setFont(font);
		label_content.setFont(font);

		label_content.setMargin(ResourceHelper.convert(15), 0, 0, 0);

		add(label_title);
		add(label_content);
	}

	protected void onVisibilityChange(boolean visible)
	{
		super.onVisibilityChange(visible);

		if (visible && !inbox_message.read)
		{			
			inbox_message.read = true;
			PersistentStoreHelper.inboxAdd(inbox_message);

			// invalidate screen, not just this field, so that we update the number on the actionbar
			UiApplication.getUiApplication().getActiveScreen().invalidate();
		}
	}
	
	
	
	protected boolean keyChar(char ch, int status, int time)
	{
		if (ch == Characters.ESCAPE)
		{
			FlurryHelper.endTimedEvent(FlurryHelper.EVENT_READ_MESSAGE);
		}

		return super.keyChar(ch, status, time);
	}
	
	public void onClose() {
	
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_READ_MESSAGE);
		super.onClose();
	}
}
