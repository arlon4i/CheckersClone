package com.wigroup.wiAppService.responsehandlers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.bb.checkers.datatypes.CampaignData;
import fi.bb.checkers.datatypes.CouponCategory;
import fi.bb.socialsharing.logger.RemoteLogger;

public class WiAppCampaignResponseHandler extends WiAppResponseHandler {

    private CampaignData campaign;
    private Vector campaignList = new Vector();
    private boolean isCategory= false;
    private CouponCategory couponCategory;
   
    public Vector getCampaignList() {
	return campaignList;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	super.startElement(uri, localName, qName, attributes);

	if (qName.equalsIgnoreCase("campaign")) {
	    campaign = new CampaignData();
	    	  campaignList.addElement(campaign);


	  
	   
	}
	if(qName.equalsIgnoreCase("category")){
		isCategory=true;	
		couponCategory= new CouponCategory();
		campaign.getCategoryList().addElement(couponCategory);		
	}
	
	
	
    }
    
    /**
     * 
     */
    public void endElement(String uri, String localName, String qName)
    		throws SAXException {
    	super.endElement(uri, localName, qName);
//    	if(qName.equalsIgnoreCase("campaign")){
//    		isEndTagCoupon=true;
//    	//	isCampaign=false;
//    	}
    	
//    	else 
   		if(qName.equalsIgnoreCase("category")){
    			isCategory= false;
    			//	isCampaign=true;
    	}
    	
    }
    
    
    
    

    public void characters(char ch[], int start, int length) throws SAXException {
	super.characters(ch, start, length);

	if (tagName.equalsIgnoreCase("id") &&!isCategory) {
	    campaign.setId(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("name") && !isCategory) {
	    campaign.setName(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("description")) {
	    campaign.setDescription(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("terms")) {
	    campaign.setTerms(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("expirydate")) {
	    campaign.setExpireDate(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("wicode")) {
	    campaign.setWiCode(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("imageurl")) {

	    String imageUrl = new String(ch, start, length);
	    String imageName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
	   campaign.setImageURL(imageUrl); 

	} else if (tagName.equalsIgnoreCase("amount")) {
	    campaign.setValue(new String(ch, start, length));
	}
	
	else if(tagName.equalsIgnoreCase("id") && isCategory){
		couponCategory.setId(new String(ch, start, length));
	}
	else if(tagName.equalsIgnoreCase("name") && isCategory){
		couponCategory.setName(new String(ch, start, length));
		
	}
  }

//	public CouponCategory getCouponCategory() {
//		return couponCategory;
//	}


    
}
