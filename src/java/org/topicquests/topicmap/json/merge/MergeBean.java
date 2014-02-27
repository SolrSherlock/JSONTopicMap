/**
 * 
 */
package org.topicquests.topicmap.json.merge;

import java.util.Map;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.model.api.IEnvironment;
import org.topicquests.model.api.IMergeImplementation;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.IVirtualizer;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 *
 */
public class MergeBean implements IMergeImplementation {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private INodeModel model;
	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IMergeImplementation#init(org.topicquests.model.api.IEnvironment)
	 */
	@Override
	public void init(IEnvironment environment) {
		this.environment = (JSONTopicmapEnvironment)environment;
		database = (IJSONTopicDataProvider)environment.getDataProvider();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IMergeImplementation#setNodeModel(org.topicquests.model.api.INodeModel)
	 */
	@Override
	public void setNodeModel(INodeModel m) {
		model = m;

	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.IMergeImplementation#assertMerge(java.lang.String, java.lang.String, java.util.Map, double, java.lang.String)
	 */
	@Override
	public IResult assertMerge(String sourceNodeLocator,
			String targetNodeLocator, Map<String, Double> mergeData,
			double mergeConfidence, IVirtualizer virtualizer, String userLocator) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

}
