/*
 * Copyright 2013, TopicQuests
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
package test;

import java.util.*;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.INodeModel;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;

/**
 * @author park
 *
 */
public class FirstBootTest {
	private JSONTopicmapEnvironment environment;
	private IJSONDocStoreModel jsonModel;
	private INodeModel nodeModel;
	private IDataProvider database;
	private Set<String>credentials;
	/**
	 * 
	 */
	public FirstBootTest() {
		environment = new JSONTopicmapEnvironment();
		jsonModel = environment.getJSONModel();
		nodeModel = environment.getTopicDataProvider().getNodeModel();
		database = environment.getTopicDataProvider();
		//create credentials
		credentials = new HashSet<String>();
		credentials.add(ITopicQuestsOntology.SYSTEM_USER);
		String firstId = "FirstTopic";
		String user = ITopicQuestsOntology.SYSTEM_USER;
		//newNode(String locator,String label, String description, String lang, 
		//  String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
		IResult r = nodeModel.newNode(firstId, "My very first topic", 
				"Something to think about", "en", user, null, null, false);
		INode n1 = (INode)r.getResultObject();
		System.out.println("FBT-0 "+n1.toJSON());
		r = database.putNode(n1);
		System.out.println("FBT-1 "+r.getErrorString());
		r = database.getNode(firstId, credentials);
		System.out.println("FBT-2 "+(r.getResultObject() == null)+" "+r.getErrorString());
		if (r.getResultObject() != null) {
			n1 = (INode)r.getResultObject();
			System.out.println(n1.toJSON());
		}
		
		
		environment.shutDown();
	}
/**
{
    "locator": "FirstTopic",
    "details": [
        "Something to think about"
    ],
    "isPrivate": "false",
    "creatorId": "SystemUser",
    "label": [
        "My very first topic"
    ],
    "lastEditDate": "2013-11-26 11:53:24",
    "createdDate": "2013-11-26 11:53:24"
}
 */
}
