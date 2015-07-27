/**
 * 
 */
package org.topicquests.topicmap.json.model;

import java.util.*;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.ICoreIcons;
import org.topicquests.common.api.INodeTypes;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.IEnvironment;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.ITicket;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.topicmap.json.model.api.IJSONTopicMapOntology;
import org.topicquests.topicmap.json.model.api.ISocialBookmarkLegend;
import org.topicquests.topicmap.json.model.api.ISocialBookmarkModel;
import org.topicquests.model.Node;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author park
 *
 */
public class SocialBookmarkModel implements ISocialBookmarkModel {
	private final String TAG_SUFFIX = "_TAG";
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private IJSONDocStoreModel jsonModel;
	private INodeModel nodeModel;
	private ITicket credentials;
//	private JSONParser parser;
	private final String
		//defined in jsonblobstore-props.xml
		TOPIC_INDEX		= IJSONTopicMapOntology.TOPIC_INDEX,
		CORE_TYPE		= IJSONTopicMapOntology.CORE_TYPE;
	/**
	 * 
	 */
	public SocialBookmarkModel(JSONTopicmapEnvironment env) {
		environment = env;
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		nodeModel = database.getNodeModel();
		jsonModel = environment.getJSONModel();
//		parser = new JSONParser();
		//We assume all tags are public, so any credentials will work
		credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ISocialBookmarkModel#init(org.topicquests.model.api.IEnvironment)
	 */
	@Override
	public IResult init(IEnvironment env) {
		// TODO Not used
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ISocialBookmarkModel#bookmark(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public IResult bookmark(String url, String title, String details,
			String language, String userId, List<String> tags) {
		IResult result = new ResultPojo();
		INode theBookmark = null;
		//See if the bookmark already exists:
		BoolQueryBuilder qba = QueryBuilders.boolQuery();
		QueryBuilder qb1 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, INodeTypes.BOOKMARK_TYPE);
		QueryBuilder qb2 = QueryBuilders.termQuery(ITopicQuestsOntology.RESOURCE_URL_PROPERTY, url);
		qba.must(qb1);
		qba.must(qb2);		
		environment.logDebug("SocialBookmarkModel.bookmark- "+qba.toString());
		IResult r =  jsonModel.runQuery(TOPIC_INDEX, qba, 0, -1, CORE_TYPE);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		environment.logDebug("SocialBookmarkModel.bookmark-1 "+r.getResultObject());
		
		if (r.getResultObject() != null && !((List<String>)r.getResultObject()).isEmpty()) {
			List<String>json = (List<String>)r.getResultObject();
			System.out.println("BOOKMARKS "+json);
			if (json != null && !json.isEmpty()) {
				try {
					JSONObject jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(json.get(0));
					theBookmark = new Node(jo);
				} catch (Exception e) {
					result.addErrorString(e.getMessage());
					environment.logError(e.getMessage(), e);
				}
			}
		} else {
			String t = title;
			if (t == null || t.equals(""))
				t = "Bookmark for "+url;
			theBookmark = nodeModel.newInstanceNode(INodeTypes.BOOKMARK_TYPE, t, details, language, userId, ICoreIcons.BOOKMARK_SM, ICoreIcons.BOOKMARK, false);
			environment.logDebug("SocialBookmarkModel.bookmark-2 "+qba.toString());
			database.putNode(theBookmark, true);
		}
		result.setResultObject(theBookmark);
		environment.logDebug("SocialBookmarkModel.bookmark-3 "+theBookmark.toJSON());
		INode tag;
		INode user;
		r = database.getNode(userId, credentials);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		user = (INode)r.getResultObject();
		environment.logDebug("SocialBookmarkModel.bookmark-4 "+theBookmark.toJSON());
		String theTuple;
		if (tags != null && !tags.isEmpty()) {
			Iterator<String>itr = tags.iterator();
			r = getOrCreateTag(itr.next(),language,userId);
			if (r.hasError())
				result.addErrorString(r.getErrorString());
			if (r.getResultObject() != null) {
				tag = (INode)r.getResultObject();
				//NOW time to wire relations
				//relateExistingNodes(INode sourceNode, INode targetNode, String relationTypeLocator, 
				  //String userId, String smallImagePath, String largeImagePath, boolean isTransclude, boolean isPrivate);
				//Tag/Bookmark
				environment.logDebug("SocialBookmarkModel.bookmark-5 "+tag+" "+theBookmark);
				r = nodeModel.relateExistingNodes(tag, theBookmark, ISocialBookmarkLegend.TAG_BOOKMARK_RELATION_TYPE, userId, ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false, false);
				if (r.hasError())
					result.addErrorString(r.getErrorString());
				theTuple = (String)r.getErrorString();
				//Tag/User
				environment.logDebug("SocialBookmarkModel.bookmark-6 "+tag+" "+user);
				 nodeModel.relateExistingNodes(tag, user, ISocialBookmarkLegend.TAG_USER_RELATION_TYPE, userId, ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false, false);
					if (r.hasError())
						result.addErrorString(r.getErrorString());
			}
		}
		//user/bookmark
		r = nodeModel.relateExistingNodes(user, theBookmark, ISocialBookmarkLegend.USER_BOOKMARK_RELATIONTYPE, userId, ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false, false);
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ISocialBookmarkModel#formTagLocator(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public String formTagLocator(String tag) {
		String result = tag;
		result  = result.replaceAll(" ", "_");
		return result+TAG_SUFFIX;
	}
	
	
	/**
	 * <p>Find the {@link INode} for this <code>tag</code>. If it doesn't
	 * exist, create it.</p>
	 * @param tag
	 * @param lang
	 * @param userId
	 * @return
	 */
	private IResult getOrCreateTag(String tag, String lang, String userId) {
		String lox = formTagLocator(tag);
		IResult result = database.getNode(lox, credentials);
		if (result.getResultObject() == null) {
			//newInstanceNode(String locator,String typeLocator,String label, String description, 
			//  String lang, String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
			INode n = nodeModel.newInstanceNode(lox, INodeTypes.TAG_TYPE, tag, "A tag with the label: "+tag, lang, userId, ICoreIcons.TAG_SM, ICoreIcons.TAG, false);
			database.putNode(n, true);
			result.setResultObject(n);
		}
		environment.logDebug("SocialBookmarkModel.getOrCreateTag+ "+result.getResultObject());		
		return result;
	}

}
