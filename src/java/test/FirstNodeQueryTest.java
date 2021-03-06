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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IRelationsLegend;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.query.INodeQuery;
import org.topicquests.model.api.ITicket;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.NodeQuery;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 *
 */
public class FirstNodeQueryTest {
	private JSONTopicmapEnvironment environment;
//	private IJSONDocStoreModel jsonModel;
///	private INodeModel nodeModel;
	private IJSONTopicDataProvider database;
	private ITicket credentials;
	private String userId;
	private String lang = "en";
	private String smallImagePath = null;
	private String largeImagePath = null;
	private boolean isPrivate = false;
	//These fields will be used in subsequent testing
	public static final String
		CAUSE_LABEL 				= "causes",
		CAUSAL_RELATION_TYPE		=IRelationsLegend.CAUSES_RELATION_TYPE,
		MOLECULE_TYPE				= "MoleculeType",
		CO2_TYPE					= "CO2Type",
		METHANE_TYPE				= "MethaneType",
		ATMOSPHERIC_EVENT_TYPE		= "AtmosphericEventType",
				CLIMATE_CHANGE				= "ClimateChange",
				BAR_SCOPE					= "bar",
				FOO_SCOPE					= "foo";

	/**
	 * 
	 */
	public FirstNodeQueryTest() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
//		jsonModel = environment.getJSONModel();
//		nodeModel = environment.getTopicDataProvider().getNodeModel();
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		//create credentials
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
		userId = ITopicQuestsOntology.SYSTEM_USER;
		runTest();
	}
	
	void runTest() {
		IResult result = new ResultPojo();
		INodeQuery q = null;
		IResult r1 = database.getNode(CLIMATE_CHANGE, credentials);
		if (r1.hasError())
			result.addErrorString(r1.getErrorString());
		INode climatechange = (INode)r1.getResultObject();
		q = database.getNodeQuery(climatechange);
		q = q.setRelationType(CAUSAL_RELATION_TYPE, 0, -1, credentials);
		q = q.tupleHas(ITopicQuestsOntology.SCOPE_LIST_PROPERTY_TYPE, FOO_SCOPE);
		long count = q.count();
		System.out.println("Count "+count);
		IResult r2 = q.nodes(credentials);
		if (r2.hasError())
			result.addErrorString(r2.getErrorString());
		System.out.println("Nodes "+r2.getResultObject());
		List<INode> nodes = (List<INode>)r2.getResultObject();
		INode n = nodes.get(0);
		System.out.println(n.toJSON());
		environment.shutDown();
		
	}

}
