package com.wigroup.wiAppService.responsehandlers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import fi.bb.checkers.datatypes.Title;

public class WiappTitleResponseHandler extends WiAppResponseHandler {


    private Vector titles = new Vector();
    private Title title; 


    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	super.startElement(uri, localName, qName, attributes);

	if (qName.equalsIgnoreCase("title")) {	
		title= new Title();
		titles.addElement(title);			
		}	
    }

 
    public void characters(char ch[], int start, int length) throws SAXException {
	super.characters(ch, start, length);

	if (tagName.equalsIgnoreCase("id")) {
		title.setId(new String(ch, start, length));
	} else if (tagName.equalsIgnoreCase("description")) {
		title.setDescription(new String(ch, start, length));
	}
    }

	public Vector getTitles() {
		return titles;
	}

	public void setTitles(Vector titles) {
		this.titles = titles;
	}
}
