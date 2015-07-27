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
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.ITicket;
//import org.json.simple.JSONObject;
import org.topicquests.model.api.provider.ITopicDataProvider;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.util.LoggingPlatform;
import org.apache.commons.collections4.CollectionUtils;
import net.minidev.json.JSONObject;

/**
 * @author park
 *
 */
public class CredentialUtility {
	private LoggingPlatform log = LoggingPlatform.getLiveInstance();
	private ITopicDataProvider database;
	private IJSONDocStoreModel jsonModel;

	/**
	 * 
	 */
	public CredentialUtility(ITopicDataProvider d, IJSONDocStoreModel j) {
		database = d;
		jsonModel = j;
	}

	/**
	 * Return <code>true</code> if the document is one
	 * <em>public</em> or <em>credentials are satisfied</em> and <em>isLive</em>
	 * @param locator
	 * @param credentials
	 * @return
	 */
	public boolean checkCredentials(JSONObject jo, ITicket credentials) {
		String il = (String)jo.get(ITopicQuestsOntology.IS_LIVE);
		if (il != null && il.equals("false")) {
			return false;
		}
		String o = (String)jo.get(ITopicQuestsOntology.IS_PRIVATE_PROPERTY);
		if (o == null) {
			//defaults true
			log.logDebug("CredentialUtility bad: "+jo.toJSONString());
			return true;			
		}
		else if ( o.equals("true")) {
			//same creator?
			o = (String)jo.get(ITopicQuestsOntology.CREATOR_ID_PROPERTY);
			if (o.equals(credentials.getUserLocator()))
				return true;
			//same avatar?
			List<String>l = credentials.listAvatars();
			if (l.contains(o))
				return true;
			//check acls
			l = credentials.listGroupLocators();
			if (!l.isEmpty()) {
				Object x = jo.get(ITopicQuestsOntology.RESTRICTION_PROPERTY_TYPE);
				String y = (String)x;
				if (x instanceof String) {
					return (l.contains(y));
				}
				List<String>acls = (List<String>)x;
				Collection<String> intersect = CollectionUtils.intersection(acls, l);
				return (!intersect.isEmpty());
			}
		} else {
			return true;
		}
		return false;
	}

}
