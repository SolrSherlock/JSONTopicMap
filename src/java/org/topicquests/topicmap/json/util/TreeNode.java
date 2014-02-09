/**
 * 
 */
package org.topicquests.topicmap.json.util;

import java.util.*;

import org.topicquests.topicmap.json.model.api.ITreeNode;

/**
 * @author park
 *
 */
public class TreeNode implements ITreeNode {
	private String locator = "";
	private String label = "no label";
	private List<ITreeNode>subs = null;
	private List<ITreeNode>instances = null;
	/**
	 * 
	 */
	public TreeNode() {
	}
	
	public TreeNode(String locator) {
		setNodeLocator(locator);
	}
	
	public TreeNode(String locator, String label) {
		setNodeLocator(locator);
		setNodeLabel(label);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#setNodeLocator(java.lang.String)
	 */
	@Override
	public void setNodeLocator(String locator) {
		this.locator = locator;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#getNodeLocator()
	 */
	@Override
	public String getNodeLocator() {
		return locator;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#setNodeLabel(java.lang.String)
	 */
	@Override
	public void setNodeLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#getNodeLabel()
	 */
	@Override
	public String getNodeLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#addSubclassChild(org.topicquests.topicmap.json.model.api.ITreeNode)
	 */
	@Override
	public void addSubclassChild(ITreeNode c) {
		if (subs == null)
			subs = new ArrayList<ITreeNode>();
		subs.add(c);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#addInstanceChild(org.topicquests.topicmap.json.model.api.ITreeNode)
	 */
	@Override
	public void addInstanceChild(ITreeNode c) {
		if (instances == null)
			instances = new ArrayList<ITreeNode>();
		instances.add(c);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#listSubclassChildNodes()
	 */
	@Override
	public List<ITreeNode> listSubclassChildNodes() {
		return subs;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ITreeNode#listInstanceChildNodes()
	 */
	@Override
	public List<ITreeNode> listInstanceChildNodes() {
		return instances;
	}

	@Override
	public String simpleToXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<node locator=\""+locator+"\">\n");
		ITreeNode k;
		Iterator<ITreeNode>itr;
		if (subs != null) {
			buf.append("<subs>\n");
			itr = subs.iterator();
			while (itr.hasNext()) {
				k = itr.next();
				buf.append(k.simpleToXML());
			}
			buf.append("</subs>\n");
		}
		if (instances != null) {
			buf.append("<instances>\n");
			itr = instances.iterator();
			while (itr.hasNext()) {
				k = itr.next();
				buf.append(k.simpleToXML());
			}
			buf.append("</instances>\n");
		}
		buf.append("</node>");
		return buf.toString();
	}

	@Override
	public int getSubclassCount() {
		if (this.subs != null)
			return subs.size();
		return 0;
	}

	@Override
	public int getInstanceCount() {
		if (this.instances != null)
			return instances.size();
		return 0;
	}

}
