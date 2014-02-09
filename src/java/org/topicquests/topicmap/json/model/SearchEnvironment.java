/**
 * 
 */
package org.topicquests.topicmap.json.model;

import java.util.*;

import org.topicquests.common.api.IResult;
import org.topicquests.model.api.IQueryIterator;
import org.topicquests.model.api.IQueryModel;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.api.IExtendedConsoleDisplay;
import org.topicquests.topicmap.json.model.api.IExtendedEnvironment;
import org.topicquests.topicmap.ui.SearchTab;
import org.topicquests.model.api.INode;
/**
 * @author park
 *
 */
public class SearchEnvironment {
	private IExtendedEnvironment environment;
	private SearchTab searchtab;
	private IQueryModel queryModel;
	
	
	/**
	 * 
	 */
	public SearchEnvironment(IExtendedEnvironment e) {
		environment = e;
		IExtendedConsoleDisplay mainframe = environment.getConsoleDisplay();
		if (mainframe == null)
			throw new RuntimeException("SearchEnvironment null console");
		searchtab = mainframe.getSearchTab();
		queryModel = environment.getQueryModel();
	}

	/**
	 * Use <code>query</code> to retrieve <em>hits</em> from the JSON database
	 * and return them to {@link SearchTab}
	 * @param query
	 * @param language
	 */
	public void acceptKeywordQuery(String query, String language, int start, int count, ITicket credentials) {
		System.out.println("Keywordquery: "+query);
		IQueryIterator r = queryModel.listNodesByLabel(query, language, count, credentials);
		IResult x = r.next();
		if (x.getResultObject() != null) {
			List<INode>nodes = (List<INode>)x.getResultObject();
			INode n;
			Iterator<INode>itr = nodes.iterator();
			StringBuilder buf = new StringBuilder();
			List<String>strings;
			boolean isFirst = true;
			int len = 0;
			while (itr.hasNext()) {
				n = itr.next();
				buf.append("Locator: ");
				buf.append(n.getLocator());
				buf.append("\nDetails: ");
				strings = n.listLabels();
				if (strings != null && !strings.isEmpty()) {
					len = strings.size();
					for (int i=0;i<len;i++) {
						if (!isFirst)
							buf.append(", ");
						else
							isFirst = false;
						buf.append(strings.get(i));
					}
				}
				buf.append("\n");
				searchtab.addSearchHit(buf.toString());
				buf.setLength(0);		
			}
		} else
			searchtab.addSearchHit("Nothing found");
	}
	
	/**
	 * Use <code>query</code> to retrieve <em>hits</em> from the JSON database
	 * and return them to {@link SearchTab}
	 * @param query
	 * @param language
	 */
	public void acceptLabelQuery(String query, String language, int start, int count, ITicket credentials) {
		System.out.println("Labelquery: "+query);
		IQueryIterator r = queryModel.listNodesByDetails(query, language, count, credentials);	
		IResult x = r.next();
		if (x.getResultObject() != null) {
			List<INode>nodes = (List<INode>)x.getResultObject();
			INode n;
			Iterator<INode>itr = nodes.iterator();
			StringBuilder buf = new StringBuilder();
			List<String>strings;
			boolean isFirst = true;
			int len = 0;
			while (itr.hasNext()) {
				n = itr.next();
				buf.append("Locator: ");
				buf.append(n.getLocator());
				buf.append("\nLabel: ");
				strings = n.listLabels();
				if (strings != null && !strings.isEmpty()) {
					len = strings.size();
					for (int i=0;i<len;i++) {
						if (!isFirst)
							buf.append(", ");
						else
							isFirst = false;
						buf.append(strings.get(i));
					}
				}
				buf.append("\n");
				searchtab.addSearchHit(buf.toString());
				buf.setLength(0);	
			}
		} else
			searchtab.addSearchHit("Nothing found");
	}

}
