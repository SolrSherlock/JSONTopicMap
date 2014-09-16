/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

import java.util.Date;
import java.util.List;

import org.topicquests.model.api.node.ICitation;

/**
 * @author park
 * <p>For creating scholarly citation topics</p>
 */
public interface ICitationModel {
	
	/**
	 * Create a new {@link ICitation}. Must continue to fillin details.
	 * @param publicationTypeLocator
	 * @param docTitle
	 * @param docAbstract
	 * @param language
	 * @param publicationTitle
	 * @param publisherLocator
	 * @param userLocator
	 * @param isPrivate
	 * @return
	 */
	ICitation newCitation(String publicationTypeLocator,String docTitle, String docAbstract, String language,
			String publicationTitle, String publisherLocator, String userLocator, boolean isPrivate);
	
	/**
	 * 
	 * @param locator
	 * @param publicationTypeLocator
	 * @param docTitle
	 * @param docAbstract
	 * @param language
	 * @param publicationTitle
	 * @param publisherLocator
	 * @param userLocator
	 * @param isPrivate
	 * @return
	 */
	ICitation newCitation(String locator, String publicationTypeLocator,String docTitle, String docAbstract, String language,
			String publicationTitle, String publisherLocator, String userLocator, boolean isPrivate);


}
