/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

/**
 * @author park
 *
 */
public interface ISameLabelListener {

	/**
	 * When <em>same label</em> study results in a merge,
	 * the <code>virtualNodeLocator</code> plus the original
	 * locators are returned
	 * @param virtualNodeLocator
	 * @param nodeALocator
	 * @param nodeBLocator
	 * @param errorMessages
	 */
	void acceptSameLabelResults(String virtualNodeLocator, 
								String nodeALocator,
								String nodeBLocator,
								String errorMessages);
}
