package fi.bb.checkers.datatypes;

import java.util.Vector;

import net.rim.device.api.util.Persistable;

public class PersistentUserData implements Persistable
{
	public String profile_picture_url;
	public Vector mylist = new Vector();
}
