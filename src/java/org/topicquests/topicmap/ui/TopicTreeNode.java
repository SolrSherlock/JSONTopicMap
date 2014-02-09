/**
 * 
 */
package org.topicquests.topicmap.ui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author park
 *
 */
public class TopicTreeNode extends DefaultMutableTreeNode {
	private String locator;
	/**
	 * 
	 */
	public TopicTreeNode() {
		// TODO Auto-generated constructor stub
	}

	public void setLocator(String lox) {
		locator = lox;
	}
	public String getLocator() {
		return locator;
	}
	/**
	 * @param userObject
	 */
	public TopicTreeNode(Object userObject) {
		super(userObject);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param userObject
	 * @param allowsChildren
	 */
	public TopicTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
		// TODO Auto-generated constructor stub
	}

}
