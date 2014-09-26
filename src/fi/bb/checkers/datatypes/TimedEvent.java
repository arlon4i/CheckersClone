package fi.bb.checkers.datatypes;

import java.util.Calendar;
import java.util.Hashtable;

public class TimedEvent {

	//TODO might need an unique id... that is saved in the class that wants a timedEvent
	private String eventName;
	private Hashtable eventParams;
	private Calendar startTime;

	public TimedEvent(String eventName, Hashtable eventParams, Calendar startTime)
	{
		this.eventName = eventName;
		this.eventParams = eventParams;
		this.startTime = startTime;
	}
	
	public String getEventName()
	{
		return eventName;
	}
	
	public Hashtable getEventParams()
	{
		return eventParams;
	}
	
	public Calendar getStartTime()
	{
		return startTime;
	}
}
