/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

import java.util.List;

/**
 * @author park
 *
 */
public interface ITreeNode {

	void setNodeLocator(String locator);
	String getNodeLocator();
	
	void setNodeLabel(String label);
	String getNodeLabel();
	
	void addSubclassChild(ITreeNode c);
	
	void addInstanceChild(ITreeNode c);
	
	int getSubclassCount();
	
	int getInstanceCount();
	
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	List<ITreeNode> listSubclassChildNodes();
	
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	List<ITreeNode> listInstanceChildNodes();
	
	String simpleToXML();
}
