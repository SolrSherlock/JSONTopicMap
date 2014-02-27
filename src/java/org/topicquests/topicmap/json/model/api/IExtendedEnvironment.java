/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

import org.topicquests.model.api.IEnvironment;
import org.topicquests.model.api.query.IQueryModel;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.SearchEnvironment;

/**
 * @author park
 *
 */
public interface IExtendedEnvironment extends IEnvironment {
	
	IExtendedConsoleDisplay getConsoleDisplay();

	IQueryModel getQueryModel();
	
	SearchEnvironment getSearchEnvironment();
}
