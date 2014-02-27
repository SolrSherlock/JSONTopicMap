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

import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.ITicket;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IRelationsLegend;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.legends.IEventLegend;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.node.ITuple;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 *
 */
public class BuildHugeTopicMap {
	private JSONTopicmapEnvironment environment;
	private IJSONDocStoreModel jsonModel;
	private INodeModel nodeModel;
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
	public BuildHugeTopicMap() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		jsonModel = environment.getJSONModel();
		nodeModel = environment.getDataProvider().getNodeModel();
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		//create credentials
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
		userId = ITopicQuestsOntology.SYSTEM_USER;
		buildTM();
	}
	
	void buildTM() {
		IResult result = new ResultPojo();
		//newSubclassNode(String locator,String superclassLocator,String label, 
		//  String description, String lang, String userId, String smallImagePath, 
		//  String largeImagePath, boolean isPrivate)
		IResult r;
		INode x = nodeModel.newSubclassNode(ATMOSPHERIC_EVENT_TYPE, IEventLegend.EVENT_TYPE, "Atmospheric Event", 
				"Events related to atmospheric conditions, weather, and climate", lang, userId, smallImagePath, largeImagePath, isPrivate);
		r = database.putNode(x);
		INode cc = nodeModel.newSubclassNode(CLIMATE_CHANGE, ATMOSPHERIC_EVENT_TYPE, "Climate Change", 
				"Major changes in climate", lang, userId, smallImagePath, largeImagePath, isPrivate);
		r = database.putNode(cc);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		INode y = nodeModel.newSubclassNode(MOLECULE_TYPE, ITopicQuestsOntology.CLASS_TYPE, "Molecule Type", 
				"Substances composed of many atoms", lang, userId, smallImagePath, largeImagePath, isPrivate);
		r = database.putNode(y);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		INode co2 = nodeModel.newSubclassNode(CO2_TYPE, MOLECULE_TYPE, "CO2", 
				"Carbon dioxide", lang, userId, smallImagePath, largeImagePath, isPrivate);
		r = database.putNode(co2);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		INode methane = nodeModel.newSubclassNode(METHANE_TYPE, MOLECULE_TYPE, "Methane", 
				"Methane", lang, userId, smallImagePath, largeImagePath, isPrivate);
		r = database.putNode(methane);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		//now wire them
		r = nodeModel.relateExistingNodes(co2, cc, CAUSAL_RELATION_TYPE, userId, smallImagePath, largeImagePath, false, isPrivate);
		IResult r1 = database.getTuple((String)r.getResultObject(), credentials);
		if (r1.hasError())
			result.addErrorString(r.getErrorString());
		ITuple t1 = (ITuple)r1.getResultObject();
		//co2 is in the FOO_SCOPE
		t1.addScope(FOO_SCOPE);
		r1 = database.putTuple(t1);
		if (r1.hasError())
			result.addErrorString(r.getErrorString());
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		r = nodeModel.relateExistingNodes(methane, cc, CAUSAL_RELATION_TYPE, userId, smallImagePath, largeImagePath, false, isPrivate);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		r1 = database.getTuple((String)r.getResultObject(), credentials);
		if (r1.hasError())
			result.addErrorString(r.getErrorString());
		ITuple t2 = (ITuple)r1.getResultObject();
		//methane is in the BAR_SCOPE
		t2.addScope(BAR_SCOPE);
		r1 = database.putTuple(t1);
		if (r1.hasError())
			result.addErrorString(r.getErrorString());
		System.out.println("DONE "+result.getErrorString());
		environment.shutDown();

	}

}
/**
<node locator="CO2Type">
  <property key="subOf" >
    <value><![CDATA[MoleculeType]]></value>
  </property>
  <property key="details" >
    <value><![CDATA[Carbon dioxide]]></value>
  </property>
  <property key="isPrivate" >
    <value><![CDATA[false]]></value>
  </property>
  <property key="creatorId" >
    <value><![CDATA[SystemUser]]></value>
  </property>
  <property key="label" >
    <value><![CDATA[CO2]]></value>
  </property>
  <property key="lastEditDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
  <property key="tuples" >
    <value><![CDATA[d899b9b5-372b-4b5d-bd6a-8e236df2ca2d]]></value>
  </property>
  <property key="createdDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
</node>
<node locator="ClimateChange">
  <property key="subOf" >
    <value><![CDATA[AtmosphericEventType]]></value>
  </property>
  <property key="details" >
    <value><![CDATA[Major changes in climate]]></value>
  </property>
  <property key="isPrivate" >
    <value><![CDATA[false]]></value>
  </property>
  <property key="creatorId" >
    <value><![CDATA[SystemUser]]></value>
  </property>
  <property key="label" >
    <value><![CDATA[Climate Change]]></value>
  </property>
  <property key="lastEditDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
  <property key="tuples" >
    <value><![CDATA[d899b9b5-372b-4b5d-bd6a-8e236df2ca2d]]></value>
    <value><![CDATA[5d519e58-df49-41a0-8682-ac6b0d818326]]></value>
  </property>
  <property key="createdDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
</node>
<node locator="MethaneType">
  <property key="subOf" >
    <value><![CDATA[MoleculeType]]></value>
  </property>
  <property key="details" >
    <value><![CDATA[Methane]]></value>
  </property>
  <property key="isPrivate" >
    <value><![CDATA[false]]></value>
  </property>
  <property key="creatorId" >
    <value><![CDATA[SystemUser]]></value>
  </property>
  <property key="label" >
    <value><![CDATA[Methane]]></value>
  </property>
  <property key="lastEditDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
  <property key="tuples" >
    <value><![CDATA[5d519e58-df49-41a0-8682-ac6b0d818326]]></value>
  </property>
  <property key="createdDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
</node>
<node locator="AtmosphericEventType">
  <property key="subOf" >
    <value><![CDATA[EventType]]></value>
  </property>
  <property key="details" >
    <value><![CDATA[Events related to atmospheric conditions, weather, and climate]]></value>
  </property>
  <property key="isPrivate" >
    <value><![CDATA[false]]></value>
  </property>
  <property key="creatorId" >
    <value><![CDATA[SystemUser]]></value>
  </property>
  <property key="label" >
    <value><![CDATA[Atmospheric Event]]></value>
  </property>
  <property key="lastEditDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
  <property key="createdDate" >
    <value><![CDATA[2013-11-28 10:53:34]]></value>
  </property>
</node>
*/
