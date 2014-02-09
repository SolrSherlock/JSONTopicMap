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
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.TopicMapJSONExporter;
import org.topicquests.topicmap.json.model.TopicMapXMLExporter;
import org.topicquests.topicmap.json.model.api.IExporterListener;

/**
 * @author park
 *
 */
public class ExportTest implements IExporterListener {
	private JSONTopicmapEnvironment environment;
	private PrintWriter writer;
	private ITicket credentials;
	/**
	 * 
	 */
	public ExportTest() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		//create credentials
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
		//TopicMapXMLExporter exporter = environment.getXMLExporter();
		TopicMapJSONExporter exporter = environment.getJSONExporter();
		exporter.setListener(this);
		try {
			File f = new File("ExportTest"+System.currentTimeMillis()+".xml");
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			writer = new PrintWriter(bos);
			writer.println("<topicmap>");
			exporter.exportJSONFile(ITopicQuestsOntology.TYPE_TYPE, writer, credentials, true);
		} catch (Exception e) {
			e.printStackTrace();
			environment.shutDown();
		}
		
	}
	@Override
	public void exportDone() {
		environment.shutDown();
		writer.println("</topicmap>");
		try {
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
