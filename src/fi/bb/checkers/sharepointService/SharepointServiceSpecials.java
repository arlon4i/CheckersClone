package fi.bb.checkers.sharepointService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import fi.bb.checkers.network.HttpInterface;
import fi.bb.checkers.sharepointService.responsehandlers.SharepointFeaturedResponseHandler;
import fi.bb.checkers.sharepointService.responsehandlers.SharepointLoginResponseHandler;

public class SharepointServiceSpecials
{
	public static Hashtable tokens = new Hashtable();

	public static String login()
	{
		String request = "<Login xmlns=\"http://schemas.microsoft.com/sharepoint/soap/\"><username>shoprite@4imobile.co.za</username><password>whocares</password></Login>";
		Hashtable headers = new Hashtable();
		SharepointLoginResponseHandler response = new SharepointLoginResponseHandler();

		SharepointServiceRequest.sendRequest(request, headers, response);

		if (response.getResponseDesc().equalsIgnoreCase("noerror"))
		{
			tokens.clear();

			String cookie = (String) headers.get(HttpProtocolConstants.HEADER_SET_COOKIE);
			if (cookie == null)
			{
				// BIS makes changes to the headers, so have to parse out the FedAuth cookie
				cookie = (String) headers.get(HttpProtocolConstants.HEADER_SET_COOKIE.toLowerCase());
				cookie = cookie.substring(cookie.indexOf(response.getCookiename()));
			}

			tokens.put(HttpProtocolConstants.HEADER_COOKIE, cookie);
		}

		return response.getResponseDesc();
	}
	public static SharepointFeaturedResponseHandler getSpecials() throws Exception
	{
		InputStream stream = null;
		SharepointFeaturedResponseHandler response = new SharepointFeaturedResponseHandler();

		try
		{
			String url = "http://www.checkers.co.za/native/_vti_bin/ListData.svc/Container";

			Hashtable headers = new Hashtable();
			Enumeration e = tokens.keys();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				headers.put(key, tokens.get(key));
			}

			stream = HttpInterface.doGET(url, headers);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, response);
		}
		catch (Exception e)
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
					stream = null;
				} 
				catch (IOException e) 
				{
				}
			}
		}

		return response;
	}
}
