/**
 * 
 */
package test;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.ITicket;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.api.IJSONDataProvider;
import org.topicquests.topicmap.json.model.api.ITreeNode;

/**
 * @author park
 *
 */
public class TreeWalkTest {
	private JSONTopicmapEnvironment environment;
	private IJSONDataProvider dataProvider;
	private ITicket credentials;

	/**
	 * 
	 */
	public TreeWalkTest() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		dataProvider = (IJSONDataProvider)environment.getDataProvider();
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
		
		IResult r = dataProvider.loadTree(ITopicQuestsOntology.TYPE_TYPE, 4, 0, 50, credentials);
		ITreeNode tree = (ITreeNode)r.getResultObject();
		System.out.println(tree.simpleToXML());
		
		environment.shutDown();
	}

}
