/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

import javax.swing.JPanel;

import org.topicquests.common.api.IConsoleDisplay;
import org.topicquests.model.api.IEnvironment;
import org.topicquests.topicmap.ui.SearchTab;
import org.topicquests.topicmap.ui.SuggestedMergeTab;

/**
 * @author park
 *
 */
public interface IExtendedConsoleDisplay extends IConsoleDisplay {

	/**
	 * Make the {@link IConsoleDisplay} extensible by adding
	 * standAlone <code>tab</code> objects
	 * @param name
	 * @param tab
	 */
	void addStandaloneTab(String name, JPanel tab);
	
	/**
	 * Add other {@link IEnvironment} objects for shutting down
	 * @param e
	 */
	void addShutDownEnvironments(IEnvironment e);
	
	/**
	 * Allow to set the <code>title</code>
	 * @param title
	 */
	void setConsoleTitle(String title);
	
	/**
	 * Returns this tab
	 * @return can return <code>null</code>
	 */
	SuggestedMergeTab getSuggestedMergeTab();
	
	/**
	 * Returns this tab
	 * @return
	 */
	SearchTab getSearchTab();
	
}
