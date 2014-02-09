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
import java.io.*;
import java.util.logging.Logger;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.ITuple;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.api.IExporterListener;
import org.topicquests.util.LoggingPlatform;

/**
 * @author park
 *
 */
public class TopicMapXMLExporter {
	public LoggingPlatform log = LoggingPlatform.getLiveInstance();

	private IDataProvider database;
	private IExporterListener listener = null;
	private List<String> loopStopper = null;

	/**
	 * 
	 */
	public TopicMapXMLExporter(IDataProvider db) {
		database = db;
		loopStopper = new ArrayList<String>();
	}
	
	public void setListener(IExporterListener l) {
		listener = l;
	}
	/**
	 * <p>Export a tree root and it's entire subtree.</p>
	 * <p>This entails finding all {@link INode} and {@link ITuple} instances
	 * related to <code>treeRootLocator</code> and recursing on them as well.</p>
	 * @param treeRootLocator
	 * @param out
	 * @param standAlone <code>true</code> if this is the only method called
	 * @param credentials
	 * @return
	 */
	public IResult exportXmlTreeFile(String treeRootLocator, Writer out, ITicket  credentials, boolean standAlone) {
		if (standAlone)
			loopStopper = new ArrayList<String>();
		IResult result = new ResultPojo();
		INode n = (INode)database.getNode(treeRootLocator, credentials).getResultObject();
		System.out.println("TopicMapXMLExporter.exportXmlTreeFile- "+n);
		if (n != null) {
			new Worker(n,out,credentials,0);
			System.out.println("TopicMapXMLExporter.exportXmlTreeFile+");
		}
		return result;
	}
	
	class Worker extends Thread {
		private INode n;
		private Writer out;
		private ITicket  credentials;
		private int depth;
		private Object waiter = new Object();
		
		public Worker(INode n, Writer out, ITicket credentials, int depth) {
			this.n = n;
			this.out = out;
			this.credentials = credentials;
			this.depth = depth;
			this.run();
		}
		
		public void run() {
			exportTree(n,out,credentials,depth);
			if (listener != null)
				listener.exportDone();
		}
		
