/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

import java.util.Map;

import org.topicquests.common.api.IResult;
import org.topicquests.model.api.node.INode;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;

/**
 * @author park
 *
 */
public interface IVirtualizer {

	/**
	 * Create a <em>VirtualNode</em>
	 * @param primary
	 * @param merge
	 * @param mergeData
	 * @param confidence
	 * @param userLocator
	 * @return returns the locator of the created VirtualNode
	 */
	IResult createVirtualNode(INode primary, INode merge,
				Map<String,Double> mergeData, double confidence,
				String userLocator);
	
	/**
	 * Init allows us to use different implementations
	 * @param env
	 */
	void init(JSONTopicmapEnvironment env);
}
