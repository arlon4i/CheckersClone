package fi.bb.checkers.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.WLANInfo;

public class HttpInterface
{
	private static Timer timer = new Timer();

	private static final int connection_request_timeout = 30000; // 30s to establish connection
	private static final int connection_response_timeout = 30000; // 30s to retrieve the response

	private static class SocketTimeoutTask extends TimerTask
	{
		HttpConnection connection;

		public SocketTimeoutTask(HttpConnection connection)
		{
			this.connection = connection;
		}
		public void run()
		{
			try
			{
				connection.close();
			} catch (Exception e)
			{
			}
		}
	}

	/**
	 * Returns the input stream of the HTTP call, headers that are returned by the server are inserted into the headers hashtable, if non-null.
	 * 
	 * @param url
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public static InputStream doGET(String url, Hashtable headers) throws IOException, Exception
	{
		return (new HttpInterfaceInstance()).doGET(url, headers);
		/*url += getConnectionString();
		RemoteLogger.log("HttpInterface", "doGET: " + url + " | headers: " + headers);

		ConnectionFactory connectionFactoryInstance = new ConnectionFactory();
		HttpConnection connection = (HttpConnection) connectionFactoryInstance.getConnection(url).getConnection();

		connection.setRequestMethod(HttpConnection.GET);
		connection.setRequestProperty(HttpHeaders.HEADER_CACHE_CONTROL, "no-cache");

		if (headers != null)
		{
			Enumeration e = headers.keys();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				connection.setRequestProperty(key, (String) headers.get(key));
			}
		}

		SocketTimeoutTask timertask = new SocketTimeoutTask(connection);
		timer.schedule(timertask, connection_request_timeout);

		int responsecode = connection.getResponseCode();
		RemoteLogger.log("RESPONSECODE", "code: |" + responsecode +"|" );
		if (responsecode != 200)
		{
			throw new RuntimeException("Could not connect to Server.\nPlease try again later.");
		}
		InputStream inputStream = connection.openInputStream();

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

		// reset the timeout (connection has been established successfully)
		timertask.cancel();
		timertask = new SocketTimeoutTask(connection);
		timer.schedule(timertask, connection_response_timeout);

		return inputStream;*/
	}

	// ---------------------------- POST ---------------------------
	/**
	 * Returns the input stream of the HTTP call, headers that are returned by the server are inserted into the headers hashtable, if non-null.
	 * 
	 * @param url
	 * @param headers
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static InputStream doPOST(String url, Hashtable headers, byte[] data) throws IOException, Exception
	{
		return (new HttpInterfaceInstance()).doPOST(url, headers, data);
		/*url += getConnectionString();
		RemoteLogger.log("HttpInterface", "doPOST: " + url + " | headers: " + headers);

		ConnectionFactory connectionFactoryInstance = new ConnectionFactory();
		HttpConnection connection = (HttpConnection) connectionFactoryInstance.getConnection(url).getConnection();

		connection.setRequestMethod(HttpConnection.POST);
		connection.setRequestProperty(HttpHeaders.HEADER_CACHE_CONTROL, "no-cache");
		connection.setRequestProperty(HttpHeaders.HEADER_CONTENT_LENGTH, String.valueOf(data.length));

		if (headers != null)
		{
			Enumeration e = headers.keys();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				connection.setRequestProperty(key, (String) headers.get(key));
			}
		}

		SocketTimeoutTask timertask = new SocketTimeoutTask(connection);
		timer.schedule(timertask, connection_request_timeout);

		OutputStream outputStream = connection.openOutputStream();
		outputStream.write(data);
		outputStream.flush();
		outputStream.close();

		int responsecode = connection.getResponseCode();
		if (responsecode != 200)
		{
			throw new RuntimeException("Could not connect to Server.\nPlease try again later.");
		}
		InputStream inputStream = connection.openInputStream();

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

		// reset the timeout (connection has been established successfully)
		timertask.cancel();
		timertask = new SocketTimeoutTask(connection);
		timer.schedule(timertask, connection_response_timeout);

		return inputStream;*/
	}
	// ----------------------------------------------------------------

	public static String readStream(InputStream stream) throws IOException
	{
		ByteArrayOutputStream baos = null;
		String toreturn = "";
		try
		{
			baos = new ByteArrayOutputStream();

			int data;
			while ((data = stream.read()) != -1)
			{
				baos.write(data);
			}

			toreturn = baos.toString();
			//baos.close(); closed in finally
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (Exception e2)
				{
				}
			}
			if (baos != null)
			{
				try
				{
					baos.close();
				}
				catch (Exception e2)
				{
				}
			}
		}
		return toreturn;
	}

	public static String printStream(InputStream stream) throws IOException
	{
		ByteArrayOutputStream baos = null;
		String toreturn = "";
		try
		{
			baos = new ByteArrayOutputStream();

			int data;
			while ((data = stream.read()) != -1)
			{
				baos.write(data);
			}

			toreturn = baos.toString();
			//baos.close(); closed in finally
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (baos != null)
			{
				try
				{
					baos.close();
				}
				catch (Exception e2)
				{
				}
			}
		}
		return toreturn;
	}
	
	/**
	 * Gets the connection string needed for HTTP connections.
	 * 
	 * @return The connection string.
	 */
	public static String getConnectionString()
	{

		// read the coverage type
		boolean isWifi = WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED;
		boolean isMDS = CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS);
		boolean isBIS = CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B);
		boolean isDirect = CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_DIRECT);
		boolean noCoverage = CoverageInfo.getCoverageStatus() == CoverageInfo.COVERAGE_NONE;

		// get the connection string
		String connectionString = "";
		if (isWifi)
		{
			connectionString = ";interface=wifi";
		}
		else if (isBIS)
		{

			String carrierUid = getCarrierBIBSUid();
			if (carrierUid == null)
			{
				connectionString = ";deviceside=true;apn=internet";
			}
			else
			{
				connectionString = ";deviceside=false;connectionUID=" + carrierUid + ";ConnectionType=mds-public";
			}
		}
		else if (isMDS)
		{
			connectionString = ";deviceside=false";
		}
		else if (isDirect)
		{
			String carrierUid = getCarrierBIBSUid();
			if (carrierUid == null)
			{
				connectionString = ";deviceside=true";
			}
			else
			{
				connectionString = ";deviceside=false;ConnectionUID=" + carrierUid + ";ConnectionType=mds-public";
			}
		}
		else if (noCoverage)
		{
		}
		else
		{
			connectionString = ";deviceside=true";
		}
		return connectionString;
	}

	/**
	 * Looks through the phone's service book for a carrier provided BIBS network
	 * 
	 * @return The uid used to connect to that network.
	 */
	private static String getCarrierBIBSUid()
	{
		ServiceRecord[] records = ServiceBook.getSB().getRecords();
		int currentRecord;

		for (currentRecord = 0; currentRecord < records.length; currentRecord++)
		{
			if (records[currentRecord].getCid().toLowerCase().equals("ippp"))
			{
				if (records[currentRecord].getName().toLowerCase().indexOf("bibs") >= 0)
				{
					return records[currentRecord].getUid();
				}
			}
		}

		return null;
	}
}