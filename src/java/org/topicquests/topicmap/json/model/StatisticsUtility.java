/**
 * 
 */
package org.topicquests.topicmap.json.model;

import java.util.Iterator;

//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.topicquests.util.TextFileHandler;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author park
 *
 */
public class StatisticsUtility {
	private JSONObject data;
	private final String
		TOPIC_NODES				= "Topic Nodes",
		FILE_NAME			 	= "TopicMapStatistics.json";
	
	/**
	 * 
	 */
	public StatisticsUtility() {
		loadStats();
	}
	
	public void setValue(String key, Long value) {
		synchronized(data) {
			data.put(key, value);
		}
	}
	
	public void addTopicNode() {
		addToKey(TOPIC_NODES);
	}
	
	/**
	 * General purpose: any routine can choose a key
	 * and add to it, avoiding the reserved keys defined here
	 * @param key
	 */
	public void addToKey(String key) {
		synchronized(data) {
			Long v = (Long)data.get(key);
			if (v == null)
				v = new Long(1);
			else
				v += 1;
			data.put(key, v);
		}

	}
	


	public String getStats() {
		System.out.println("GETSTATS "+data);
		synchronized(data) {
			StringBuffer buf = new StringBuffer();
			Long x = null;
			String key;
			Iterator<String>itr = data.keySet().iterator();
			while (itr.hasNext()) {
				key = itr.next();
				x = (Long)data.get(key);
				if (x != null) {
					buf = buf.append(key).append(": ").append(x.toString()).append("\n");
				}
			}
			return buf.toString();
		}
	}
	
	public String toJSON() {
		synchronized(data) {
			return data.toJSONString();
		}
	}
	private void loadStats() {
		TextFileHandler h = new TextFileHandler();
		java.io.File f = new java.io.File(FILE_NAME);
		if (f.exists()) {
			String json = h.readFile(FILE_NAME);
			if (json == null)
				data = new JSONObject();
			else {
				try {
					data = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else
			data = new JSONObject();
	}
	public void saveStats() {
		TextFileHandler h = new TextFileHandler();
		h.writeFile(FILE_NAME, data.toJSONString());
	}
}
