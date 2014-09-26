package fi.bb.checkers.datatypes;

import net.rim.device.api.util.Persistable;

public class InboxMessage implements Persistable
{
	public int message_id;
	public boolean read = false;
	public String thumbnail_url;
	public String title;
	public String description;
	public long date_recieved;

	public boolean equals(Object obj)
	{
		if (obj instanceof InboxMessage)
		{
			return message_id == ((InboxMessage) obj).message_id;
		}
		return false;
	}
}
