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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

import org.nex.config.ConfigPullParser;
import org.topicquests.model.BiblioBootstrap;
import org.topicquests.model.CoreBootstrap;
import org.topicquests.model.RelationsBootstrap;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.IQueryModel;
import org.topicquests.model.api.ITicket;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.merge.SameLabelMergeHandler;
import org.topicquests.topicmap.json.merge.VirtualizerHandler;
import org.topicquests.topicmap.json.model.api.IExtendedConsoleDisplay;
import org.topicquests.topicmap.json.model.api.IExtendedEnvironment;
import org.topicquests.topicmap.json.model.api.IJSONDataProvider;
import org.topicquests.topicmap.json.model.api.ISocialBookmarkModel;
import org.topicquests.topicmap.json.model.api.IVirtualizer;
import org.topicquests.topicmap.json.persist.JSONDocStoreTopicMapProvider;
import org.topicquests.util.LoggingPlatform;
import org.topicquests.util.Tracer;
import org.topicquests.common.api.IConsoleDisplay;
import org.topicquests.common.api.ITopicQuestsOntology;
//import org.topicquests.topicmap.json.model.api.IExporterListener;
import org.topicquests.topicmap.json.mp.AMPQHandler;



/**
 * @author park
 *
 */
public class JSONTopicmapEnvironment 
	implements IConsoleDisplay, IExtendedEnvironment {
	public LoggingPlatform log = LoggingPlatform.getInstance("logger.properties");
	private JSONDocStoreEnvironment jsonEnvironment;
	private IJSONDocStoreModel jsonModel;
	private IJSONDataProvider dataProvider;
	private Map<String,Object>props;
	private TopicMapXMLExporter xmlExporter;
	private IQueryModel queryModel;
	private IExtendedConsoleDisplay console = null;
	private ISocialBookmarkModel bookmarkModel;
	private SameLabelMergeHandler sameLabelMerger;
	protected IVirtualizer virtualizer;
	private VirtualizerHandler virtualizerHandler;
	private TopicMapJSONExporter jsonExporter;
	private StatisticsUtility stats;
	private SearchEnvironment searcher;
	private AMPQHandler messenger;


	public SearchEnvironment getSearchEnvironment() {
		return searcher;
	}
	public void setConsole(IExtendedConsoleDisplay c) {
		console = c;
		//must wait till we have a console
		searcher = new SearchEnvironment(this);
	}
	
	/**
	 *
	 */
	public JSONTopicmapEnvironment(StatisticsUtility u) {
		stats = u;
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
		if (stats == null)
			stats = new StatisticsUtility();
		try {
			String siz = getStringProperty("MapCacheSize");
			System.out.println("XXXX "+siz);
			int cachesize = Integer.parseInt(siz);
			dataProvider = new JSONDocStoreTopicMapProvider(this, cachesize) ;
			siz = getStringProperty("VirtualizerClass");
			virtualizer = (IVirtualizer)Class.forName(siz).newInstance();
			virtualizer.init(this);
		} catch (Exception e) {
			log.logError(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		virtualizerHandler = new VirtualizerHandler(this);
		dataProvider.setVirtualizerHandler(virtualizerHandler);
		queryModel = new QueryModel(this);
		xmlExporter = new TopicMapXMLExporter(dataProvider);
		jsonExporter = new TopicMapJSONExporter(dataProvider);
		bookmarkModel = new SocialBookmarkModel(this);
		String bs = (String)props.get("ShouldBootstrap");
		sameLabelMerger = new SameLabelMergeHandler(this);
		messenger = new AMPQHandler(this);
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
	
	public IQueryModel getQueryModel() {
		return queryModel;
	}
	
	public VirtualizerHandler getVirtualizerHandler() {
		return virtualizerHandler;
	}
	
	public StatisticsUtility getStats() {
		return stats;
	}
	
	public IExtendedConsoleDisplay getConsoleDisplay() {
		return this.console;
	}
	/**
	 * Really only used internally
	 * @return
	 */
	public IVirtualizer getVirtualizer() {
		return virtualizer;
	}
	
	public SameLabelMergeHandler getSameLabelMergeHandler() {
		return sameLabelMerger;
	}
	
	public TopicMapXMLExporter getXMLExporter() {
		return xmlExporter;
	}
	
	@Override
	public IDataProvider getDataProvider() {
		return dataProvider;
	}
	
	public ISocialBookmarkModel getBookmarkModel() {
		return bookmarkModel;
	}

	public JSONDocStoreEnvironment getJSONEnvironment() {
		return jsonEnvironment;
	}
	
	public IJSONDocStoreModel getJSONModel() {
		return jsonModel;
	}
	
	public TopicMapJSONExporter getJSONExporter() {
		return this.jsonExporter;
	}
	
	@Override
	public void shutDown() {
		virtualizerHandler.shutDown();
		jsonEnvironment.shutDown();
		if (stats != null)
			stats.saveStats();
		((JSONDocStoreTopicMapProvider)this.dataProvider).shutDown();
	}
	
	@Override
	public Map<String,Object> getProperties() {
		return props;
	}
	
	@Override
	public String getStringProperty(String key) {
		return (String)props.get(key);
	}

	@Override
	public List<List<String>> getListProperty(String key) {
		return (List<List<String>>)props.get(key);
	}
	
	public void dumpDatabase() {
	ITicket credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
	TopicMapXMLExporter exporter = getXMLExporter();
	PrintWriter writer = null;
	console.toStatus("Exporting Index");
	try {
		File f = new File("TopicMapIndex_"+System.currentTimeMillis()+".xml");
		FileOutputStream fos = new FileOutputStream(f);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		writer = new PrintWriter(bos);
		writer.println("<topicmap>");
		exporter.setListener(new ExportListener(this,writer));
		exporter.exportXmlTreeFile(ITopicQuestsOntology.TYPE_TYPE, writer, credentials, true);
	} catch (Exception e) {
		e.printStackTrace();
		shutDown();
	}
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

class ExportListener implements org.topicquests.topicmap.json.model.api.IExporterListener {
	JSONTopicmapEnvironment host;
	java.io.PrintWriter writer;
	
	public ExportListener(JSONTopicmapEnvironment env, java.io.PrintWriter w) {
		host = env;
		writer = w;
	}
		@Override
		public void exportDone() {
			
			writer.println("</topicmap>");
			try {
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			host.toStatus("Index Exported");
		}
}
