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
package org.topicquests.topicmap.json.persist;

import java.io.Writer;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.nex.util.LRUCache;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IMergeRuleMethod;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.Environment;
import org.topicquests.model.Node;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.IMergeImplementation;
import org.topicquests.model.api.INode;
import org.topicquests.model.api.INodeModel;
import org.topicquests.model.api.INodeQuery;
import org.topicquests.model.api.ITuple;
import org.topicquests.model.api.ITupleQuery;
import org.topicquests.model.api.IXMLFields;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.merge.MergeInterceptor;
import org.topicquests.topicmap.json.model.CredentialUtility;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.NodeQuery;
import org.topicquests.topicmap.json.model.TopicMapXMLExporter;
import org.topicquests.topicmap.json.model.TupleQuery;
import org.topicquests.topicmap.json.model.api.IJSONTopicMapOntology;
import org.topicquests.model.NodeModel;

/**
 * @author park
 *
 */
public class JSONDocStoreTopicMapProvider implements IDataProvider {
	private JSONTopicmapEnvironment environment;
	private JSONDocStoreEnvironment jsonEnvironment;
	private IJSONDocStoreModel jsonModel;
	private IMergeImplementation merger = null;//not used here
	private INodeModel _model;
	private ITupleQuery tupleQuery;
	private TopicMapXMLExporter exporter;
	private JSONParser parser;
	private MergeInterceptor interceptor;
	private CredentialUtility credentialUtil;

	/** We only save public nodes in this cache */
	private LRUCache nodeCache;

	private final String
			//defined in jsonblobstore-props.xml
			TOPIC_INDEX		= IJSONTopicMapOntology.TOPIC_INDEX,
			CORE_TYPE		= IJSONTopicMapOntology.CORE_TYPE;
	
