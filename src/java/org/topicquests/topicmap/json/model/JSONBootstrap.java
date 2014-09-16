/**
 * 
 */
package org.topicquests.topicmap.json.model;

import java.io.*;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.node.ITuple;
import org.topicquests.model.api.ITicket;

/**
 * @author Park
 * A class to replace hard-coded bootstrapping with
 * file loading from JSON files
 */
public class JSONBootstrap {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private INodeModel model;
	private final String path = "data/bootstrap/";

	/**
	 * 
	 */
	public JSONBootstrap(JSONTopicmapEnvironment env) {
		this.environment = env;
		this.database = (IJSONTopicDataProvider)environment.getDataProvider();
		this.model = database.getNodeModel();
		System.out.println("JSONBOOTSTRAP+");
	}

	public IResult bootstrap() {
		IResult result = new ResultPojo();
		File dir = new File(path);
		System.out.println("JSONBOOTSTRAP.bootstrap "+dir.getAbsolutePath());
		File files [] = dir.listFiles();
		int len = files.length;
		File f;
		for (int i=0;i<len;i++) {
			f = files[i];
			System.out.println(f.getAbsolutePath());
		}
		//TODO
		return result;
	}
}
