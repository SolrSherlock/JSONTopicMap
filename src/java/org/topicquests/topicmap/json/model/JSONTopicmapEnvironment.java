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
package org.topicquests.topicmap.json.model;

import java.util.*;

import org.nex.config.ConfigPullParser;
import org.topicquests.model.BiblioBootstrap;
import org.topicquests.model.CoreBootstrap;
import org.topicquests.model.RelationsBootstrap;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.IQueryModel;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.persist.JSONDocStoreTopicMapProvider;
import org.topicquests.util.LoggingPlatform;
import org.topicquests.util.Tracer;
import org.topicquests.common.api.IConsoleDisplay;


/**
 * @author park
 *
 */
public class JSONTopicmapEnvironment implements IConsoleDisplay {
	public LoggingPlatform log = LoggingPlatform.getInstance("logger.properties");
	private JSONDocStoreEnvironment jsonEnvironment;
	private IJSONDocStoreModel jsonModel;
	private IDataProvider dataProvider;
	private Map<String,Object>props;
	private TopicMapXMLExporter xmlExporter;
	public IQueryModel model;
	private IConsoleDisplay console = null;


	public void setConsole(IConsoleDisplay c) {
		console = c;
	}
	/**
	 *
	 */
	public JSONTopicmapEnvironment() {
		ConfigPullParser p = new ConfigPullParser("topicmap-props.xml");
		props = p.getProperties();
		jsonEnvironment = new JSONDocStoreEnvironment();
		jsonModel = jsonEnvironment.getModel();
		init();
	}

	/**
	 * Any system which uses this API must have defined an index thus:
	 * <pre>
	 * 		<parameter name="TopicIndex" value = "topics" />
	 * </pre>
	 * @param env
	 */
	public JSONTopicmapEnvironment(JSONDocStoreEnvironment env) {
	//	super(false);
		jsonEnvironment = env;
		jsonModel = jsonEnvironment.getModel();
		ConfigPullParser p = new ConfigPullParser("topicmap-props.xml");
		props = p.getProperties();
		init();
	}
	
	void init() {
		try {
			String siz = getStringProperty("MapCacheSize");
			System.out.println("XXXX "+siz);
			int cachesize = Integer.parseInt(siz);
			dataProvider = new JSONDocStoreTopicMapProvider(this, cachesize) ;
		} catch (Exception e) {
			log.logError(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		xmlExporter = new TopicMapXMLExporter(dataProvider);
		String bs = (String)props.get("ShouldBootstrap");
		boolean shouldBootstrap = false; // default value
		if (bs != null)
			shouldBootstrap = bs.equalsIgnoreCase("Yes");
		if (shouldBootstrap)
			bootstrap();
	}
	/**
	 * Available to extensions if needed
	 */
	public void bootstrap() {
		CoreBootstrap cbs = new CoreBootstrap(dataProvider);
		cbs.bootstrap();
		BiblioBootstrap bbs = new BiblioBootstrap(dataProvider);
		bbs.bootstrap();
		RelationsBootstrap rbs = new RelationsBootstrap(dataProvider);
		rbs.bootstrap();
	}
	
	public TopicMapXMLExporter getXMLExporter() {
		return xmlExporter;
	}
	
	public IDataProvider getTopicDataProvider() {
		return dataProvider;
	}

	public JSONDocStoreEnvironment getJSONEnvironment() {
		return jsonEnvironment;
	}
	
	public IJSONDocStoreModel getJSONModel() {
		return jsonModel;
	}
	
	public void shutDown() {
		jsonEnvironment.shutDown();
		((JSONDocStoreTopicMapProvider)this.dataProvider).shutDown();
	}
	
	public Map<String,Object> getProperties() {
		return props;
	}
	
	public String getStringProperty(String key) {
		return (String)props.get(key);
	}
	
	/////////////////////////////
	// Utilities
	public void logDebug(String msg) {
		log.logDebug(msg);
	}
	
	public void logError(String msg, Exception e) {
		log.logError(msg,e);
	}
	
	public void record(String msg) {
		log.record(msg);
	}

	public Tracer getTracer(String name) {
		return log.getTracer(name);
	}
	
	////////////////////////////////
	//Console, valid if booted from a console
	////////////////////////////////
	@Override
	public void toConsole(String text) {
		if (console != null)
			console.toConsole(text);
	}
	@Override
	public void toStatus(String text) {
		if (console != null)
			console.toStatus(text);
	}

}
