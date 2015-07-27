/**
 * 
 */
package org.topicquests.topicmap.json.model;

import java.io.*;
import java.util.*;

//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.util.TextFileHandler;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.Node;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.node.INode;
//import org.topicquests.model.api.node.INodeModel;
//import org.topicquests.model.api.node.ITuple;
import org.topicquests.model.api.ITicket;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


/**
 * @author Park
 * A class to replace hard-coded bootstrapping with
 * file loading from JSON files
 */
public class JSONBootstrap {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private String userId = ITopicQuestsOntology.SYSTEM_USER;
	private ITicket credentials;
	private final String path = "data/bootstrap/";
	private TextFileHandler handler;

	/**
	 * 
	 */
	public JSONBootstrap(JSONTopicmapEnvironment env) {
		this.environment = env;
		this.database = (IJSONTopicDataProvider)environment.getDataProvider();
		this.credentials = new TicketPojo();
		credentials.setUserLocator(userId);
		handler = new TextFileHandler();
		System.out.println("JSONBOOTSTRAP+");
	}

	public IResult bootstrap() {
		//environment.logDebug("JSONBootstrap- ");
		IResult result = new ResultPojo();
		IResult r = database.getNode(ITopicQuestsOntology.TYPE_TYPE, credentials);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		//environment.logDebug("JSONBootstrap-1 "+r.hashCode()+" "+r.getResultObject());
		if (r.getResultObject() == null) {
			File dir = new File(path);
			System.out.println("JSONBOOTSTRAP.bootstrap "+dir.getAbsolutePath());
			File files [] = dir.listFiles();
			int len = files.length;
			File f;
			r = null;
			for (int i=0;i<len;i++) {
				f = files[i];
				System.out.println(f.getAbsolutePath());
				if (f.getName().endsWith(".json")) {
					r = importJSONFile(f);
					if (r.hasError())
						result.addErrorString(r.getErrorString());
				}
			}
		}
		return result;
	}
	
	/**
	 * Testing the first line of each file. Return <code>true</code>
	 * if this class already exists.
	 * @param p
	 * @return
	 */
	private boolean seenThis(JSONObject p) {
		boolean result = false;
		String lox = (String)p.get("lox");
		IResult r = database.getNode(lox, credentials);
		result = (r.getResultObject() != null);
		return result;
	}
	
	private IResult importJSONFile(File f) {
		environment.logDebug(f.getName());
		IResult result = new ResultPojo();
		String json = handler.readFile(f);
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		try {
			JSONObject jo = (JSONObject)p.parse(json);
			List<JSONObject> o = (List<JSONObject>)jo.get("nodes");
			if (o != null) {
				IResult r;
				Iterator<JSONObject>itr = o.iterator();
				JSONObject x;
				boolean isFirst = true;
				while (itr.hasNext()) {
					x = itr.next();
					if (x.get("lox") != null) {
						if (isFirst) {
							if (seenThis(x)) {
								return result;
							}
							isFirst = false;
						}
						r = buildProxy(x);
						if (r.hasError())
							result.addErrorString(r.getErrorString());
					}
				}
			} else {
				result.addErrorString(f.getName()+" MISSING Data");
			}
		} catch (Exception e) {
			environment.logError("JSONBootstrap1 "+e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	private IResult buildProxy(JSONObject jo)  {
		environment.logDebug(jo.toJSONString());
		IResult result = new ResultPojo();
		//JSONParser p = new JSONParser();
		try {
		//	JSONObject jo = (JSONObject)p.parse(json);
			INode n = new Node(jo);
			n.setCreatorId(userId);
			Date d = new Date();
			n.setDate(d);
			n.setLastEditDate(d);
			n.setIsFederated(false);
			n.setIsPrivate(false);
			n.setVersion(Long.toString(System.currentTimeMillis()));
			IResult r = database.putNode(n, false);
			if (r.hasError())
				result.addErrorString("JSONBootstrap2 "+r.getErrorString());
		} catch (Exception e) {
			environment.logError("JSONBootstrap3 "+e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
}