	/**
	 * Constructor
	 * @param env
	 * @param cachesize
	 */
	public JSONDocStoreTopicMapProvider(JSONTopicmapEnvironment env, int cachesize) throws Exception {
		environment = env;
		jsonEnvironment = environment.getJSONEnvironment();
		jsonModel =  jsonEnvironment.getModel();
		nodeCache = new LRUCache(cachesize);
		//maybe not sending in a merge engine
		//The theory being that an external merge engine
		// will be at work
		_model = new NodeModel(this,null);
		tupleQuery = new TupleQuery(this, jsonModel);
		exporter = new TopicMapXMLExporter(this);
		parser = new JSONParser();
		interceptor = new MergeInterceptor();
		credentialUtil = new CredentialUtility(this,jsonModel);
		environment.logDebug("JSONDocStoreTopicMapProvider.init "+environment+" "+jsonEnvironment+" "+jsonModel);		
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#setMergeBean(org.topicquests.model.api.IMergeImplementation)
	 */
	@Override
	public void setMergeBean(IMergeImplementation merger) {
		this.merger = merger;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#exportXmlFile(java.io.Writer, java.util.Set)
	 */
	@Override
	public IResult exportXmlFile(Writer out, Set<String> credentials) {
		throw new RuntimeException("Use exportXmlTreeFile instead");
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#exportXmlTreeFile(java.lang.String, java.io.Writer, java.util.Set)
	 */
	@Override
	public IResult exportXmlTreeFile(String treeRootLocator, Writer out,
			Set<String> credentials) {
		IResult result = null;
		try {
			out.write("<"+IXMLFields.DATABASE+">\n");
			result =  exporter.exportXmlTreeFile(treeRootLocator, out, credentials, true);
			out.write("</"+IXMLFields.DATABASE+">\n");
			out.flush();
			out.close();
		} catch (Exception e) {
			result.addErrorString(e.getMessage());
			environment.logError(e.getMessage(),e);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#getNode(java.lang.String, java.util.Set)
	 */
	@Override
	public IResult getNode(String locator, Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.getNode- "+locator);
		//getDocument(String index, String type, String documentId)
		Object nx = nodeCache.get(locator);
		IResult result = null;
		if (nx != null) {
			result = new ResultPojo();
			result.setResultObject(nx);
		} else {
			result = jsonModel.getDocument(TOPIC_INDEX, CORE_TYPE, locator);
			if (result.getResultObject() != null) {
				try {
					JSONObject jo = (JSONObject)parser.parse((String)result.getResultObject());
					if (credentialUtil.checkCredentials(jo, credentials)) {
						INode n = new Node(jo);
						result.setResultObject(n);
						nodeCache.add(locator, n);
					}
				} catch (Exception e) {
					environment.logError(e.getMessage(), e);
					result.addErrorString(e.getMessage());
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#getNodeView(java.lang.String, java.util.Set)
	 */
	@Override
	public IResult getNodeView(String locator, Set<String> credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#removeNode(java.lang.String)
	 */
	@Override
	public IResult removeNode(String locator) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#putNode(org.topicquests.model.api.INode)
	 */
	@Override
	public IResult putNode(INode node) {
		environment.logDebug("JSONDocStoreTopicMapProvider.putNode- "+node);
		nodeCache.remove(node.getLocator());
		//putDocument(String id, String index, String type, String jsonString);
		IResult result = jsonModel.putDocument(node.getLocator(), TOPIC_INDEX, 
					CORE_TYPE, node.toJSON());
		interceptor.acceptNodeForMerge(node);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#putNodeNoMerge(org.topicquests.model.api.INode)
	 */
	@Override
	public IResult putNodeNoMerge(INode node) {
		environment.logDebug("JSONDocStoreTopicMapProvider.putNodeNoMerge- "+node);
		nodeCache.remove(node.getLocator());
		IResult result = jsonModel.putDocument(node.getLocator(), TOPIC_INDEX, 
				CORE_TYPE, node.toJSON());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#getVirtualNodeIfExists(java.lang.String, java.util.Set)
	 */
	@Override
	public IResult getVirtualNodeIfExists(String locator,
										Set<String> credentials) {
//		String query = ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":"+ITopicQuestsOntology.MERGE_ASSERTION_TYPE+
//		" AND "+ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY+":"+locator;
		BoolQueryBuilder qba = QueryBuilders.boolQuery();
		QueryBuilder qb1 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, ITopicQuestsOntology.MERGE_ASSERTION_TYPE);
		QueryBuilder qb2 = QueryBuilders.termQuery(ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY, locator);
		qba.must(qb1);
		qba.must(qb2);		
		environment.logDebug("JSONDocStoreTopicMapProvider.getVirtualNodeIfExists- "+qba.toString());
		IResult result =  jsonModel.runQuery(TOPIC_INDEX, qba, 0, -1, CORE_TYPE);
		if (result.getResultObject() != null) {
			result.setResultObject(null);
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			INode n;
			JSONObject jo;
			try {
				//There can be only one merged on the given node
				jo = jsonToJSON(docs.get(0));
				//This is the tuple
				String subjLoc = (String)jo.get(ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY);
				return this.getNode(subjLoc, credentials);
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#existsTupleBySubjectOrObjectAndRelation(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult existsTupleBySubjectOrObjectAndRelation(String theLocator,
			String relationLocator) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		QueryBuilder qb1 = QueryBuilders.termQuery(ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY, theLocator);
		QueryBuilder qb2 = QueryBuilders.termQuery(ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY, theLocator);
		QueryBuilder qb3 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, relationLocator);
		qb.must(qb3);
		qb.should(qb1);
		qb.should(qb2);
		environment.logDebug("JSONDocStoreTopicMapProvider.existsTupleBySubjectOrObjectAndRelation- "+qb.toString());
		IResult result =  jsonModel.runQuery(TOPIC_INDEX, qb, 0, -1, CORE_TYPE);
		if (result.getResultObject() != null)
			result.setResultObject(new Boolean(true));
		else
			result.setResultObject(new Boolean(false));
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#nodeIsA(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public IResult nodeIsA(String nodeLocator, String targetTypeLocator,
			Set<String> credentials) {
		IResult result = walkUpTransitiveClosure(nodeLocator,targetTypeLocator,credentials);
		if (result.getResultObject() != null)
			result.setResultObject(new Boolean(true));
		else
			result.setResultObject(new Boolean(false));
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByPSI(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listNodesByPSI(String psi, int start, int count,
			Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listNodesByPSI- "+psi);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByProperty(TOPIC_INDEX, ITopicQuestsOntology.PSI_PROPERTY_TYPE, 
				psi, start, count, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByLabelAndType(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listNodesByLabelAndType(String label, String typeLocator,
			int start, int count, Set<String> credentials) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		QueryBuilder qb1 = QueryBuilders.termQuery(ITopicQuestsOntology.LABEL_PROPERTY, label);
		QueryBuilder qb2 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, typeLocator);
		qb.must(qb1);
		qb.should(qb2);
		environment.logDebug("JSONDocStoreTopicMapProvider.listNodesByLabelAndType- "+qb.toString());
		IResult result =  jsonModel.runQuery(TOPIC_INDEX, qb, 0, -1, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByLabel(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listNodesByLabel(String label, int start, int count,
			Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listNodesByLabel- "+label);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByProperty(TOPIC_INDEX, ITopicQuestsOntology.LABEL_PROPERTY, 
				label, start, count, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByLabelLike(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listNodesByLabelLike(String labelFragment, int start,
			int count, Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listNodesByLabelLike- "+labelFragment);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByWildcardPropertyValue(TOPIC_INDEX, ITopicQuestsOntology.LABEL_PROPERTY, 
				labelFragment, start, count, CORE_TYPE);
		//that required the "label" field to be unanalyzed
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByDetailsLike(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listNodesByDetailsLike(String detailsFragment, int start,
			int count, Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listNodesByDetailsLike- "+detailsFragment);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByWildcardPropertyValue(TOPIC_INDEX, ITopicQuestsOntology.DETAILS_PROPERTY, 
				detailsFragment, start, count, CORE_TYPE);
		//that required the "label" field to be unanalyzed
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByQuery(java.lang.String, int, int, java.util.Set)
	 * MUST be a valid ElasticSearch <code>queryString</code>
	 */
	@Override
	public IResult listNodesByQuery(String queryString, int start, int count,
			Set<String> credentials) {
		return jsonModel.runQuery(TOPIC_INDEX, queryString, start, count, CORE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByCreatorId(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listNodesByCreatorId(String creatorId, int start, int count,
			Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listNodesByCreatorId- "+creatorId);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByProperty(TOPIC_INDEX, ITopicQuestsOntology.CREATOR_ID_PROPERTY, 
				creatorId, start, count, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listNodesByType(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listNodesByType(String typeLocator, int start, int count,
			Set<String> credentials) {
		return listInstanceNodes(typeLocator,start,count,credentials);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listTuplesBySignature(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listTuplesBySignature(String signature, int start,
			int count, Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listTuplesBySignature- "+signature);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByProperty(TOPIC_INDEX, ITopicQuestsOntology.TUPLE_SIGNATURE_PROPERTY, 
				signature, start, count, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listInstanceNodes(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listInstanceNodes(String typeLocator, int start, int count,
			Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listInstanceNodes- "+typeLocator);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByProperty(TOPIC_INDEX, ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, 
				typeLocator, start, count, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			System.out.println("LISTINSTANCES "+docs);
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listTrimmedInstanceNodes(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listTrimmedInstanceNodes(String typeLocator, int start,
			int count, Set<String> credentials) {
		// Can be same type AND virtual proxy
		// OR Can be same type AND NOT merged 
		// TODO Auto-generated method stub
//		String query = 
//				"("+ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":"+typeLocator+" AND "+ITopicQuestsOntology.IS_VIRTUAL_PROXY+":true) OR "+
//				"("+ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE+":"+typeLocator+" AND NOT "+ITopicQuestsOntology.MERGE_TUPLE_PROPERTY+":* )";
		BoolQueryBuilder qba = QueryBuilders.boolQuery();
		BoolQueryBuilder qbb = QueryBuilders.boolQuery();
		QueryBuilder qb1 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, typeLocator);
		QueryBuilder qb2 = QueryBuilders.termQuery(ITopicQuestsOntology.IS_VIRTUAL_PROXY, true);
		qba.must(qb1);
		qba.must(qb2);
		QueryBuilder qb3 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, typeLocator);
		QueryBuilder qb4 = QueryBuilders.wildcardQuery(ITopicQuestsOntology.MERGE_TUPLE_PROPERTY, "*");
		qbb.must(qb3);
		qbb.mustNot(qb4);
		BoolQueryBuilder qbc = QueryBuilders.boolQuery();
		qbc.should(qba);
		qbc.should(qbb);
		
		environment.logDebug("JSONDocStoreTopicMapProvider.listTrimmedInstanceNodes- "+qbc.toString());
		IResult result =  jsonModel.runQuery(TOPIC_INDEX, qbc, 0, -1, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#listSubclassNodes(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listSubclassNodes(String superclassLocator, int start,
			int count, Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.listSubclassNodes- "+superclassLocator);
//listDocumentsByProperty(String index, String key, String value, int start, int count, String... types)
		IResult result = jsonModel.listDocumentsByProperty(TOPIC_INDEX, ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE, 
				superclassLocator, start, count, CORE_TYPE);
		if (result.getResultObject() != null) {
			List<String>docs = (List<String>)result.getResultObject();
			System.out.println("LISTSUBS "+docs);
			String json;
			Iterator<String>itr = docs.iterator();
			List<INode>nl = new ArrayList<INode>();
			result.setResultObject(nl);
			INode n;
			JSONObject jo;
			try {
				while(itr.hasNext()) {
					json = itr.next();
					jo = jsonToJSON(json);
					if (credentialUtil.checkCredentials(jo,credentials)) 
						nl.add(new Node(jo));
					
				}
			} catch (Exception e) {
				jsonEnvironment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#putTuple(org.topicquests.model.api.ITuple)
	 */
	@Override
	public IResult putTuple(ITuple tuple) {
		return putNode(tuple);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#getTuple(java.lang.String, java.util.Set)
	 */
	@Override
	public IResult getTuple(String tupleLocator, Set<String> credentials) {
		environment.logDebug("JSONDocStoreTopicMapProvider.getTuple- "+tupleLocator);
		IResult result = jsonModel.getDocument(TOPIC_INDEX, CORE_TYPE, tupleLocator);
		if (result.getResultObject() != null) {
			try {
				JSONObject jo = (JSONObject)parser.parse((String)result.getResultObject());
				if (credentialUtil.checkCredentials(jo, credentials)) {
					ITuple n = new Node(jo);
					result.setResultObject(n);
				}
			} catch (Exception e) {
				environment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#updateNode(org.topicquests.model.api.INode)
	 */
	@Override
	public IResult updateNode(INode node) {
		return putNode(node);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITopicDataProvider#runQuery(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult runQuery(String queryString, int start, int count,
			Set<String> credentials) {
		return this.listNodesByQuery(queryString, start, count, credentials);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#init(org.topicquests.model.Environment, int)
	 */
	@Override
	public IResult init(Environment env, int cachesize) {
		IResult result = new ResultPojo();
		//NOT USED
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#getNodeModel()
	 */
	@Override
	public INodeModel getNodeModel() {
		return _model;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#getTupleQuery()
	 */
	@Override
	public ITupleQuery getTupleQuery() {
		return tupleQuery;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#removeFromCache(java.lang.String)
	 */
	@Override
	public void removeFromCache(String nodeLocator) {
		nodeCache.remove(nodeLocator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#getUUID()
	 */
	@Override
	public String getUUID() {
		UUID x = UUID.randomUUID();
		return x.toString();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#getUUID_Pre(java.lang.String)
	 */
	public String getUUID_Pre(String prefix) {
		UUID x = UUID.randomUUID();
		return prefix+x.toString();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#getUUID_Post(java.lang.String)
	 */
	public String getUUID_Post(String suffix) {
		UUID x = UUID.randomUUID();
		return x.toString()+suffix;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IDataProvider#createMergeRule(org.topicquests.common.api.IMergeRuleMethod)
	 */
	@Override
	public IResult createMergeRule(IMergeRuleMethod theMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	private JSONObject jsonToJSON(String json) throws Exception {
		return (JSONObject)parser.parse(json);
	}
	
	/**
	 * A recursive call to walk up the isA hierarchy, if any, above the
	 * @param locator
	 * @param typeTargetLocator
	 * @param credentials
	 * @return IResult.returnObject = <code>null</code> if not found. Any non-null means found
	 */
	IResult walkUpTransitiveClosure(String locator, String typeTargetLocator, Set<String>credentials) {
		IResult result = new ResultPojo();
		result.setResultObject(null);
		IResult temp = getNode(locator,credentials);
		INode child = (INode)temp.getResultObject();
		temp.setResultObject(null);
		if (temp.hasError())
			result.addErrorString(temp.getErrorString());
		if (child != null) {
			//The cases where that could be null are:
			//  database error (node's there, system failed)
			//  lack of appropriate credentials (private node)
			List<String> supers = child.listSuperclassIds();
			String type = child.getNodeType();
			if (type.equals(typeTargetLocator)) {
				result.setResultObject(child);
				return result;
			} else {
				temp = walkUpTransitiveClosure(type,typeTargetLocator,credentials);
				if (temp.hasError())
					result.addErrorString(temp.getErrorString());
				if (temp.getResultObject() != null) {
					result.setResultObject(temp.getResultObject());
					return result;
				}
			}
			if (supers != null && supers.size() > 0) {
				Iterator<String>itr = supers.iterator();
				while (itr.hasNext()) {
					temp = walkUpTransitiveClosure(itr.next(),typeTargetLocator,credentials);
					if (temp.hasError())
						result.addErrorString(temp.getErrorString());
					if (temp.getResultObject() != null) {
						result.setResultObject(temp.getResultObject());
						return result;
					}
				}
			}
		}
		return result;
	}
	
	public void shutDown() {
		this.interceptor.shutDown();
	}

	@Override
	public INodeQuery getNodeQuery(INode node) {
		return new NodeQuery(node,this,jsonModel);
	}
}