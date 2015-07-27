/**
 * 
 */
package org.topicquests.topicmap.json.model.api;

import org.topicquests.common.api.IResult;
import org.topicquests.model.api.node.INode;

/**
 * @author park
 *
 */
public interface IRelationModel {

	IResult  cause(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  what(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  why(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  how(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  similar(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  analogous(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  notAnalogous(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  metaphor(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  agree(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  disagree(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  different(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  opposite(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  same(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  uses(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  implies(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  enables(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  improves(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  addresses(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  solves(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  prerequisite(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  impairs(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  prevents(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  proves(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  refutes(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  evidenceFor(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  evidenceAgainst(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  consistent(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  inconsistent(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  example(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  predicts(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  envisages(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  responds(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  related(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  hasRole(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  partOf(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	IResult  containedIn(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate);
	
    /** 
     * Given a <code>relationType</code>, select the appropriate function and return its results
     * @param sourceNode
     * @param targetNode
     * @param relationType
     * @param userLocator
     * @param isPrivate
     * @param callback signature(err,data)
     */
	IResult  createRelation(INode sourceNode, INode targetNode, String relationType, String userLocator, boolean isPrivate);
	
	
	
	
}
