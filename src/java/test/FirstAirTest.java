/**
 * 
 */
package test;

import java.util.List;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.ITicket;
import org.topicquests.model.api.node.IAddressableInformationResource;
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
public class FirstAirTest {
	private JSONTopicmapEnvironment environment;
	private IJSONDocStoreModel jsonModel;
	private INodeModel nodeModel;
	private IJSONTopicDataProvider database;
	private ITicket credentials;
	private String userId;
	private String lang = "en";

	/**
	 * 
	 */
	public FirstAirTest() {
		environment = new JSONTopicmapEnvironment(new StatisticsUtility());
		jsonModel = environment.getJSONModel();
		nodeModel = environment.getDataProvider().getNodeModel();
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		//create credentials
		credentials = nodeModel.getDefaultCredentials(ITopicQuestsOntology.SYSTEM_USER);
		userId = ITopicQuestsOntology.SYSTEM_USER;	
		runTest();
		environment.shutDown();
		System.out.println("finis");
	}
	
	void runTest() {
		//newNode(String label, String description, String lang, String userId, 
		//  String smallImagePath, String largeImagePath, boolean isPrivate
		INode n = nodeModel.newNode("My Node", "Fun, what?", "en", userId, null, null, false);
		//newAIR(String locator, String subject, String body, String language,
		//String userId, boolean isPrivate);
		IAddressableInformationResource a = nodeModel.newAIR(null, "My very first AIR", "This would be the body", 
				"en", userId, false);
		n.addAIR(a);
		//We had a concurrentmodificationexception possibly when asking n.getLocator below
		//while at the same time, putNode fired up mergeInterceptor which asked for this node as json.
		// so, this experiment: fetch the locator now.
		String lox = n.getLocator();
		IResult r = database.putNode(n);
		System.out.println("AAA "+r.getErrorString()+"\n"+n.toXML());
		r = database.putAIRVersion(a);
		System.out.println("BBB "+r.getErrorString()+"\n"+a.toXML());
		r = database.getNode(lox, credentials);
//		r = database.getNode(n.getLocator(), credentials);
		System.out.println("CCC "+r.getErrorString()+" "+r.getResultObject());
		n = (INode)r.getResultObject();
		System.out.println("DDD "+n.toXML());
		System.out.println("EEE \n"+n.toJSON());
		System.out.println("FFF "+((List<IAddressableInformationResource>)n.listAIRs().getResultObject()).get(0).toJSON());
	}
/**
<node locator="0da20fbd-c744-4490-8ad3-7cb50aadc7d1">
  <property key="details" >
    <value><![CDATA[This would be the body]]></value>
  </property>
  <property key="creatorId" >
    <value><![CDATA[SystemUser]]></value>
  </property>
  <property key="AirModelPropertyType" >
  <property key="AirHostPropertyType" >
    <value><![CDATA[AirHostPropertyType]]></value>
  </property>
  </property>
  <property key="label" >
    <value><![CDATA[My very first AIR]]></value>
  </property>
  <property key="lastEditDate" >
    <value><![CDATA[2014-02-26 08:43:56]]></value>
  </property>
  <property key="createdDate" >
    <value><![CDATA[2014-02-26 08:43:56]]></value>
  </property>
  <property key="TransitiveClosureListPropertyType" >
  </property>
</node>

<node locator="03ed1146-ce47-4f78-84e3-88fcaff41b3e">
  <property key="AirListPropertyType" >
    <value><![CDATA[{"locator":"0da20fbd-c744-4490-8ad3-7cb50aadc7d1","details":["This would be the body"],"creatorId":"SystemUser","AirModelPropertyType":{"AirHostPropertyType":{}},"label":["My very first AIR"],"lastEditDate":"2014-02-26 08:43:56","createdDate":"2014-02-26 08:43:56","TransitiveClosureListPropertyType":[]}]]></value>
  </property>
  <property key="details" >
    <value><![CDATA[Fun, what?]]></value>
  </property>
  <property key="isPrivate" >
    <value><![CDATA[false]]></value>
  </property>
  <property key="creatorId" >
    <value><![CDATA[SystemUser]]></value>
  </property>
  <property key="label" >
    <value><![CDATA[My Node]]></value>
  </property>
  <property key="lastEditDate" >
    <value><![CDATA[2014-02-26 08:43:56]]></value>
  </property>
  <property key="createdDate" >
    <value><![CDATA[2014-02-26 08:43:56]]></value>
  </property>
  <property key="TransitiveClosureListPropertyType" >
  </property>
</node>

{
    "locator": "70d4b0e7-4f23-488f-9941-8a320aabb660",
    "details": [
        "This would be the body"
    ],
    "creatorId": "SystemUser",
    "instanceOf": "AirType",
    "label": [
        "My very first AIR"
    ],
    "AirModelPropertyType": {
        "03a0e3b7-8875-46f3-9e38-6e22374bf4c5": {
            "PurpleNumberPropertyType": "1"
        }
    },
    "lastEditDate": "2014-02-26 15:09:23",
    "createdDate": "2014-02-26 15:09:23",
    "TransitiveClosureListPropertyType": [
        "TypeType",
        "ClassType",
        "NodeType",
        "AirType"
    ]
} */
}
