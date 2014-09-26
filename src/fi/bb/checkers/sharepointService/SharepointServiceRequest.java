package fi.bb.checkers.sharepointService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

import fi.bb.checkers.logger.RemoteLogger;
import fi.bb.checkers.network.HttpInterface;
import fi.bb.checkers.sharepointService.responsehandlers.SharepointResponseHandler;

public class SharepointServiceRequest
{
	public static void sendRequest(String request, Hashtable headers, SharepointResponseHandler response)
	{
		InputStream stream = null;
		try
		{
			String url = "http://www.checkers.co.za/native/_vti_bin/authentication.asmx";

			request = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
					+ request + "</soap:Body></soap:Envelope>";

			headers.put("Host", "www.checkers.co.za");
			headers.put("Content-Type", "text/xml; charset=utf-8");

			URLEncodedPostData encPostData = new URLEncodedPostData("UTF-8", false);
			encPostData.setData(request);
			byte[] postData = encPostData.toString().getBytes("UTF-8");
			stream = HttpInterface.doPOST(url, headers, postData);

			// pipe directly into saxparser due to memory constraints
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, response);

		} catch (Exception e)
		{
			RemoteLogger.log("WiAppServiceRequest", "Request: " + request+" | "  + e.toString());
			//REMOVED_OLD FlurryAgent.onError("99", e.toString(), "Socket");
			response.setResponseDesc("Could not connect to Server.\nPlease try again later.");
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
}
