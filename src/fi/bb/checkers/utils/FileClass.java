package fi.bb.checkers.utils;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class FileClass
{
	public static final String FILEPATH = getSdCardDir();

	private static String getSdCardDir()
	{
		if (ApplicationInterface.isSDCardIn())
			return "file:///SDCard/checkers/";

		return "file:///store/home/user/checkers/";
	}

	public static boolean exists(String filename)
	{
		boolean response = false;

		try
		{
			FileConnection fileConnection = (FileConnection) Connector.open(filename);

			if (fileConnection.exists() == true)
			{
				response = true;
			}
			else
			{
				response = false;
			}

			fileConnection.close();
		} catch (IOException exc)
		{
			response = false;
		}

		return response;
	}

	public static void hideFile(String filename)
	{
		try
		{
			FileConnection fileConnection = (FileConnection) Connector.open(filename);
			fileConnection.setHidden(true);
			fileConnection.close();
		} catch (IOException exc)
		{
		}
	}
}
