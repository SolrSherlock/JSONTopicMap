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

import org.json.simple.JSONObject;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.util.LoggingPlatform;

/**
 * @author park
 *
 */
public class CredentialUtility {
	private LoggingPlatform log = LoggingPlatform.getLiveInstance();
	private IDataProvider database;
	private IJSONDocStoreModel jsonModel;

	/**
	 * 
	 */
	public CredentialUtility(IDataProvider d, IJSONDocStoreModel j) {
		database = d;
		jsonModel = j;
	}

	/**
	 * Return <code>true</code> if the document is one of
	 * <em>public</em> or <em>credentials are satisfied</em>
	 * @param locator
	 * @param credentials
	 * @return
	 */
	public boolean checkCredentials(JSONObject jo, Set<String>credentials) {
		boolean result = true;
		//TODO
		return result;
	}

}
