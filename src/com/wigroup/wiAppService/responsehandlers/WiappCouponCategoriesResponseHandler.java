package com.wigroup.wiAppService.responsehandlers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fi.bb.checkers.datatypes.CouponCategory;

public class WiappCouponCategoriesResponseHandler extends WiAppResponseHandler {


    private Vector couponCategories = new Vector();
    private CouponCategory category; 


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	super.startElement(uri, localName, qName, attributes);

	if (qName.equalsIgnoreCase("category")) {	
		category= new CouponCategory();
		couponCategories.addElement(category);			
		}	
    }

 
    public void characters(char ch[], int start, int length) throws SAXException {
	super.characters(ch, start, length);

	if (tagName.equalsIgnoreCase("id")) {
		category.setId(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("name")) {
		category.setName(new String(ch, start, length));
	}
    }

	public Vector getCouponCategories() {
		return couponCategories;
	}

	public void setCouponCategories(Vector couponCategories) {
		this.couponCategories = couponCategories;
	}
    
    
    
}
