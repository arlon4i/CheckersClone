package fi.bb.checkers.logger;

import java.io.OutputStream;

import javax.microedition.io.HttpConnection;

import fi.bb.checkers.network.HttpInterface;

import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.component.Dialog;

public class RemoteLogger
{
	// Thread pool
	private static int thread_pool_size = 3;
	private static final Queue logging_queue = new Queue();
	private static LoggingThread[] thread_pool = new LoggingThread[thread_pool_size];

	//Logger constants
	public static String channel = "default";
	public static String host_filename = "CheckersQR.txt";
	public static String remote_filename = "log.txt";
	public static String host_url = "http://192.168.88.5/Logger/logger.php";
	public static String remote_url = "http://demo.4imobile.co.za/Logger/logger.php";
	public static String boundary =  "*****";
	public static String[] knownDevice = new String[]{"549981686","643625962", "658670551"};
	
	//Settings
	public static boolean logging_enabled = true;
	public static boolean onlyKnownDevices = false;
	public static boolean local_server = true;
	
	public static void logJson(String tag , String json_msg)
	{
		RemoteLogger.logMessage(tag, json_msg,true);
	}
	public static void log(String tag,String msg)
	{
		RemoteLogger.logMessage(tag, msg,false);	
	}
	
	private static void logMessage(String tag,String msg,boolean is_json)
	{
		if (logging_enabled == false)
		{
			return;
		}
		
		boolean should_log = true;
		if(onlyKnownDevices == true)
		{
			should_log = false;
			String deviceId = ""+DeviceInfo.getDeviceId();
			
			for(int i=0;i<knownDevice.length;i++)
			{
				if(knownDevice[i].equals(deviceId))
				{
					should_log = true;
				}
			}
		}
		if(should_log == false)
			return;
		
		synchronized (logging_queue)
		{
			logging_queue.put(new LoggingQueueItem(tag, msg, is_json));
		}

		int threads_needed = logging_queue.size();
		for (int i = 0; i < thread_pool.length; i++)
		{
			if (threads_needed == 0) break;

			if (thread_pool[i] == null || !thread_pool[i].isAlive())
			{
				thread_pool[i] = new LoggingThread();
				thread_pool[i].start();
				threads_needed--;
			}
		}
	}
	
	private static class LoggingThread extends Thread
	{
		public LoggingThread()
		{
			setPriority(Thread.NORM_PRIORITY);//WAS MIN_PRIORITY
		}

		public void run()
		{
			while (!logging_queue.isEmpty())
			{
				try
				{
					final LoggingQueueItem queued_item;
					synchronized (logging_queue)
					{
						queued_item = (LoggingQueueItem) logging_queue.get();
					}

					doPost(queued_item.tag, queued_item.msg, queued_item.is_json);

				} catch (Exception e)
				{
				}
			}
		}
	}

	private static class LoggingQueueItem
	{
		String tag, msg;
		boolean is_json;

		public LoggingQueueItem(String tag, String msg, boolean is_json)
		{
			this.tag = tag;
			this.msg = msg;
			this.is_json = is_json;
		}
	}
	
	private static void doPost(String tag, String msg, boolean is_json)
	{
		String exception = "";
		HttpConnection connection = null;
		OutputStream outputStream = null;
		
		try
		{
			String json_str = "0";
			if(is_json)
	        {	
				json_str = "1";
	        }
			
			String url = host_url + HttpInterface.getConnectionString();
			String filename = host_filename;
			
			if (!local_server)
			{
				url = remote_url + HttpInterface.getConnectionString();
				filename = remote_filename;
			}
			exception = url;
			ConnectionFactory connectionFactoryInstance = new ConnectionFactory();
			connection = (HttpConnection) connectionFactoryInstance.getConnection(url).getConnection();
			
			connection.setRequestMethod(HttpConnection.POST);
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			connection.setRequestProperty("Connection", "Keep-Alive");
            
			String lineEnd = "\r\n";
		    String twoHyphens = "--";
			
			String postString = "";
			postString += (lineEnd);
            postString += (twoHyphens + boundary + lineEnd);
            
            postString += ("Content-Disposition: form-data; name=\"msg\""+ lineEnd + lineEnd);
            postString += (msg);
            postString += (lineEnd);
            postString += (twoHyphens + boundary + lineEnd);
            
            postString += ("Content-Disposition: form-data; name=\"tag\""+ lineEnd + lineEnd);
            postString += (tag);
            postString += (lineEnd);
            postString += (twoHyphens + boundary + lineEnd);    
			
            postString += ("Content-Disposition: form-data; name=\"channel\""+ lineEnd + lineEnd);
            postString += (channel);
            postString += (lineEnd);
            postString += (twoHyphens + boundary + lineEnd); 
            
            postString += ("Content-Disposition: form-data; name=\"filename\""+ lineEnd + lineEnd);
            postString += (filename);
            postString += (lineEnd);
            postString += (twoHyphens + boundary + lineEnd); 
            
            postString += ("Content-Disposition: form-data; name=\"json\""+ lineEnd + lineEnd);
            postString += (json_str);
            postString += (lineEnd);
            postString += (twoHyphens + boundary + lineEnd); 
            
            URLEncodedPostData encPostData = new URLEncodedPostData("UTF-8", false);
			encPostData.setData(postString);
			byte[] postData = encPostData.toString().getBytes("UTF-8");
			
			outputStream = connection.openOutputStream();
			outputStream.write(postData);
			outputStream.flush();
			outputStream.close();

			int responsecode = connection.getResponseCode();
			if (responsecode != 200)
			{
			//	throw new RuntimeException ("Server response " + responsecode);
			}
		} 
		catch (Exception e)
		{
			Dialog.inform("RemoteLogger.Exception (" + exception + "): " + e.getMessage());
		}
		finally
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.close();
				}
				catch (Exception e) 
				{
				}
			}
			
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (Exception e) 
				{
				}
			}
		}
	}
}
