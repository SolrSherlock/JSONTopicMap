package org.topicquests.topicmap.ui;

import javax.swing.*;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.topicmap.json.model.api.IMergeResultsListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SuggestedMergeTab
    extends JPanel {
	private NodeComparisonDialog compareDialog;
	private DefaultListModel listModel;
	//HOT THREAD SAFE
	private String theNodes;
	private INode leftNode, rightNode;
	private IJSONTopicDataProvider database;
	private ITicket credentials;
	
  public SuggestedMergeTab() {
    try {
      jbInit();
      listModel = new DefaultListModel();
      this.nodePairList.setModel(listModel);
      compareDialog = new NodeComparisonDialog();
      compareDialog.setHost(this);
      credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setDataProvider(IJSONTopicDataProvider dp) {
	  database = (IJSONTopicDataProvider)dp;
  }
  
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jPanel1.setLayout(flowLayout1);
    nodePairList.addMouseListener(new
                                  SuggestedMergeTab_nodePairList_mouseAdapter(this));
    examineButton.setEnabled(false);
    examineButton.setToolTipText("Study the selected Node Pair");
    examineButton.setText("Study");
    examineButton.addActionListener(new
        SuggestedMergeTab_examineButton_actionAdapter(this));
    removeButton.setEnabled(false);
    removeButton.setToolTipText("Remove selected Node Pair");
    removeButton.setText("Remove");
    removeButton.addActionListener(new
                                   SuggestedMergeTab_removeButton_actionAdapter(this));
    this.add(jPanel1, java.awt.BorderLayout.NORTH);
    jPanel1.add(examineButton);
    jPanel1.add(removeButton);
    this.add(jScrollPane1, java.awt.BorderLayout.CENTER);
    jScrollPane1.getViewport().add(nodePairList);
  }

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList nodePairList = new JList();
  JButton examineButton = new JButton();
  JButton removeButton = new JButton();
  
  public void addNodeLocatorPair (String leftNodeLocator, String rightNodeLocator) {
	  synchronized(nodePairList) {
		  listModel.addElement(leftNodeLocator+" "+rightNodeLocator);
		  nodePairList.repaint();
	  }
  }
  
  void enableButtons(boolean t) {
	  this.examineButton.setVisible(t);
	  this.removeButton.setVisible(t);
  }
  
  public void nodePairList_mouseClicked(MouseEvent e) {
	  this.theNodes = (String)nodePairList.getSelectedValue();
	  enableButtons(true);
  }
  
  /**
   * A user has asserted that two nodes are to be merged given <code>reason</code>
   * @param reason
   */
  public void doTheMerge(String reason) {
	  IMergeResultsListener mergeListener = null; //no need to send in a listener
	  database.mergeTwoNodes(leftNode, rightNode, reason, ITopicQuestsOntology.SYSTEM_USER, mergeListener);
	  listModel.removeElement(theNodes);	  
  }
  
  public void doNoMerge(String reason) {
	  //punt, for now
	  //TODO, we really ought to think about a way to represent
	  // a NotThoughtToBeMerged assertion
	  listModel.removeElement(theNodes);	  
  }

  /**
   * Task is to take <code>theNodes</code> and fetch the two
   * topic map {@link INode} instances as XML and send them
   * to the {@link NodeComparisonDialog}
   * @param e
   */
  public void examineButton_actionPerformed(ActionEvent e) {
	  String [] x = theNodes.split(" ");
	  String leftLocator = x[0].trim();
	  String rightLocator = x[1].trim();
	  IResult r = database.getNode(leftLocator, credentials);
	  leftNode = (INode)r.getResultObject();
	  r = database.getNode(rightLocator, credentials);
	  rightNode = (INode)r.getResultObject();
	  compareDialog.setNodes(leftNode.toXML(), rightNode.toXML());
  }

  public void removeButton_actionPerformed(ActionEvent e) {
	  listModel.removeElement(theNodes);
  }
}

class SuggestedMergeTab_removeButton_actionAdapter
    implements ActionListener {
  private SuggestedMergeTab adaptee;
  SuggestedMergeTab_removeButton_actionAdapter(SuggestedMergeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.removeButton_actionPerformed(e);
  }
}

class SuggestedMergeTab_examineButton_actionAdapter
    implements ActionListener {
  private SuggestedMergeTab adaptee;
  SuggestedMergeTab_examineButton_actionAdapter(SuggestedMergeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.examineButton_actionPerformed(e);
  }
}

class SuggestedMergeTab_nodePairList_mouseAdapter
    extends MouseAdapter {
  private SuggestedMergeTab adaptee;
  SuggestedMergeTab_nodePairList_mouseAdapter(SuggestedMergeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseClicked(MouseEvent e) {
    adaptee.nodePairList_mouseClicked(e);
  }
}
