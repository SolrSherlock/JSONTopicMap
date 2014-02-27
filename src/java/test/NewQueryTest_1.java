/**
 * 
 */
package test;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 *
 */
public class NewQueryTest_1 {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private ITicket credentials;
	private String userId;
	private String lang = "en";
	/** test query to see if we can find the node with this label */
	private final String LABEL = "Ontology Inverse Functional Property Type";

	/**
	 * 
	 */
	public NewQueryTest_1() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		//create credentials
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
		userId = ITopicQuestsOntology.SYSTEM_USER;
		runTest();
	}

	void runTest() {
		IResult r = null;
		//this did not work
		 /*r = database.listNodesByLabel(LABEL, lang, 0, 10, credentials);
		System.out.println(r.getResultObject());
		System.out.println(r.getErrorString());
		r = database.getNode("OntologyInverseFunctionalPropertyType", credentials);
		INode n = (INode)r.getResultObject();
		System.out.println(n.toJSON());*/
		QueryBuilder qb1 = QueryBuilders.matchQuery("label.en", LABEL);

		String query = qb1.toString(); // "{\"query\": {\"match\": {\"label.en\": \""+LABEL+"\"}}}";
		System.out.println(query);
		r = database.runQuery(query, 0, 10, credentials);
		System.out.println(r.getResultObject());
		System.out.println(r.getErrorString());
		
		environment.shutDown();
		
		
	}
}
