/**
 * 
 */
package org.topicquests.topicmap.json.model;

import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.model.api.ITicket;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.topicmap.json.model.api.IRelationModel;
import org.topicquests.common.api.IRelationsLegend;
import org.topicquests.common.api.ICoreIcons;

/**
 * @author park
 *
 */
public class RelationModel implements IRelationModel {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private INodeModel model;

	/**
	 * 
	 */
	public RelationModel(JSONTopicmapEnvironment env) {
		environment = env;
		database = (IJSONTopicDataProvider)environment.getDataProvider();
		model = database.getNodeModel();
	}

	/**
	 * Internal method to create relations
	 * @param sourceNode
	 * @param targetNode
	 * @param relationType
	 * @param userLocator
	 * @param smallIcon
	 * @param largeIcon
	 * @param isPrivate
	 * @return
	 */
	IResult _assert(INode sourceNode, INode targetNode,
			String relationType, String userLocator,
			String smallIcon, String largeIcon,
			boolean isPrivate) {
		IResult result = null;
		result = model.relateExistingNodes(sourceNode, targetNode, relationType, 
				userLocator, smallIcon, largeIcon, false, isPrivate);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#cause(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult cause(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.CAUSES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#what(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult what(INode sourceNode, INode targetNode, String userLocator,
			boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.EXPLAINS_WHAT_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#why(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult why(INode sourceNode, INode targetNode, String userLocator,
			boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.EXPLAINS_WHY_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#how(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult how(INode sourceNode, INode targetNode, String userLocator,
			boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.EXPLAINS_HOW_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#similar(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult similar(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_SIMILAR_TO_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#analogous(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult analogous(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_ANALOGOUS_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#notAnalogous(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult notAnalogous(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_NOT_ANALOGOUS_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#metaphor(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult metaphor(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_METAPHOR_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#agree(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult agree(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.AGREES_WITH_RELATION_TYPE, userLocator,
				ICoreIcons.PRO_SM, ICoreIcons.PRO, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#disagree(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult disagree(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.DISAGREES_WITH_RELATION_TYPE, userLocator,
				ICoreIcons.CON_SM, ICoreIcons.CON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#different(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult different(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_DIFFERENT_TO_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#opposite(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult opposite(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_OPPOSITE_OF_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#same(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult same(INode sourceNode, INode targetNode, String userLocator,
			boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_SAME_AS_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#uses(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult uses(INode sourceNode, INode targetNode, String userLocator,
			boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.USES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#implies(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult implies(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IMPLIES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#enables(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult enables(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.ENABLES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#improves(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult improves(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IMPROVES_ON_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#addresses(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult addresses(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.ADDRESSES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#solves(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult  solves(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.SOLVES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#prerequisite(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult prerequisite(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_PREREQUISITE_FOR_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#impairs(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult impairs(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IMPAIRS_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#prevents(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult prevents(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.PREVENTS_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#proves(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult proves(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.PROVES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#refutes(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult refutes(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.REFUTES_RELATION_TYPE, userLocator,
				ICoreIcons.CON_SM, ICoreIcons.CON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#evidenceFor(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult evidenceFor(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_EVIDENCE_FOR_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#evidenceAgainst(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult evidenceAgainst(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_EVIDENCE_AGAINST_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#consistent(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult consistent(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_CONSISTENT_WITH_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#inconsistent(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult inconsistent(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_INCONSISTENT_WITH_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#example(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public 	IResult  example(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_EXAMPLE_OF_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#predicts(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult predicts(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.PREDICTS_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#envisages(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult envisages(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.ENVISAGES_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#responds(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult responds(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.RESPONDS_TO_RELATION_TYPE, userLocator,
				ICoreIcons.POSITION_SM, ICoreIcons.POSITION, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#related(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult related(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_RELATED_TO_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	public 	IResult hasRole(INode sourceNode, INode targetNode, String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.HAS_ROLE_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#partOf(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult partOf(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_PART_OF_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#containedIn(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult containedIn(INode sourceNode, INode targetNode,
			String userLocator, boolean isPrivate) {
		return _assert(sourceNode,targetNode, IRelationsLegend.IS_CONTAINED_IN_RELATION_TYPE, userLocator,
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, isPrivate);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.topicmap.json.model.api.IRelationModel#createRelation(org.topicquests.model.api.node.INode, org.topicquests.model.api.node.INode, java.lang.String, java.lang.String, org.topicquests.model.api.ITicket, boolean)
	 */
	@Override
	public IResult createRelation(INode sourceNode, INode targetNode,
			String relationType, String userLocator, 
			boolean isPrivate) {
		IResult result = null;
		if (relationType.equals(IRelationsLegend.CAUSES_RELATION_TYPE))
			result = this.cause(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.EXPLAINS_WHAT_RELATION_TYPE))
			result = this.what(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.EXPLAINS_WHY_RELATION_TYPE))
			result = this.why(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.EXPLAINS_HOW_RELATION_TYPE))
			result = this.how(sourceNode, targetNode, userLocator, isPrivate);

		else if (relationType.equals(IRelationsLegend.IS_SIMILAR_TO_RELATION_TYPE))
			result = this.similar(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_ANALOGOUS_RELATION_TYPE))
			result = this.analogous(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_NOT_ANALOGOUS_RELATION_TYPE))
			result = this.notAnalogous(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_METAPHOR_RELATION_TYPE))
			result = this.metaphor(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.AGREES_WITH_RELATION_TYPE))
			result = this.agree(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.DISAGREES_WITH_RELATION_TYPE))
			result = this.disagree(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_DIFFERENT_TO_RELATION_TYPE))
			result = this.different(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_OPPOSITE_OF_RELATION_TYPE))
			result = this.opposite(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_SAME_AS_RELATION_TYPE))
			result = this.same(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.USES_RELATION_TYPE))
			result = this.uses(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IMPLIES_RELATION_TYPE))
			result = this.implies(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.ENABLES_RELATION_TYPE))
			result = this.enables(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IMPROVES_ON_RELATION_TYPE))
			result = this.improves(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.ADDRESSES_RELATION_TYPE))
			result = this.addresses(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.SOLVES_RELATION_TYPE))
			result = this.solves(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_PREREQUISITE_FOR_RELATION_TYPE))
			result = this.prerequisite(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IMPAIRS_RELATION_TYPE))
			result = this.impairs(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.PREVENTS_RELATION_TYPE))
			result = this.prevents(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.PROVES_RELATION_TYPE))
			result = this.proves(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.REFUTES_RELATION_TYPE))
			result = this.refutes(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_EVIDENCE_FOR_RELATION_TYPE))
			result = this.evidenceFor(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_EVIDENCE_AGAINST_RELATION_TYPE))
			result = this.evidenceAgainst(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_CONSISTENT_WITH_RELATION_TYPE))
			result = this.consistent(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_INCONSISTENT_WITH_RELATION_TYPE))
			result = this.inconsistent(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_EXAMPLE_OF_RELATION_TYPE))
			result = this.example(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.PREDICTS_RELATION_TYPE))
			result = this.predicts(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.ENVISAGES_RELATION_TYPE))
			result = this.envisages(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.RESPONDS_TO_RELATION_TYPE))
			result = this.responds(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_RELATED_TO_RELATION_TYPE))
			result = this.related(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.HAS_ROLE_RELATION_TYPE))
			result = this.hasRole(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_PART_OF_RELATION_TYPE))
			result = this.partOf(sourceNode, targetNode, userLocator, isPrivate);
		else if (relationType.equals(IRelationsLegend.IS_CONTAINED_IN_RELATION_TYPE))
			result = this.containedIn(sourceNode, targetNode, userLocator, isPrivate);
		else {
			result = new ResultPojo();
			result.addErrorString("RelationModel.createRelation bad relation: "+relationType);
		}
		
		return result;
	}

}
