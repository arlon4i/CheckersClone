package fi.bb.checkers.ui.fragments;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.decor.BackgroundFactory;
import fi.bb.checkers.datatypes.InboxMessage;
import fi.bb.checkers.helpers.FlurryHelper;
import fi.bb.checkers.helpers.PersistentStoreHelper;
import fi.bb.checkers.helpers.ResourceHelper;
import fi.bb.checkers.prompts.CustomDialog;
import fi.bb.checkers.ui.components.InboxField;

public class InboxFragment extends Fragment
{
	public static final int FRAGMENT_ID = getUUID();

	public InboxFragment()
	{
		super(VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);
		setBackground(BackgroundFactory.createSolidBackground(ResourceHelper.color_background_app));
	}

	protected void onVisibilityChange(boolean visible)
	{
		super.onVisibilityChange(visible);

		if (visible)
		{
			refresh();
		}
	}
	
	public void onClose() {
		FlurryHelper.endTimedEvent(FlurryHelper.EVENT_INBOX);
		super.onClose();
	}

	private void refresh()
	{
		deleteAll();

		Vector inbox = PersistentStoreHelper.getInbox();
		for (int i = 0; i < inbox.size(); i++)
		{
			InboxField field = new InboxField((InboxMessage) inbox.elementAt(i))
			{
				public void clickButton() {
					logNewMessageTapped(getMessage());
					super.clickButton();
				}
			};
			synchronized (Application.getEventLock())
			{
				add(field);
			}
		}

		// invalidate screen, not just this field, so that we update the number on the actionbar
		UiApplication.getUiApplication().getActiveScreen().invalidate();
	}

	private void logNewMessageTapped(InboxMessage message)
	{
		if (!message.read)
		{
			Hashtable eventParams = new Hashtable();

			eventParams.put(FlurryHelper.PARAM_TIMESTAMP, FlurryHelper.getFlurryFormatDate(Calendar.getInstance()));
			FlurryHelper.addProvinceParam(eventParams);

			FlurryHelper.logEvent(FlurryHelper.EVENT_READ_MESSAGE, eventParams, true);
		}
	}

	public void makeMenu(Menu menu)
	{
		super.makeMenu(menu);

		MenuItem item;

		final int focusindex = content.getFieldWithFocusIndex();

		if (focusindex != -1)
		{
			final InboxField inboxfield = (InboxField) content.getField(focusindex);

			item = new MenuItem(inboxfield.getMessage().read ? "Mark as Unread" : "Mark as Read", 0x00070000, 0)
			{
				public void run()
				{
					inboxfield.getMessage().read = !inboxfield.getMessage().read;
					PersistentStoreHelper.inboxAdd(inboxfield.getMessage());
					refresh();
				}
			};
			menu.add(item);

			item = new MenuItem("Delete", 0x00070000, 1)
			{
				public void run()
				{
					int choice = CustomDialog.doModal("Are you sure you want to remove this message from your inbox?", new String[]{"Cancel", "Okay"}, new int[]{Dialog.CANCEL, Dialog.YES});
					if (choice == Dialog.YES)
					{

						PersistentStoreHelper.inboxDelete(inboxfield.getMessage());
						refresh();
					}
				}
			};
			menu.add(item);
		}

		int total_messages = PersistentStoreHelper.getInbox().size();
		if (total_messages > 1)
		{
			int unread = PersistentStoreHelper.inboxUnread();
			if (unread > 0)
			{
				item = new MenuItem("Mark All as Read", 0x00080000, 0)
				{
					public void run()
					{
						PersistentStoreHelper.inboxMarkAll(true);
						refresh();
					}
				};
				menu.add(item);
			}

			if (total_messages > unread)
			{
				item = new MenuItem("Mark All as Unread", 0x00080000, 1)
				{
					public void run()
					{
						PersistentStoreHelper.inboxMarkAll(false);
						refresh();
					}
				};
				menu.add(item);
			}

			item = new MenuItem("Delete All", 0x00080000, 2)
			{
				public void run()
				{
					PersistentStoreHelper.inboxDeleteAll();
					refresh();
				}
			};
			menu.add(item);
		}
	}
}
