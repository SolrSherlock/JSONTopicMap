/**
 * 
 */
package org.topicquests.topicmap.json.merge;

import java.util.*;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.ICoreIcons;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
//import org.topicquests.model.api.IDataProvider;
//import org.topicquests.model.api.IEnvironment;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.ITicket;
import org.topicquests.model.api.node.ITuple;
import org.topicquests.model.api.query.ITupleQuery;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 * <p>SetUnion functions for all VirtualNode creation
 */
public class BaseVirtualizer {
	protected JSONTopicmapEnvironment environment;
	protected IJSONTopicDataProvider database;
	protected INodeModel nodeModel;
	protected ITupleQuery tupleQuery;
	

	public void init(JSONTopicmapEnvironment env) {
		environment = (JSONTopicmapEnvironment)env;
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		nodeModel = database.getNodeModel();
		tupleQuery = database.getTupleQuery();
	}

	/**
	 * <p>Perform a <em>Set Union</em> on various key/value pairs</p>
	 * <p>This is ONLY appropriate for creation of a virtual proxy</p>
	 * @param virtualNode
	 * @param mergedNode
	 * @param isTuple
	 * @return
	 */
	protected boolean setUnionProperties(INode virtualNode, INode mergedNode) {
		//if any surgery is performed, result = true
		boolean result = false;
				//Copy over all labels and details for both nodes
			//	installLabelsAndDetails(virtualNode,mergedNode);
		//grab unrestricted tuples first
		environment.logDebug("BaseVirtualizer.setUnionProperties- "+virtualNode.getLocator()+" "+mergedNode.getLocator());
		//other properties -- doing surgery on the map
		//note: this will pick up all the labels and details in all languages
		Map<String,Object>sourceMap = mergedNode.getProperties();
		Map<String,Object>virtMap = virtualNode.getProperties();
		Iterator<String>keys = sourceMap.keySet().iterator();
		String key;
		Object os;
		Object ov;
		List<String>sxx;
		List<String>vxx;
		while (keys.hasNext()) {
			key = keys.next();
			environment.logDebug("BaseVirtualizer.setUnionProperties-1 "+key);
			if (okToUse(key)) {
				os = sourceMap.get(key);
				ov = virtMap.get(key);
				environment.logDebug("BaseVirtualizer.setUnionProperties-2 "+key+" "+ov+" "+os);
				
					if (os instanceof String ||
						os instanceof Date ||
						os instanceof Long ||
						os instanceof Double ||
						os instanceof Integer ||
						os instanceof Float ||
						os instanceof Boolean) {
						///////////////////////////////////////
						//This may be where we are creating dates as strings
						//and messing things up
						///////////////////////////////////////
						if (ov == null && !(key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY) ||
										    key.equals(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY))) {
							virtMap.put(key, os);
							result = true;
						} else if (ov instanceof String && os instanceof String) {
							//ov and os are strings: same key; make a list
							if (!ov.equals(os)) {
								vxx = new ArrayList<String>();
								vxx.add((String)ov);
								vxx.add((String)os);
								virtMap.put(key, vxx);
								environment.logDebug("BaseVirtualizer.setUnionProperties-3 "+key+" "+vxx);
							}
						} else if (ov instanceof List) {
							vxx = (List<String>)ov;
							if (!vxx.isEmpty() && !vxx.contains((String)os)) {
								vxx.add((String)os);
								virtMap.put(key, vxx);
								environment.logDebug("BaseVirtualizer.setUnionProperties-4 "+key+" "+vxx+" "+virtMap);
							}
						} else {
							environment.logDebug("WIERD "+key+" "+ov+" | "+os);
						}
					} else { //os must be a list
						sxx = (List<String>)os;
						environment.logDebug("BaseVirtualizer.setUnionProperties-5 "+key+" "+sxx);
						if (ov == null)
							vxx = new ArrayList<String>();
						else //TODO TEST FOR LIST OR STRING  XXXXX
							vxx = (List<String>)ov;
						int len = sxx.size();
						for (int i=0;i<len;i++) {
							if (!vxx.contains(sxx.get(i))) {
								vxx.add(sxx.get(i));
								result = true;
							}
						}
						if (!vxx.isEmpty()) {
							virtMap.put(key, vxx);
							environment.logDebug("BaseVirtualizer.setUnionProperties-5a "+key+" "+vxx);
						}
					}
			}
		}
		//update last edit
		if (result)
			virtMap.put(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY, new Date());
		return result;
	}
	
	/**
	 * Filter out certain keys
	 * @param key
	 * @return
	 */
	boolean okToUse(String key) {
		if (key.equals(ITopicQuestsOntology.LOCATOR_PROPERTY) ||
			key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY) ||
			key.equals(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY) ||
			key.equals(ITopicQuestsOntology.CREATOR_ID_PROPERTY) ||
			key.equals(ITopicQuestsOntology.IS_PRIVATE_PROPERTY))
			return false;
		return true;
	}
	/**
	 * Adding to a singleValued field
	 * @param key
	 * @return false if not ok to add
	 */
	boolean okToAdd(String key) {
		if (key.equals(ITopicQuestsOntology.LOCATOR_PROPERTY) ||
			key.equals(ITopicQuestsOntology.CREATED_DATE_PROPERTY) ||
			key.equals(ITopicQuestsOntology.LAST_EDIT_DATE_PROPERTY) ||
			key.equals(ITopicQuestsOntology.CREATOR_ID_PROPERTY) ||
			key.equals(ITopicQuestsOntology.PSI_PROPERTY_TYPE) ||
			key.equals(ITopicQuestsOntology.RESOURCE_URL_PROPERTY) ||
			key.equals(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE) ||
			key.equals(ITopicQuestsOntology.IS_PRIVATE_PROPERTY))
			return false;
		return true;
	}
	
	/**
	 * Wire up a MergeAssertion
	 * @param virtualProxy
	 * @param targetProxy
	 * @param mergeData key = reason, value = vote
	 * @param mergeConfidence not used at the moment
	 * @param userLocator
	 * @return tupleLocator is included
	 */
	IResult wireMerge(INode virtualProxy, INode targetProxy, Map<String, Double> mergeData,
			double mergeConfidence, String userLocator) {
		//force relation engine to re-fetch the nodes for accurate surgery
		//changed my mind
		//TODO rethink this algorithm; it's a lot of node fetches
		IResult result = relateNodes(virtualProxy, targetProxy, 
				 userLocator, ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, 
				 false, targetProxy.getIsPrivate(), mergeData);
		// result contains the tuple's locator
		// use that to fetch it and wire up scopes from mergeData
		return result;
	}
	
	/**
	 * Forge a merge relation--not using the version in INodeModel
	 * @param virtualNode
	 * @param targetNode
	 * @param userId
	 * @param smallImagePath
	 * @param largeImagePath
	 * @param isTransclude
	 * @param isPrivate
	 * @param mergeData
	 * @return locator of the created tuple
	 */
	private IResult relateNodes(INode virtualNode, INode targetNode,
			String userId, String smallImagePath,
			String largeImagePath, boolean isTransclude, boolean isPrivate, Map<String, Double> mergeData) {
		String relationTypeLocator = ITopicQuestsOntology.MERGE_ASSERTION_TYPE;
		database.removeFromCache(virtualNode.getLocator());
		database.removeFromCache(targetNode.getLocator());
		IResult result = new ResultPojo();
		String signature = virtualNode.getLocator()+ITopicQuestsOntology.MERGE_ASSERTION_TYPE+targetNode.getLocator();
		//NOTE that we make the tuple an instance of the relation type, not of TUPLE_TYPE
		ITuple t = (ITuple)nodeModel.newInstanceNode(signature, relationTypeLocator, 
				virtualNode.getLocator()+" "+relationTypeLocator+" "+targetNode.getLocator(), "en", userId, smallImagePath, largeImagePath, isPrivate);
		t.setIsTransclude(isTransclude);
		//NOTE: tuple object is always merged node
		t.setObject(targetNode.getLocator());
		t.setObjectType(ITopicQuestsOntology.NODE_TYPE);
		//NOTE: tuple subject is always virtualProxy
		t.setSubjectLocator(virtualNode.getLocator());
		t.setSubjectType(ITopicQuestsOntology.NODE_TYPE);
		Iterator<String>itx = mergeData.keySet().iterator();
		String reason;
		while (itx.hasNext()) {
			reason = itx.next();
			t.addMergeReason(reason+" "+mergeData.get(reason));
		}
		IResult x = database.putNode(t, true);
		environment.logDebug("MergeBean.relateNodes "+virtualNode.getLocator()+" "+targetNode.getLocator()+" "+t.getLocator());
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		String tLoc = t.getLocator();
		//save the tuple's locator in the output
		result.setResultObject(tLoc);
		addPropertyValue(virtualNode,ITopicQuestsOntology.MERGE_TUPLE_PROPERTY,tLoc);
		//add the value to the node after it's been surgically added
		virtualNode.addMergeTupleLocator(tLoc);
		targetNode.addMergeTupleLocator(tLoc);
		changePropertyValue(targetNode,ITopicQuestsOntology.MERGE_TUPLE_PROPERTY,tLoc);
		environment.logDebug("MergeBean.relateNodes+ "+result.getErrorString());
		return result;
	}
	
	/**
	 * Surgically change a property value (NOT a list value)
	 * @param node
	 * @param key
	 * @param newValue
	 * @return
	 */
	void changePropertyValue(INode node, String key, String newValue) {
		environment.logDebug("MergeBean.changePropertyValue- "+node.getLocator()+" "+key+" "+newValue);
		Map<String,Object>p =node.getProperties();
		p.put(key, newValue);
		database.removeFromCache(node.getLocator());
	}
	
	void addPropertyValue(INode node, String key, String newValue) {
		environment.logDebug("MergeBean.addPropertyValue- "+node.getLocator()+" "+key+" "+newValue);
		String sourceNodeLocator = node.getLocator();
		Map<String,Object> propMap = node.getProperties();
		Object ox = propMap.get(key);
		if (ox instanceof String)
			propMap.put(key, newValue);
		else {
			List<String> l = (List<String>)ox;
			l.add(newValue);
		}
		database.removeFromCache(node.getLocator());
	}
	
	/////////////////////////////////////////////////////////////////
	// Rewiring the graph
	// CASE: Merged nodes
	// 	A Node was merged with another node.
	// 	A VirtualNode was created
	// 	For every place in the graph where either node is referenced
	//		surgically replace that node locator with the virtual node locator
	// CASE: Merged tuples
	//  A Tuple has merged with another tuple
	//		NOTE: these are NOT Merge-related tuples, only knowledge graph tuples
	//  For every node which references a merged tuple in its tuple field
	//    	surgically replace that tuple locator with the new tuple locator
	//        	this entails overwriting a tuple *in place* in a list of tuples
	//          and doing an update on that list in the database
	/////////////////////////////////////////////////////////////////
	// Emergent Issue
	//	If a new node is saved and it is immediately related to another node
	//		it will be in a merge process while the relation is occuring.
	//		THIS means that there MIGHT be NO TUPLE available to be captured
	//		during the merge. Thus, the VirtualNode MIGHT end up with no tuple
	//////////////////////////////////////////////////////////////////
	/**
	 * Substitute <code>virtualProxyLocator</code> for all hits of <code>mergedProxyLocator</code>
	 * @param mergedProxyLocator
	 * @param virtualProxyLocator
	 * @return
	 */
	IResult reWireNodeGraph(String mergedProxyLocator, String virtualProxyLocator, String mergeTupleLocator, ITicket credentials) {
		//this really must deal with tuples first
		//TODO sort out what other propertyTypes entail symbolic links, then
		//chase those
		environment.logDebug("MergeBean.reWireNodeGraph- "+mergedProxyLocator+" "+virtualProxyLocator+" "+mergeTupleLocator);
		IResult result = new ResultPojo();
		//Find all tuples where mergedProxyLocator isA subject and fix them
		IResult xx = tupleQuery.listTuplesBySubject(mergedProxyLocator, 0,-1,credentials);
		if (xx.hasError())
			result.addErrorString(xx.getErrorString());
		List<INode>n = (List<INode>)xx.getResultObject();
		ITuple t;
		Iterator<INode>itr;
		IResult surgR;
		if (n != null && !n.isEmpty()) {
			//time for surgery
			itr = n.iterator();
			while (itr.hasNext()) {
				t = (ITuple)itr.next();
				if (!t.getLocator().equals(mergeTupleLocator)) {
					environment.logDebug("MergeBean.reWireGraph-1 "+t.getLocator());
					surgR = performTupleSurgery(t,virtualProxyLocator,true);
					if (surgR.hasError())
						result.addErrorString(surgR.getErrorString());
					//notice the distinct possibility that no surgery got performed
					//and the topic map will have errors
				}
			}
		}
		//Find all tuples where mergedProxyLocator isA object and fix them
		xx = tupleQuery.listTuplesByObjectLocator(mergedProxyLocator, 0,-1, credentials);
		if (xx.hasError())
			result.addErrorString(xx.getErrorString());
		n = (List<INode>)xx.getResultObject();
		if (n != null && !n.isEmpty()) {
			//time for surgery
			itr = n.iterator();
			while (itr.hasNext()) {
				t = (ITuple)itr.next();
				if (!t.getLocator().equals(mergeTupleLocator)) {
					environment.logDebug("MergeBean.reWireGraph-2 "+t.getLocator());
					surgR = performTupleSurgery(t,virtualProxyLocator,false);
					if (surgR.hasError())
						result.addErrorString(surgR.getErrorString());
					//notice the distinct possibility that no surgery got performed
					//and the topic map will have errors
				}
			}
		}
		return result;
	}

	/**
	 * <p>This is supposed to perform surgery on {@link ITuple} objects only.</p>
	 * @param t
	 * @param newLocator
	 * @param isSubject
	 * @return
	 */
	IResult performTupleSurgery(ITuple t, String newLocator, boolean isSubject) {
		String key = ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY;
		if (!isSubject)
			key = ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY;
		//We are performing surgery on a tuple which might have already had
		//surgery earlier, which means the version number will be out of date.
		//Two options: remove the version number, or refetch the tuple before
		// doing this surgery
		///////////////////////////////
		//TODO: must pay attention to changes in date fields
		///////////////////////////////
		environment.logDebug("MergeBean.performTupleSergery "+t.getLocator()+" "+newLocator);
		IResult result = nodeModel.changePropertyValue(t, key, newLocator);
		
		return result;
	}
	
}
