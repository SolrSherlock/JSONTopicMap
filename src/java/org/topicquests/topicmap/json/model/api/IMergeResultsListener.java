/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

/**
 * @author park
 *
 */
public interface IMergeResultsListener {

	/**
	 * Returned only if a merge was performed on
	 * <code>mergeLocatorA</code> and <code>mergeLocatorB</code>
	 * @param virtualNodeLocator
	 * @param mergedLocatorA
	 * @param mergedLocatorB
	 * @param errorMessages
	 */
	void acceptMergeResults(String virtualNodeLocator,
								  String mergedLocatorA,
								  String mergedLocatorB,
								  String errorMessages);
}