		/**
		 * Recursive tree descent; export <code>n</code> and all of its child nodes
		 * @param n
		 * @param out
		 * @param credentials
		 * @param depth // for diagnostics
		 * @return
		 */
		private IResult exportTree(INode n, Writer out, ITicket credentials, int depth) {
	/*		synchronized(waiter) {
				try {
					waiter.wait(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} */
			Thread.yield();
			String locator = n.getLocator();
			IResult result = new ResultPojo();
			if (!loopStopper.contains(locator)) {
				int mydepth = depth++;
				loopStopper.add(locator);
				//Export this node
				String xml = n.toXML();
				//TODO should check for null string
				System.out.println(depth+" EXPORT- "+locator+" | "+xml);
				try {
					out.write(xml);
				} catch (Exception e) {
					//TODO
					result.addErrorString(e.getMessage());
				}
				//Now pick up children
				//What are children? subclasses, instances, IBIS nodes, etc...
				//Must pick up ITuples as well
				//we ignore any superclass or parent types; just start here and go down and out
				int start = 0, count = 50, fetched =50;
				//instances first
				INode theNode = null; 
				INode tNode = null;
				IResult xx = listInstanceNodes(locator,start,count,credentials);
				Iterator<INode>nitr = null;
				List<INode>nodes = (List<INode>)xx.getResultObject();
//				if (locator.equals("f7b7084f-442b-47b8-b925-95a678db62d5Cluster"))
//					log.debug("EXXXXXP: "+nodes);
				System.out.println("EXPORT 0 "+nodes+" | "+result.getErrorString());
				while (nodes != null && nodes.size() > 0) {
					fetched = nodes.size();
					nitr = nodes.iterator();
					while (nitr.hasNext())
						exportTree(nitr.next(),out,credentials,mydepth);
					start += fetched;
					count += 50;
					xx = listInstanceNodes(locator,start,count,credentials);
					nodes =(List<INode>)xx.getResultObject();
				}
				//subclasses next
				start = 0; count = 50;
				xx = listSubclassNodes(locator,start,count,credentials);
				nodes = (List<INode>)xx.getResultObject();
				System.out.println(depth+" EXPORT 1 "+locator+" "+nodes+" | "+result.getErrorString());
				
				while (nodes != null && nodes.size() > 0) {
					fetched = nodes.size();
					nitr = nodes.iterator();
					while (nitr.hasNext())
						exportTree(nitr.next(),out,credentials,mydepth);
					start += fetched;
					count += 50;
					xx = listSubclassNodes(locator,start,count,credentials);
					nodes = (List<INode>)xx.getResultObject();
				}
				//tuples next
				List<String> tuples = n.listTuples();
				String tox;
//				System.out.println("EXPORT 2 "+tuples+" | "+result.getErrorString());
				if (tuples != null && tuples.size() > 0) {
					Iterator<String>itr = tuples.iterator();
					while (itr.hasNext()) {
						tox = itr.next();
						//TODO should check for null
						tNode = (INode)database.getNode(tox, credentials).getResultObject();
						if (tNode != null) {
							exportTree(tNode,out,credentials,mydepth);
							//Now, take apart source and target nodes in case we haven't plucked them yet
							tox = ((ITuple)tNode).getSubjectLocator();
							theNode = (INode)database.getNode(tox, credentials).getResultObject();
							if (theNode != null)
								exportTree(theNode,out,credentials,mydepth);
							tox = ((ITuple)tNode).getObject();
							if (((ITuple)tNode).getObjectType().equals(ITopicQuestsOntology.NODE_TYPE) ||
							   ((ITuple)tNode).getObjectType().equals(ITopicQuestsOntology.VIRTUAL_NODE_TYPE)) {
								theNode = (INode)database.getNode(tox, credentials).getResultObject();
								if (theNode != null) 
									exportTree(theNode,out,credentials,mydepth);
							}
						} else
							result.addErrorString("SolrExporter.exportTree missing tuple "+tox);
					}
				}
//				System.out.println("EXPORT 3 "+tuples+" | "+result.getErrorString());
				tuples = n.listRestrictedTuples();
				if (tuples != null && tuples.size() > 0) {
					Iterator<String>itr = tuples.iterator();
					while (itr.hasNext()) {
						tox = itr.next();
						//TODO should check for null
						tNode = (INode)database.getNode(tox, credentials).getResultObject();
						if (tNode != null) {
							exportTree(tNode,out,credentials,mydepth);
							//Now, take apart source and target nodes in case we haven't plucked them yet
							tox = ((ITuple)tNode).getSubjectLocator();
							theNode = (INode)database.getNode(tox, credentials).getResultObject();
							if (theNode != null)
								exportTree(theNode,out,credentials,mydepth);
							tox = ((ITuple)tNode).getObject();
							if (((ITuple)tNode).getObjectType().equals(ITopicQuestsOntology.NODE_TYPE) ||
									   ((ITuple)tNode).getObjectType().equals(ITopicQuestsOntology.VIRTUAL_NODE_TYPE)) {
								theNode = (INode)database.getNode(tox, credentials).getResultObject();
								if (theNode != null) 
									exportTree(theNode,out,credentials,mydepth);
							}
						} else
							result.addErrorString("SolrExporter.exportTree missing tuple "+tox);
					}
				}
//				System.out.println("EXPORT 4 "+tuples+" | "+result.getErrorString());		
				System.out.println(depth+" EXPORT+ "+locator);
			}
			return result;
		}
	}
	

	
	private IResult listInstanceNodes(String locator, int start, int count, ITicket credentials) {
		System.out.println("LISTINSTANCENODES "+locator);
		return database.listInstanceNodes(locator, start, count, credentials);
	}
	private IResult listSubclassNodes(String locator, int start, int count, ITicket credentials) {
		return database.listSubclassNodes(locator, start, count, credentials);
	}
}
