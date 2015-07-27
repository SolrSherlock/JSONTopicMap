/**
 * 
 */
package test;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.ITicket;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 *
 */
public class TypeURLTest {
	private JSONTopicmapEnvironment environment;
	private IJSONDocStoreModel jsonModel;
	private INodeModel nodeModel;
	private IJSONTopicDataProvider database;
	private ITicket credentials;
	private String userId;
	private String lang = "en";
	private String smallImagePath = null;
	private String largeImagePath = null;
	private boolean isPrivate = false;
	private final String url = "http://google.com/";
	private final String typ = ITopicQuestsOntology.CLASS_TYPE;

	/**
	 * 
	 */
	public TypeURLTest() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		jsonModel = environment.getJSONModel();
		nodeModel = environment.getDataProvider().getNodeModel();
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		//create credentials
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
		userId = ITopicQuestsOntology.SYSTEM_USER;
		runTest();
	}

	void runTest() {
		INode n1 = nodeModel.newSubclassNode(Long.toString(System.currentTimeMillis()), typ, "Test", "Yup", "en", userId, "", "", false);
		n1.setURL(url);
		IResult r = database.putNode(n1, false);
		System.out.println("A "+r.getErrorString());
	//	r = database.getNodeByURL(url, credentials);
	//	System.out.println("B "+r.getErrorString()+" "+r.getResultObject());
		r = database.listNodesByTypeAndURL(typ, url, credentials);
		System.out.println("C "+r.getErrorString()+" "+r.getResultObject());
		environment.shutDown();
	}
}
