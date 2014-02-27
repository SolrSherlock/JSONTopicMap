/*
 * Copyright 2012,2013 TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.topicmap.json.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLDecoder;

import org.nex.util.DateUtil;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.ITuple;
import org.topicquests.model.api.IXMLFields;
import org.topicquests.model.Node;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author park
 *
 */
public class ImportPullParser {
	private IJSONTopicDataProvider database;
	private INode theNode = null;

	/**
	 * 
	 */
	public ImportPullParser(IJSONTopicDataProvider db) {
		database = db;
	}

	public IResult parse(String filePath) {
		IResult result = new ResultPojo();
		try {
			// open a file
			File f = new File(filePath);
			// grab an inputstream
			FileInputStream fis = new FileInputStream(f);
			// parse this puppy
			parse(result, fis);
		} catch (Exception e) {
			e.printStackTrace();
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	void parse(IResult result, InputStream is) {
	      try {
	         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	         factory.setNamespaceAware(false);
	         XmlPullParser xpp = factory.newPullParser();

	         BufferedReader in = new BufferedReader(new InputStreamReader(is));
	         xpp.setInput(in);
	         String temp = null;
	         String text = null;
	         String name = null;
	         String locator = null;
	         String key = null;
	         String value = null;
	         Date date = null;
	         String userId = ITopicQuestsOntology.SYSTEM_USER; // default
	         Map<String,String> attributes = null;
	         ArrayList<List> theList = null;
	         Map<String,String> stringMap = null;
	         int eventType = xpp.getEventType();
	         boolean isStop = false;
	         while (!(isStop || eventType == XmlPullParser.END_DOCUMENT)) {
	            temp = xpp.getName();
	            text = null;
	            attributes = getAttributes(xpp);
	            if (attributes != null) {
	            	name = (String)attributes.get("name");
	            	value = (String)attributes.get("value");
	            } else {
	            	name = null;
	            	value = null;
	            }
	            if(eventType == XmlPullParser.START_DOCUMENT) {
	                System.out.println("Start document");
	            } else if(eventType == XmlPullParser.END_DOCUMENT) {
	                System.out.println("End document");
	            } else if(eventType == XmlPullParser.START_TAG) {
	                System.out.println("Start tag "+temp + " | "+attributes);
	                if(temp.equalsIgnoreCase(IXMLFields.NODES)) {

	                } else if(temp.equalsIgnoreCase(IXMLFields.NODE)) {
	                	locator = (String)attributes.get(IXMLFields.LOCATOR_ATT);
	                	theNode = new Node();
	                	theNode.setLocator(locator);
	                } else if (temp.equalsIgnoreCase(IXMLFields.TUPLES)) {

	                } else if (temp.equalsIgnoreCase(IXMLFields.TUPLE)) {
	                	locator = (String)attributes.get(IXMLFields.LOCATOR_ATT);
	                	theNode = new Node();
	                	theNode = new Node();
	                	theNode.setLocator(locator);
	                } else if (temp.equalsIgnoreCase(IXMLFields.PROPERTIES)) {

	                } else if (temp.equalsIgnoreCase(IXMLFields.PROPERTY)) {
	                	key = (String)attributes.get(IXMLFields.KEY_ATT);
	                } else if (temp.equalsIgnoreCase(IXMLFields.VALUE)) {
	                } else if (temp.equalsIgnoreCase(IXMLFields.DATABASE)) {
	                	
	                }
	            } else if(eventType == XmlPullParser.END_TAG) {
	                System.out.println("End tag "+temp+" // "+text);
	                if(temp.equalsIgnoreCase(IXMLFields.NODES)) {

	                } else if(temp.equalsIgnoreCase(IXMLFields.NODE)) {
	                	database.putNode(theNode);
	                	theNode = null;
	                	locator = null;
	                } else if (temp.equalsIgnoreCase(IXMLFields.TUPLES)) {

	                } else if (temp.equalsIgnoreCase(IXMLFields.TUPLE)) {
	                	database.putNode(theNode);
	                	theNode = null;
	                	locator = null;
	                } else if (temp.equalsIgnoreCase(IXMLFields.PROPERTIES)) {

	                } else if (temp.equalsIgnoreCase(IXMLFields.PROPERTY)) {
	                	key = null;
	                	value = null;
	                } else if (temp.equalsIgnoreCase(IXMLFields.VALUE)) {
	                	value = URLDecoder.decode(text,"UTF-8");
	                	if (key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY))  {
	                		date = getDate(value);
	                		theNode.setDate(date);
	                	} else if (key.equals(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY)) {
	                		date = getDate(value);//HandyTools.toDate(value);
	                		theNode.setLastEditDate(date);
	                	} else if (key.equals(ITopicQuestsOntology.IS_PRIVATE_PROPERTY)) {
	                		theNode.setIsPrivate(value);
	                	} else if (key.equals(ITopicQuestsOntology.TUPLE_IS_TRANSCLUDE_PROPERTY)) {
	                		((ITuple)theNode).setIsTransclude(value);
	                	} else if (key.equals(ITopicQuestsOntology.LABEL_PROPERTY)) {
	                		theNode.addLabel(value, "en", userId, false);
	                	} else if (key.equals(ITopicQuestsOntology.SMALL_LABEL_PROPERTY)) {
	                		theNode.addSmallLabel(value, "en", userId, false);
	                	} else if (key.equals(ITopicQuestsOntology.DETAILS_PROPERTY)) {
	                		theNode.addDetails(value, "en", userId, false);
	                		
	                	} else
	                		theNode.addPropertyValue(key, value);
	                	if (key.equals(ITopicQuestsOntology.CREATOR_ID_PROPERTY))
	                		userId = value;
	                } else if (temp.equalsIgnoreCase(IXMLFields.DATABASE)) {
	                	
	                }
	            } else if(eventType == XmlPullParser.TEXT) {
//	                System.out.println("Text "+id+" // "+xpp.getText());
	                text = xpp.getText().trim();
	             } else if(eventType == XmlPullParser.CDSECT) {
	     //         System.out.println("Cdata "+id+" // "+xpp.getText());
	                text = xpp.getText().trim();
	            }
	            eventType = xpp.next();
	          }
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	result.addErrorString(e.getMessage());
	        } 
		}
	
	/**
	 * Code found at http://stackoverflow.com/questions/424522/how-can-i-recognize-the-zulu-time-zone-in-java-dateutils-parsedate
	 * @param zulu
	 * @return
	 */
	Date getDate(String timestamp) {
		return DateUtil.fromDefaultTimestamp(timestamp);
	}
	    /**
	     * Return null if no attributes
	     */
	    Map<String,String> getAttributes(XmlPullParser p) {
	      Map <String,String>result = null;
	      int count = p.getAttributeCount();
	      if (count > 0) {
	        result = new HashMap<String,String>();
	        String name = null;
	        for (int i = 0; i < count; i++) {
	          name = p.getAttributeName(i);
	          result.put(name,p.getAttributeValue(i));
	        }
	      }
	      return result;
	    }
}
