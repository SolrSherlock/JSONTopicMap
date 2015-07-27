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

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.Set;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.ITuple;
import org.topicquests.model.api.ITicket;
//import org.topicquests.topicmap.json.model.TopicMapXMLExporter.Worker;
import org.topicquests.topicmap.json.model.api.IExporterListener;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.util.LoggingPlatform;
//import org.json.simple.JSONObject;
import net.minidev.json.JSONObject;

/**
 * @author park
 * <p>THIS IS PROBLEMATIC:<br/>
 * <li>It has to build one huge JSONObject</li>
 * <li>That will not work because you cannot serialize the object
 * while you are building it</li>
 * </p>
 * <p>With JSONPullParsers, we can do this. It's just one giant
 * JSONArray, e.g. {"classes":[.....]}
 */
public class TopicMapJSONExporter {
	public LoggingPlatform log = LoggingPlatform.getLiveInstance();

	private IJSONTopicDataProvider database;
	private IExporterListener listener = null;
	private List<String> loopStopper = null;

	/**
	 * 
	 */
	public TopicMapJSONExporter(IJSONTopicDataProvider db) {
		database = db;
		loopStopper = new ArrayList<String>();
	}

	public void setListener(IExporterListener l) {
		listener = l;
	}
	
	public IResult exportJSONFile(String treeRootLocator, Writer out, ITicket credentials, boolean standAlone) {
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
		private boolean isFirst = true;
		
		public Worker(INode n, Writer out, ITicket credentials, int depth) {
			this.n = n;
			this.out = out;
			this.credentials = credentials;
			this.depth = depth;
			this.run();
		}
		
		public void run() {
			try {
				out.write("[ ");
				exportTree(n,out,credentials,depth);
				out.write(" ]");
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				//TODO should check for null string
				System.out.println(depth+" EXPORT- "+locator);
				try {
					if (!isFirst)
						out.write(" , ");
					else
						isFirst = false;
					((JSONObject)n).writeJSONString(out);
				} catch (Exception e) {
					//TODO
					result.addErrorString(e.getMessage());
				}
				//Now pick up children
				//What are children? subclasses, instances, IBIS nodes, etc...
				//Must pick up ITuples as well
				//we ignore any superclass or parent types; just start here and go down and out
				int start = 0, count = 50, fetched =0;
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
				List<String> tuples = n.listRelationsByRelationType(null);
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
				tuples = n.listRestrictedRelationsByRelationType(null);
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
				//pivots next
				tuples = n.listPivotsByRelationType(null);
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
				tuples = n.listRestrictedPivotsByRelationType(null);
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
				}//				System.out.println("EXPORT 4 "+tuples+" | "+result.getErrorString());		
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
