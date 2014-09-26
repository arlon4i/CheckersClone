package fi.bb.checkers.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.io.transport.ConnectionFactory;

public class HttpInterfaceInstance
{
	private static final int default_connection_timeout = 30000;
	protected HttpConnection connection = null;
	protected Timer timer = new Timer();
	protected InputStream inputStream;
	protected OutputStream outputStream;
	protected SocketTimeoutTask timertask;


	protected HttpInterfaceInstance()
	{
		super();
	}

	/**
	 * Creates a new request with the given API {@code name} and HTTP {@code method}
	 * @param name The displayed name of the API
	 * @param method The HTTP method of the request
	 */
	protected class SocketTimeoutTask extends TimerTask
	{
		public void run()
		{
			if (connection != null) 
			{
				try 
				{ 
					connection.close();
				} 
				catch (IOException e) 
				{
				} 
			}
		}
	}

	protected InputStream doPOST(String url, Hashtable headers, byte[] data) throws IOException,Exception
	{

		url += HttpInterface.getConnectionString();

		ConnectionFactory connectionFactoryInstance = new ConnectionFactory();
		try
		{
			connection = (HttpConnection) connectionFactoryInstance.getConnection(url).getConnection();
		} 
		catch (Exception e) 
		{
			closeConnection(connection);
			throw new RuntimeException ("Could not connect to the internet. Please try again later.");
		}
		if(connection == null)
		{
			throw new RuntimeException ("Could not connect to the internet. Please try again later.");
		}

		try
		{
			connection.setRequestMethod(HttpConnection.POST);
			connection.setRequestProperty(HttpProtocolConstants.HEADER_CACHE_CONTROL, "no-cache");
			connection.setRequestProperty(HttpProtocolConstants.HEADER_CONTENT_LENGTH, String.valueOf(data.length));

			if (headers != null)
			{
				Enumeration e = headers.keys();
				while (e.hasMoreElements())
				{
					String key = (String) e.nextElement();
					connection.setRequestProperty(key, (String) headers.get(key));
				}
			}

			timertask = new SocketTimeoutTask();
			timer.schedule(timertask, default_connection_timeout);

			try
			{
				outputStream = connection.openOutputStream();
				outputStream.write(data);
				outputStream.flush();
				outputStream.close();
			}
			catch (Exception e)
			{
			}
			finally
			{
				if (outputStream != null) 
				{
					try 
					{ 
						outputStream.close();
						outputStream = null;
					} 
					catch (IOException e) 
					{
					}
				}
			}

			int responsecode = connection.getResponseCode();
			if (responsecode != 200)
			{
				throw new RuntimeException("Could not connect to Server.\nPlease try again later.");
			}
			inputStream = connection.openInputStream();

			// get headers
			if (headers != null)
			{
				headers.clear();
				int i = 0;
				String key;

				while ((key = connection.getHeaderFieldKey(i)) != null)
				{
					headers.put(key, connection.getHeaderField(i));
					i++;
				}
			}
		}
		catch (Exception e) 
		{

			//want to catch any exception , close connection and throw again 
			closeConnection(connection);
			throw e;
		}
		/*finally
		{
			closeConnection(connection);
		}*///can't close stream here, since inputstream is still used...

		return inputStream;
	}


	public InputStream doGET(String url, Hashtable headers) throws IOException,Exception
	{
		String real_url = url + HttpInterface.getConnectionString();

		url = real_url;

		ConnectionFactory connectionFactoryInstance = new ConnectionFactory();
		try{
			connection = (HttpConnection) connectionFactoryInstance.getConnection(real_url).getConnection();
		} catch (Exception e) {
			closeConnection(connection);
			throw new RuntimeException ("Could not connect to the internet. Please try again later.");
		}
		if(connection == null)
		{
			throw new RuntimeException ("Could not connect to the internet. Please try again later.");
		}

		try 
		{
			connection.setRequestMethod(HttpConnection.GET);
			connection.setRequestProperty(HttpProtocolConstants.HEADER_CACHE_CONTROL, "no-cache");

			if (headers != null)
			{
				Enumeration e = headers.keys();
				while (e.hasMoreElements())
				{
					String key = (String) e.nextElement();
					connection.setRequestProperty(key, (String) headers.get(key));
				}
			}

			timertask = new SocketTimeoutTask();
			timer.schedule(timertask, default_connection_timeout);

			int responsecode = connection.getResponseCode();
			//RemoteLogger.log("RESPONSECODE", "code: |" + responsecode +"|" );
			if (responsecode != 200)
			{
				throw new RuntimeException("Could not connect to Server.\nPlease try again later.");
			}
			
			inputStream = connection.openInputStream();

			// get headers
			if (headers != null)
			{
				headers.clear();
				int i = 0;
				String key;

				while ((key = connection.getHeaderFieldKey(i)) != null)
				{
					headers.put(key, connection.getHeaderField(i));
					i++;
				}
			}
			return inputStream;
		}
		catch (Exception e) 
		{

			//want to catch any exception , close connection and throw again 
			closeConnection(connection);
			throw e;
		}
		/*finally
		{
			closeConnection(connection);
		}*///can't close stream here, since inputstream is still used...
	}

	private void closeConnection(HttpConnection con)
	{
		try 
		{
			if(con != null)
			{
				con.close();
			}
		} 
		catch (Exception e) {
		}
	}
}