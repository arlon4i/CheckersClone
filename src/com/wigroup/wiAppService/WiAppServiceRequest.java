package com.wigroup.wiAppService;

import java.io.IOException;
import java.io.InputStream;

import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

import com.wigroup.wiAppService.responsehandlers.WiAppResponseHandler;

import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.network.HttpInterface;

public class WiAppServiceRequest
{
	// LIVE - http://196.37.63.100/shoprite/http-to-socket/checkers/index.php?
	// QA - http://qa.wigroup.co/shoprite/http_to_socket/index.php?
	public static void sendRequest(String request, WiAppResponseHandler response)
	{
		InputStream stream = null;
		try
		{
			RemoteLogger.log("WiAppServiceRequest", "Request request: " + request);
			
			String url = "http://196.37.63.100/shoprite/http-to-socket/checkers/index.php?" + encodeUrl(request);

			RemoteLogger.log("WiAppServiceRequest", "Request url: " + url);
			
			stream = HttpInterface.doGET(url, null);

			// pipe directly into saxparser due to memory constraints
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, response);
		} catch (Exception e)
		{
			RemoteLogger.log("WiAppServiceRequest", "Request: " + request+"\n\n"  + e.toString());
			
			//REMOVED_OLD FlurryAgent.onError("99", e.toString(), "Socket");

			response.setResponseCode("99");
			response.setResponseMessage("Could not connect to Server.\nPlease try again later.");
		}
		finally
		{
			if (stream != null) 
			{
				try 
				{ 
					stream.close();
					stream = null;
				} 
				catch (IOException e) 
				{
				}
			}
		}
	}

	private static String encodeUrl(String hsURL)
	{
		URLEncodedPostData urlEncoder = new URLEncodedPostData("UTF-8", false);
		urlEncoder.append("xml", hsURL);
		hsURL = urlEncoder.toString();
		return hsURL;
	}

}
