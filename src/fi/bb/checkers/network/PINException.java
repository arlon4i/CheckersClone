package fi.bb.checkers.network;

import java.io.IOException;

public class PINException extends IOException
{
	public PINException()
	{
		super();
	}

	public PINException(String msg)
	{
		super(msg);
	}
}
