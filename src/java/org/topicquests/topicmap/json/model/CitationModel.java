/**
 * 
 */
package org.topicquests.topicmap.json.model;

import org.topicquests.common.api.ICoreIcons;
import org.topicquests.model.api.node.ICitation;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.topicmap.json.model.api.ICitationModel;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;

/**
 * @author park
 *
 */
public class CitationModel implements ICitationModel {
	private JSONTopicmapEnvironment environment;
	private INodeModel nodeModel;

	/**
	 * 
	 */
	public CitationModel(JSONTopicmapEnvironment env, INodeModel nm) {
		environment = env;
		nodeModel = nm;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.ICitationModel#newCitation(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public ICitation newCitation(String publicationTypeLocator,
			String docTitle, String docAbstract, String language,
			String publicationTitle, String publisherLocator,
			String userLocator, boolean isPrivate) {
		ICitation result = (ICitation)nodeModel.newInstanceNode(publicationTypeLocator, docTitle, docAbstract, language, userLocator, 
				ICoreIcons.PUBLICATION_SM, ICoreIcons.PUBLICATION, isPrivate);
		result.setPublisherLocator(publisherLocator);
		result.setJournalTitle(publicationTitle);
		return result;
	}

	@Override
	public ICitation newCitation(String locator, String publicationTypeLocator,
			String docTitle, String docAbstract, String language,
			String publicationTitle, String publisherLocator,
			String userLocator, boolean isPrivate) {
		ICitation result = (ICitation)nodeModel.newInstanceNode(locator,publicationTypeLocator, docTitle, docAbstract, language, userLocator, 
				ICoreIcons.PUBLICATION_SM, ICoreIcons.PUBLICATION, isPrivate);
		result.setPublisherLocator(publisherLocator);
		result.setJournalTitle(publicationTitle);
		return result;
	}


}
