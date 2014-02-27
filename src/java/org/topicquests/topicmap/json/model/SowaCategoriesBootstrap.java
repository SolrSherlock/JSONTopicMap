/**
 * 
 */
package org.topicquests.topicmap.json.model;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IBootstrap;
import org.topicquests.common.api.IResult;
import org.topicquests.model.BootstrapBase;
import org.topicquests.model.api.provider.ITopicDataProvider;

/**
 * @author park
 * @see http://www.jfsowa.com/ontology/toplevel.htm
 */
public class SowaCategoriesBootstrap extends BootstrapBase implements
		IBootstrap {

	/**
	 * @param db
	 */
	public SowaCategoriesBootstrap(ITopicDataProvider db) {
		super(db);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.common.api.IBootstrap#bootstrap()
	 */
	@Override
	public IResult bootstrap() {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

}
