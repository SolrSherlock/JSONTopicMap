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


import org.topicquests.model.api.ITicket;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.INode;
import org.topicquests.model.Node;
import org.topicquests.model.api.IQueryIterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * @author park
 *
 */
public class QueryIterator implements IQueryIterator {
	private JSONTopicmapEnvironment environment;
	private IDataProvider database;
	private String _query;
	private int _count;
	private int _cursor;
	private ITicket _credentials;

	/**
	 * 
	 */
	public QueryIterator(JSONTopicmapEnvironment env) {
		environment = env;
		database = environment.getDataProvider();
		
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IQueryIterator#start(java.lang.String, int, java.util.Set)
	 */
	@Override
	public void start(String queryString, int hitCount, ITicket credentials) {
		_query = queryString;
		_count = hitCount;
		_cursor = 0;
		_credentials = credentials;		
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IQueryIterator#reset()
	 */
	@Override
	public void reset() {
		_cursor = 0;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IQueryIterator#next()
	 */
	@Override
	public IResult next() {
		IResult result = runQuery();
		_cursor += _count;
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IQueryIterator#previous()
	 */
	@Override
	public IResult previous() {
		IResult result = runQuery();
		_cursor -= _count;
		if (_cursor < 0)
			_cursor = 0;
		return result;
	}
	
	private IResult runQuery() {
		IResult x = database.runQuery(_query, _cursor, _count, _credentials);
		IResult result = new ResultPojo();
		List<String>jsonStrings = (List<String>)x.getResultObject();
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		if (jsonStrings != null && !jsonStrings.isEmpty()) {
			JSONParser p = new JSONParser();
			List<INode>nodes = new ArrayList<INode>();
			result.setResultObject(nodes);
			try {
				String json;
				JSONObject jo;
				Iterator<String>itr = jsonStrings.iterator();
				while (itr.hasNext()) {
					json = itr.next();
					jo = (JSONObject)p.parse(json);
					nodes.add(new Node(jo));
				}
				
			} catch (Exception e) {
				environment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}

		return result;
	}

}
