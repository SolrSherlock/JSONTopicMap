/**
 * 
 */
package org.topicquests.topicmap.json.model.api;
import java.util.*;

import org.topicquests.common.api.IResult;
import org.topicquests.model.api.IEnvironment;
/**
 * @author park
 *
 */
public interface ISocialBookmarkModel {
	
	/**
	 * Make this so it can be booted from config properties list of maps
	 * @param env
	 * @return
	 */
	IResult init(IEnvironment env);
	
	/**
	 * <p>Forge social bookmark topics between a <code>url</code>, some <code>tags</code>
	 * and a <code>userId</code> 
	 * @param url
	 * @param title
	 * @param details
	 * @param language
	 * @param userId
	 * @param tags
	 * @return
	 */
	IResult bookmark(String url, String title, String details, String language, String userId, List<String>tags);
	
	/**
	 * Create a tag locator from <code>tag</code>; multiple-word tags
	 * use underscores '_' for spaces
	 * @param tag
	 * @return
	 */
	String formTagLocator(String tag);

}
