/**
 * 
 */
package test;

import java.util.*;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.IDataProvider;
import org.topicquests.model.api.INode;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.StatisticsUtility;
import org.topicquests.topicmap.json.model.api.ISocialBookmarkModel;

/**
 * @author park
 *
 */
public class SocialBookmarkTest {
	private JSONTopicmapEnvironment environment;
	private ISocialBookmarkModel model;
	private String userId;
	private Set<String>credentials;
	private IDataProvider database;
	private final String url = "http://foo.org/";
	private final String 
		TAG1 	= "Pretty funny",
		TAG2 	= "Nonsense!",
		TITLE 	= "My website",
		DETAILS = "Watch this space!";
	

	/**
	 * 
	 */
	public SocialBookmarkTest() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		model = environment.getBookmarkModel();
		database = environment.getDataProvider();
		//create credentials
		credentials = new HashSet<String>();
		userId = ITopicQuestsOntology.SYSTEM_USER;
		doTest();
	}

	void doTest() {
		IResult result = new ResultPojo();
		List<String>tags = new ArrayList<String>();
		tags.add(TAG1);
		tags.add(TAG2);
		IResult r = model.bookmark(url, TITLE, DETAILS, "en", userId, tags);
		INode n = (INode)r.getResultObject();
		System.out.println(n.toJSON());
		environment.shutDown();
	}
}
