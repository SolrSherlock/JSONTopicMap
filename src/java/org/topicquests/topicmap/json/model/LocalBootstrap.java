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
 *
 */
public class LocalBootstrap extends BootstrapBase implements IBootstrap {

	public LocalBootstrap(ITopicDataProvider db) {
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
