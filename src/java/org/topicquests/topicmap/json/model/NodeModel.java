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

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.Node;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.IMergeImplementation;
import org.topicquests.model.api.node.IAddressableInformationResource;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.node.ITuple;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.util.LoggingPlatform;

/**
 * @author park
 *
 */
public class NodeModel implements INodeModel {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private IMergeImplementation merger;
	private Stack<INode>nodeStack;
	private int maxNumNodes = 100;
	private StatisticsUtility stats;
	private ITicket credentials;

	/**
	 * 
	 */
	public NodeModel(JSONTopicmapEnvironment env, IJSONTopicDataProvider db, IMergeImplementation m, int stackSize) {
		environment = env;
		database = db;
		stats = environment.getStats();
		merger = m;
		nodeStack = new Stack<INode>();
		maxNumNodes = stackSize;
		credentials = getDefaultCredentials(ITopicQuestsOntology.SYSTEM_USER);
		if (merger != null)
			merger.setNodeModel(this);
	}

	/**
	 * Internal {@link INode} factory
	 * @return
	 */
	private INode getNode() {
//		return new Node();
		synchronized(nodeStack) {
			INode result = null;
			if (nodeStack.isEmpty())
				result = new Node();
			else
				result = nodeStack.pop();
			return result;
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public INode newNode(String locator, String label, String description,
			String lang, String userId, String smallImagePath,
			String largeImagePath, boolean isPrivate) {
		INode n = getNode();
		System.out.println("NodeModel.newNode- "+locator);
		//IResult result = new ResultPojo();
		//result.setResultObject(n);
		n.setLocator(locator);
		n.setCreatorId(userId);
		n.setVersion(Long.toString(System.currentTimeMillis())); //default
		Date d = new Date();
		n.setDate(d); 
		n.setLastEditDate(d);
		if (label != null)
			n.addLabel(label, lang, userId, false);
		
		if (smallImagePath != null)
			n.setSmallImage(smallImagePath);
		if (largeImagePath != null)
			n.setImage(largeImagePath);
		if (description != null)
			n.addDetails(description, lang, userId, false);
		n.setIsPrivate(isPrivate);
		stats.addTopicNode();
		System.out.println("NodeModel.newNode+ "+locator);		
		return n;	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public INode newNode(String label, String description, String lang,
			String userId, String smallImagePath, String largeImagePath,
			boolean isPrivate) {
		INode result = newNode(database.getUUID(),label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public INode newSubclassNode(String locator, String superclassLocator,
			String label, String description, String lang, String userId,
			String smallImagePath, String largeImagePath, boolean isPrivate) {
		INode result = newNode(locator,label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		result.addSuperclassId(superclassLocator);
		List<String>tc = _listTransitiveClosure(superclassLocator);
		result.setTransitiveClosure(tc);
		result.addTransitiveClosureLocator(superclassLocator);
		return result;
	}

	/**
	 * Returns transitive closure for a given node
	 * @param parentLocator
	 * @return does not return <code>null</code>
	 */
	private List<String> _listTransitiveClosure(String parentLocator) {
		IResult x = database.getNode(parentLocator, credentials);
		INode n = (INode)x.getResultObject();
		List<String>result = null;
		if (n != null)
			result  = n.listTransitiveClosure();
		if (result == null)
			result = new ArrayList<String>();
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public INode newSubclassNode(String superclassLocator, String label,
			String description, String lang, String userId,
			String smallImagePath, String largeImagePath, boolean isPrivate) {
		INode result = newNode(label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		result.addSuperclassId(superclassLocator);
		List<String>tc = _listTransitiveClosure(superclassLocator);
		result.setTransitiveClosure(tc);
		result.addTransitiveClosureLocator(superclassLocator);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public INode newInstanceNode(String locator, String typeLocator,
			String label, String description, String lang, String userId,
			String smallImagePath, String largeImagePath, boolean isPrivate) {
		INode result = newNode(locator,label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		result.setNodeType(typeLocator);
		List<String>tc = _listTransitiveClosure(typeLocator);
		result.setTransitiveClosure(tc);
		result.addTransitiveClosureLocator(typeLocator);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#newInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public INode newInstanceNode(String typeLocator, String label,
			String description, String lang, String userId,
			String smallImagePath, String largeImagePath, boolean isPrivate) {
		INode result = newNode(label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
		result.setNodeType(typeLocator);
		List<String>tc = _listTransitiveClosure(typeLocator);
		result.setTransitiveClosure(tc);
		result.addTransitiveClosureLocator(typeLocator);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#updateNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.util.Set)
	 */
	@Override
	public IResult updateNode(String nodeLocator, String updatedLabel,
			String updatedDetails, String language, String oldLabel,
			String oldDetails, String userId, boolean isLanguageAddition,
			ITicket  credentials) {
		IResult result = database.getNode(nodeLocator, credentials);
		if (result.getResultObject() != null) {
			INode n = (INode)result.getResultObject();
			if ((updatedLabel != null && !updatedLabel.equals("")) &&
				(oldLabel == null || oldLabel.equals("")))
				n.addLabel(updatedLabel, language, userId, isLanguageAddition);
			if ((updatedDetails != null && !updatedLabel.equals("")) && 
				(oldDetails == null || oldLabel.equals("")))
				n.addDetails(updatedDetails, language, userId, isLanguageAddition);
			List<String>val;
			String field;
			if (oldLabel != null) {
				field = n.makeField(ITopicQuestsOntology.LABEL_PROPERTY, language);
				val = (List<String>)n.getProperty(field);
				val.remove(oldLabel);
				val.add(updatedLabel);
				n.setProperty(field, val);
			}
			if (oldDetails != null) {
				field = n.makeField(ITopicQuestsOntology.DETAILS_PROPERTY, language);
				val = (List<String>)n.getProperty(field);
				val.remove(oldDetails);
				val.add(updatedDetails);
				n.setProperty(field, val);
			}
			return database.updateNode(n, true);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#changePropertyValue(org.topicquests.model.api.INode, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult changePropertyValue(INode node, String key, String newValue) {
		IResult result = null;
		Object o = node.getProperty(key);
		if (o instanceof List) {
			//not a real case so we do nothing here
			result = new ResultPojo();
		} else {
			String v = (String)o;
			if (!v.equals(newValue)) {
				node.setProperty(key, newValue);
				result = database.putNode(node, true);
				database.removeFromCache(node.getLocator());
			} else
				result = new ResultPojo();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#addPropertyValueInList(org.topicquests.model.api.INode, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addPropertyValueInList(INode node, String key,
			String newValue) {
		IResult result = null;
		String sourceNodeLocator = node.getLocator();
		Map<String,Object> myMap = node.getProperties();
		List<String>values = makeListIfNeeded( myMap.get(key));
		if (!values.contains(newValue)) {
			values.add(newValue);
			result = database.putNode(node, true);
			database.removeFromCache(sourceNodeLocator);
		} else
			result = new ResultPojo();
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#relateNodes(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public IResult relateNodes(String sourceNodeLocator,
			String targetNodeLocator, String relationTypeLocator,
			String userId, String smallImagePath, String largeImagePath,
			boolean isTransclude, boolean isPrivate) {
		IResult result = new ResultPojo();
		ITicket  credentials = getDefaultCredentials(userId);
		//fetch the source actor node
		IResult x = database.getNode(sourceNodeLocator, credentials);
		INode nxA = (INode)x.getResultObject();
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		if (nxA != null) {
			//fetch target actor node
			IResult y = database.getNode(targetNodeLocator,credentials);
			INode nxB = (INode)y.getResultObject();
			if (y.hasError())
				result.addErrorString(y.getErrorString());
			if (nxB != null) {
				y = relateExistingNodes(nxA, nxB, relationTypeLocator,userId,smallImagePath,largeImagePath,isTransclude,isPrivate);
				if (y.hasError())
					result.addErrorString(y.getErrorString());
				result.setResultObject(y.getResultObject());
			}
				
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#relateExistingNodes(org.topicquests.model.api.INode, org.topicquests.model.api.INode, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public IResult relateExistingNodes(INode sourceNode, INode targetNode,
			String relationTypeLocator, String userId, String smallImagePath,
			String largeImagePath, boolean isTransclude, boolean isPrivate) {
		IResult result = new ResultPojo();
		String signature = sourceNode.getLocator()+relationTypeLocator+targetNode.getLocator();
		ITuple t = (ITuple)this.newInstanceNode(relationTypeLocator, relationTypeLocator, 
				sourceNode.getLocator()+" "+relationTypeLocator+" "+targetNode.getLocator(), "en", userId, smallImagePath, largeImagePath, isPrivate);
		t.setIsTransclude(isTransclude);
		t.setObject(targetNode.getLocator());
		t.setObjectType(ITopicQuestsOntology.NODE_TYPE);
		t.setSubjectLocator(sourceNode.getLocator());
		t.setSubjectType(ITopicQuestsOntology.NODE_TYPE);
		t.setSignature(signature);
		String tLoc = t.getLocator();
		if (isPrivate) {
			sourceNode.addRestrictedTuple(tLoc);
			targetNode.addRestrictedTuple(tLoc);
		} else {
			sourceNode.addTuple(tLoc);
			targetNode.addTuple(tLoc);
		}
		IResult x = database.putNode(sourceNode, true);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		x = database.putNode(targetNode, true);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		database.putNode(t, true);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		environment.logDebug("NodeModel.relateNewNodes "+sourceNode.getLocator()+" "+targetNode.getLocator()+" "+t.getLocator()+" | "+result.getErrorString());
		result.setResultObject(tLoc);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#relateNewNodes(org.topicquests.model.api.INode, org.topicquests.model.api.INode, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public IResult relateNewNodes(INode sourceNode, INode targetNode,
			String relationTypeLocator, String userId, String smallImagePath,
			String largeImagePath, boolean isTransclude, boolean isPrivate) {
		IResult result = new ResultPojo();
		String signature = sourceNode.getLocator()+relationTypeLocator+targetNode.getLocator();
		ITuple t = (ITuple)this.newInstanceNode(relationTypeLocator, relationTypeLocator, 
				sourceNode.getLocator()+" "+relationTypeLocator+" "+targetNode.getLocator(), "en", userId, smallImagePath, largeImagePath, isPrivate);
		t.setIsTransclude(isTransclude);
		t.setObject(targetNode.getLocator());
		t.setObjectType(ITopicQuestsOntology.NODE_TYPE);
		t.setSubjectLocator(sourceNode.getLocator());
		t.setSubjectType(ITopicQuestsOntology.NODE_TYPE);
		t.setSignature(signature);
		String tLoc = t.getLocator();
		if (isPrivate) {
			sourceNode.addRestrictedTuple(tLoc);
			targetNode.addRestrictedTuple(tLoc);
		} else {
			sourceNode.addTuple(tLoc);
			targetNode.addTuple(tLoc);
		}
		IResult x = database.putNode(sourceNode, true);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		x = database.putNode(targetNode, true);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		database.putNode(t, true);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		environment.logDebug("NodeModel.relateNewNodes "+sourceNode.getLocator()+" "+targetNode.getLocator()+" "+t.getLocator()+" | "+result.getErrorString());
		result.setResultObject(tLoc);
		return result;	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#assertMerge(java.lang.String, java.lang.String, java.util.Map, double, java.lang.String)
	 */
	@Override
	public IResult assertMerge(String sourceNodeLocator,
			String targetNodeLocator, Map<String, Double> mergeData,
			double mergeConfidence, String userLocator) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#assertPossibleMerge(java.lang.String, java.lang.String, java.util.Map, double, java.lang.String)
	 */
	@Override
	public IResult assertPossibleMerge(String sourceNodeLocator,
			String targetNodeLocator, Map<String, Double> mergeData,
			double mergeConfidence, String userLocator) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#assertUnmerge(java.lang.String, org.topicquests.model.api.INode, java.util.Map, double, java.lang.String)
	 */
	@Override
	public IResult assertUnmerge(String sourceNodeLocator,
			INode targetNodeLocator, Map<String, Double> mergeData,
			double mergeConfidence, String userLocator) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#removeNode(java.lang.String)
	 */
	@Override
	public IResult removeNode(String locator) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeModel#getDefaultCredentials(java.lang.String)
	 */
	@Override
	public ITicket  getDefaultCredentials(String userId) {
		ITicket result = new TicketPojo (userId);
		return result;
	}

	
	/**
	 * 
	 * @param o
	 * @return can return <code>null</code>
	 */
	List<String> makeListIfNeeded(Object o) {
		if (o == null)
			return null;
		List<String>result = null;
		if (o instanceof List)
			result = (List<String>)o;
		else {
			result = new ArrayList<String>();
			result.add((String)o);
		}
		return result;
	}

	@Override
	public void recycleNode(INode node) {
//		node = null;
		database.removeFromCache(node.getLocator());
		synchronized(nodeStack) {
			if (nodeStack.size() >= this.maxNumNodes)
				node = null;
			else {
				node.getProperties().clear();
				nodeStack.push(node);
			}
		} 
		
	}
/**
	@Override
	public IAddressableInformationResource newAIR(String locator, String subject, String body, 
			String language, String userId, boolean isPrivate) {
		IAddressableInformationResource result = (IAddressableInformationResource)getNode();
		String lox = locator;
		if (lox == null)
			lox = database.getUUID();
		result.setLocator(lox);
		result.setNodeType(ITopicQuestsOntology.AIR_TYPE);
		List<String>tc = _listTransitiveClosure(ITopicQuestsOntology.AIR_TYPE);
		result.setTransitiveClosure(tc);
		result.addTransitiveClosureLocator(ITopicQuestsOntology.AIR_TYPE);
		result.setCreatorId(userId);
		result.setSubject(subject, language);
		result.setBody(body,language);
		Date d = new Date();
		result.setDate(d); 
		result.setLastEditDate(d);
		return result;
	}
*/
	@Override
	public IResult addSuperClass(INode node, String superClassLocator) {
		node.addSuperclassId(superClassLocator);
		IResult result = database.getNode(superClassLocator, credentials);
		INode s = (INode)result.getResultObject();
		result.setResultObject(null);
		List<String>stc = s.listTransitiveClosure();
		List<String>ntc = node.listTransitiveClosure();
		
		if (ntc == null)
			ntc = new ArrayList<String>();
		ntc.add(superClassLocator);
		if (stc != null && !stc.isEmpty()) {
			String x;
			for (int i=0;i<stc.size();i++) {
				x = stc.get(i);
				if (!ntc.contains(x))
					ntc.add(x);
			}
		}
		result = database.putNode(node, true);
		return result;
	}

	@Override
	public IResult setNodeType(INode node, String typeLocator) {
		//NOTE: if this node was already a type, it just got wiped out
		node.setNodeType(typeLocator);
		IResult result = database.getNode(typeLocator, credentials);
		INode s = (INode)result.getResultObject();
		result.setResultObject(null);
		List<String>stc = s.listTransitiveClosure();
		List<String>ntc = node.listTransitiveClosure();
		
		if (ntc == null)
			ntc = new ArrayList<String>();
		ntc.add(typeLocator);
		if (stc != null && !stc.isEmpty()) {
			String x;
			for (int i=0;i<stc.size();i++) {
				x = stc.get(i);
				if (!ntc.contains(x))
					ntc.add(x);
			}
		}
		result = database.putNode(node, true);
		return result;
	}

}
