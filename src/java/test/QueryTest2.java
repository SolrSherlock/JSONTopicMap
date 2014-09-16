/**
 * 
 */
package test;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 *
 */
public class QueryTest2 {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private ITicket credentials;
	private String userId;
	private String lang = "en";

	/**
	 * 
	 */
	public QueryTest2() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		//create credentials
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
		userId = ITopicQuestsOntology.SYSTEM_USER;
		runTest();
	}
	
	void runTest() {
		System.out.println("Running");
		IResult r = null;

	//	StringBuilder buf = new StringBuilder();
	//		buf.append("{\"from\":"+0+",\"size\":"+30+",");
		//fails {"size":30, "from":0,"term": {"sbOf": "TypeType"}}
		//fails {"from":0, "size":30,"query":{"term": {"sbOf": "TypeType"}}}
		//fails: {"from":0,"size":30,"query":{"term":{"sbOf":"TypeType"}}}
		//fails: {"from":0,"size":30,"query":{"match":{"sbOf":"TypeType"}}}
		//http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-from-size.html
		TermQueryBuilder termQuery = QueryBuilders.termQuery(ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE, ITopicQuestsOntology.TYPE_TYPE);
		//StringBuilder buf1 = new StringBuilder("\"query\":{\"term\":{");
//		StringBuilder buf1 = new StringBuilder("\"term\": {");
		//buf1.append("\""+ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE+"\":\""+ITopicQuestsOntology.TYPE_TYPE+"\"}}}");
//		buf1.append("\""+ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE+"\": \""+ITopicQuestsOntology.TYPE_TYPE+"\"}}");
//		buf.append(termQuery.toString());
//		StringBuilder buf = new StringBuilder("{\"term\":{");
//		buf.append("\""+ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE+"\": \""+ITopicQuestsOntology.TYPE_TYPE+"\"},");
//		buf.append("\"from\":"+10+",\"size\":"+30+"}");
		r = database.runQuery(termQuery.toString(), 3, 2, credentials);
		System.out.println("Done "+r.getErrorString()+" "+r.getResultObject());
		environment.shutDown();
	}

}
