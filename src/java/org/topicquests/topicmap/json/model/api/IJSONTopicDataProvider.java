/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.provider.ITopicDataProvider;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.merge.VirtualizerHandler;
import org.topicquests.common.api.IResult;

/**
 * @author park
 *
 */
public interface IJSONTopicDataProvider extends ITopicDataProvider {
	public static String
			AIR_INDEX		= "airs"; // correlates with jsonblobstore-props.xml
	
	/**
	 * Return an {@link ICitationModel} instance
	 * @return
	 */
	ICitationModel getCitationModel();
	
	/**
	 * <p>This accepts an {@link INode} and imports it</p>
	 * <p>The node might be a new node created in an editor,
	 * or an edited existing node.</p>
	 * @param nodeXML
	 */
	void updateNodeFromXML(String nodeXML);
	
	/**
	 * Load a {@link ITreeNode} starting from <code>rootNodeLocator</code>
	 * with all its child nodes (<em>subs</em> and <em>instances</em>)
	 * to a depth defined by <code>maxDepth</code>
	 * @param rootNodeLocator
	 * @param maxDepth  -1 means no limit
	 * @param start TODO
	 * @param count TODO
	 * @param credentials
	 * @return
	 */
	IResult loadTree(String rootNodeLocator, int maxDepth, int start, int count, ITicket credentials);

	/**
	 * Support various ways of asserting a merge between two nodes
	 * @param leftNode
	 * @param rightNode
	 * @param reason
	 * @param userLocator
	 * @param mergeListener 
	 */
	void mergeTwoNodes(INode leftNode, INode rightNode, String reason, String userLocator, IMergeResultsListener mergeListener);

	/**
	 * 
	 * @param h
	 */
	void setVirtualizerHandler(VirtualizerHandler h);
	
	/**
	 * We can recycle nodes when done
	 * @param n
	 */
	void recycleNode(INode n);
}
