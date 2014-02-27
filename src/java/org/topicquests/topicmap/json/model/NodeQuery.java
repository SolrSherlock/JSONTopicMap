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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.Compare;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.query.INodeQuery;
import org.topicquests.model.api.IPredicate;
import org.topicquests.model.api.node.ITuple;
import org.topicquests.model.api.query.ITupleQuery;
import org.topicquests.model.api.ITicket;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
//import org.topicquests.topicmap.json.model.api.IJSONTopicMapOntology;
import org.topicquests.util.LoggingPlatform;
import org.topicquests.model.Node;

/**
 * @author park
 *
 */
public class NodeQuery implements INodeQuery {
	private LoggingPlatform log = LoggingPlatform.getLiveInstance();
	private IJSONTopicDataProvider database;
//	private IJSONDocStoreModel jsonModel;
	private INode me;
    public List<HasContainer> hasContainers = new ArrayList<HasContainer>();
    private List<JSONObject>foundTuples = null;
    private List<ITuple>filteredTuples = null;
    private List<INode>filteredNodes = null;
    private boolean isFiltered = false;
//	private JSONParser parser;
	private ITupleQuery tupleQuery;

	/**
	 * 
	 * @param n
	 * @param dp
	 * @param m
	 */
	public NodeQuery(INode n, IJSONTopicDataProvider dp, IJSONDocStoreModel m) {
		me = n;
//		jsonModel = m;
		this.database = dp;
		tupleQuery = database.getTupleQuery();
//		parser = new JSONParser();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#tuples(java.util.Set)
	 */
	@Override
	public IResult tuples(ITicket credentials) {
		IResult result = new ResultPojo();
		if (!isFiltered) {
			IResult r = executeQuery(credentials);
			if (r.hasError())
				result.addErrorString(r.getErrorString());
			isFiltered = true;
		}
		result.setResultObject(filteredTuples);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#nodes(java.util.Set)
	 */
	@Override
	public IResult nodes(ITicket credentials) {
		IResult result = new ResultPojo();
		if (!isFiltered) {
			IResult r = executeQuery(credentials);
			if (r.hasError())
				result.addErrorString(r.getErrorString());
			isFiltered = true;
		}
		if (filteredTuples != null && !filteredTuples.isEmpty()) {
			ITuple t;
			IResult r;
			filteredNodes = new ArrayList<INode>();
			result.setResultObject(filteredNodes);
			Iterator<ITuple>itr = filteredTuples.iterator();
			String lox;
			while (itr.hasNext()) {
				t = itr.next();
				if (!t.getSubjectLocator().equals(me.getLocator()))
					lox = t.getSubjectLocator();
				else
					lox = t.getObject();
				r = database.getNode(lox, credentials);
				if (r.hasError())
					result.addErrorString(r.getErrorString());
				if (r.getResultObject() != null)
					filteredNodes.add((INode)r.getResultObject());
				else
					result.addErrorString("NodeQuery missing node for "+lox);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#setRelationType(java.lang.String, java.util.Set)
	 */
	@Override
	public INodeQuery setRelationType(String relationType,
						int start, int count, ITicket credentials) {
		//clear everything
		foundTuples = null;
		filteredTuples = null;
		filteredNodes = null;
		hasContainers.clear();
		isFiltered = false;
		//TODO: wrong query
		//We must work off what we already have in this topic
		//This topic might be either the subject or object of its tuples of the given relationType
		
		IResult r = tupleQuery.listTuplesByPredTypeAndObjectOrSubject(relationType, this.me.getLocator(), start, count, credentials);
		if (r.getResultObject() != null)
			foundTuples = (List<JSONObject>)r.getResultObject();
		return this;
	}
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.INodeQuery#count()
	 */
	@Override
	public long count() {
		//cardinality of this node's tuples as filtered by labels
		if (foundTuples != null)
			return (long)foundTuples.size();
		
		return 0;
	}
	

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#nodeLocators(java.util.Set)
	 */
	@Override
	public IResult nodeLocators(ITicket credentials) {
		IResult result = new ResultPojo();
		if (foundTuples == null)
			result.addErrorString("No tuples found in this query");
		//that error means that somebody didn't setLabels
		else {
			List<String>lox = new ArrayList<String>();
			result.setResultObject(lox);
			IResult r;
			//here, we must fetch every tuple and get its subject or object
			JSONObject t;
			Iterator<JSONObject>itr = foundTuples.iterator();
			while(itr.hasNext()) {
				t = itr.next();
				lox.add((String)t.get(ITopicQuestsOntology.LOCATOR_PROPERTY));
			}
			
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#has(java.lang.String, java.lang.Object)
	 */
	@Override
	public INodeQuery tupleHas(String key, Object value) {
        this.hasContainers.add(new HasContainer(key, Compare.EQUAL, value));
        return this;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#hasNot(java.lang.String, java.lang.Object)
	 */
	@Override
	public INodeQuery tupleHasNot(String key, Object value) {
        this.hasContainers.add(new HasContainer(key, Compare.NOT_EQUAL, value));
        return this;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#has(java.lang.String, org.topicquests.model.api.IPredicate, java.lang.Object)
	 */
	@Override
	public INodeQuery tupleHas(String key, IPredicate predicate, Object value) {
        this.hasContainers.add(new HasContainer(key, predicate, value));
        return this;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#has(java.lang.String)
	 */
	@Override
	public INodeQuery tupleHas(String key) {
        this.hasContainers.add(new HasContainer(key, Compare.NOT_EQUAL, null));
        return this;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.INodeQuery#hasNot(java.lang.String)
	 */
	@Override
	public INodeQuery tupleHasNot(String key) {
        this.hasContainers.add(new HasContainer(key, Compare.EQUAL, null));
        return this;
	}

	private IResult executeQuery(ITicket credentials) {
		IResult result = new ResultPojo();
		filteredTuples = new ArrayList<ITuple>();
		if (foundTuples != null && !foundTuples.isEmpty()) {
			filteredTuples = new ArrayList<ITuple>();
			Iterator<JSONObject>itr = foundTuples.iterator();
			ITuple t;
			JSONObject jo;
			if (hasContainers.isEmpty()) {
				while(itr.hasNext()) {
					jo = itr.next();
					t = new Node(jo);
					filteredTuples.add(t);
				}
				
			} else {
				HasContainer x;
				Iterator<HasContainer>itx;
				while (itr.hasNext()) {
					jo = itr.next();
						t = new Node(jo);
						itx = hasContainers.iterator();
						while (itx.hasNext()) {
							x = itx.next();
							if (x.isLegal(t))
								filteredTuples.add(t);
						}
				}
			}
		}
		return result;
	}
    ////////////////////


    protected class HasContainer {
        public String key;
        public Object value;
        public IPredicate predicate;

        public HasContainer(final String key, final IPredicate predicate, final Object value) {
            this.key = key;
            this.value = value;
            this.predicate = predicate;
        }

        public boolean isLegal(final INode element) {
            return this.predicate.evaluate(element.getProperty(this.key), this.value);
        }
    }

}
