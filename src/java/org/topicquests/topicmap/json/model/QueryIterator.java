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

import java.util.Set;

import org.topicquests.common.api.IResult;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.IQueryIterator;

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
	private Set<String>_credentials;

	/**
	 * 
	 */
	public QueryIterator(JSONTopicmapEnvironment env) {
		environment = env;
		database = environment.getTopicDataProvider();
		
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IQueryIterator#start(java.lang.String, int, java.util.Set)
	 */
	@Override
	public void start(String queryString, int hitCount, Set<String> credentials) {
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
		return database.runQuery(_query, _cursor, _count, _credentials);
	}

}
