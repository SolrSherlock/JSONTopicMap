package org.topicquests.topicmap.ui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.api.node.INode;
import org.topicquests.model.api.node.INodeModel;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.api.IJSONTopicDataProvider;
import org.topicquests.topicmap.json.model.api.ITreeNode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

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
public class DoubleTreeTab
    extends JPanel implements IEditorDialogListener {
	private JSONTopicmapEnvironment environment;
	private IJSONTopicDataProvider database;
	private INodeModel model;
	private ITicket credentials;
	private TreeModel leftModel;
	private TreeModel rightModel;
	private final int leftDepth = 2;
	private final int pageSize = 30;
	private TextEditorDialog editor = new TextEditorDialog();
	private String selectedNodeLocator = null;
	private String rightTreeRootLocator = null;
	private int rightTreeOffset = 0;
	private Stack<String>previousLocatorStack;
	private Stack<Integer>previousOffsetStack;

  public DoubleTreeTab() {
    try {
      jbInit();
      previousLocatorStack = new Stack<String>();
      previousOffsetStack = new Stack<Integer>();
      
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setEnvironment(JSONTopicmapEnvironment env) {
	  environment = env;
	  database = (IJSONTopicDataProvider)environment.getDataProvider();
	  model = database.getNodeModel();
	  populateLeftTree();
  }

  private void populateLeftTree() {
	  TopicTreeNode waiting = new TopicTreeNode("Loading...");
	  leftModel = new DefaultTreeModel(waiting);
	  leftTree.setModel(leftModel);

	  TopicTreeNode blank = new TopicTreeNode("Await selection from left tree");
	  rightModel = new DefaultTreeModel(blank);
	  rightTree.setModel(rightModel);

	  new PopulateWorker().start();
  }
  
  class PopulateWorker extends Thread {
	  public void run() {
		  IResult r = database.loadTree(ITopicQuestsOntology.TYPE_TYPE, leftDepth,0, pageSize, credentials);
		  ITreeNode root = (ITreeNode)r.getResultObject();
		  TopicTreeNode rt = nodeToTreeNode(root);
		  expandTree(rt,root);
		  leftModel = new DefaultTreeModel(rt);
		  leftTree.setModel(leftModel);
		  TopicTreeNode blank = new TopicTreeNode("Await selection from left tree");
		  rightModel = new DefaultTreeModel(blank);
		  rightTree.setModel(rightModel);
		  rightTreeOffset = 0;
		  nextButton.setEnabled(false);
		  previousButton.setEnabled(false);
		  previousLocatorStack.clear();
		  previousOffsetStack.clear();
		  
	  }
  }
  
  private void expandTree(TopicTreeNode root, ITreeNode rootNode) {
	  java.util.List<ITreeNode>kids = rootNode.listSubclassChildNodes();
	  Iterator<ITreeNode>itr;
	  ITreeNode snapper;
	  TopicTreeNode child;
	  if (kids != null) {
		  itr=kids.iterator();
		  while (itr.hasNext()) {
			  snapper = itr.next();
			  child = nodeToTreeNode(snapper);
			  root.add(child);
			  //now recurse
			  expandTree(child,snapper);
		  }
	  }
	  kids = rootNode.listInstanceChildNodes();
	  if (kids != null) {
		  itr=kids.iterator();
		  while (itr.hasNext()) {
			  snapper = itr.next();
			  child = nodeToTreeNode(snapper);
			  root.add(child);
			  //now recurse
			  expandTree(child,snapper);
		  }
	  }
  }
  private TopicTreeNode nodeToTreeNode(ITreeNode n) {
	  String s = n.getNodeLabel()+" | "+n.getNodeLocator();
	  TopicTreeNode result = new TopicTreeNode(s);
	  result.setLocator(n.getNodeLocator());
	  return result;
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    topPanel.setLayout(flowLayout1);
    leftTree.addMouseListener(new DoubleTreeTab_leftTree_mouseAdapter(this));
    rightTree.addMouseListener(new DoubleTreeTab_rightTree_mouseAdapter(this));
    jScrollPane1.setBorder(border2);
    newSubclassButton.setEnabled(false);
    newSubclassButton.setToolTipText(
        "Add a new Subclass to the selected topic");
    newSubclassButton.setText("New Subclass Topic");
    newSubclassButton.addActionListener(new DoubleTreeTab_newButton_actionAdapter(this));
    editButton.setEnabled(false);
    editButton.setToolTipText("Edit selected topic");
    editButton.setText("Edit");
    editButton.addActionListener(new DoubleTreeTab_editButton_actionAdapter(this));
    removeButton.setEnabled(false);
    removeButton.setToolTipText("Remove selected topic");
    removeButton.setText("Remove");
    removeButton.addActionListener(new DoubleTreeTab_removeButton_actionAdapter(this));
    newInstanceButton.setToolTipText("Add an instance to the selected topic");
    newInstanceButton.setText("New Instance Topic");
    newInstanceButton.addActionListener(new
        DoubleTreeTab_newInstanceButton_actionAdapter(this));
    languageCombo.setToolTipText("Select Language");
    refreshButton.setToolTipText("Refresh the Display");
    refreshButton.setText("Refresh");
    refreshButton.addActionListener(new
                                    DoubleTreeTab_refreshButton_actionAdapter(this));
    previousButton.setEnabled(false);
    previousButton.setBorder(BorderFactory.createEtchedBorder());
    previousButton.setToolTipText("Page to previous selections");
    previousButton.setText("Previous");
    previousButton.addActionListener(new
                                     DoubleTreeTab_previousButton_actionAdapter(this));
    nextButton.setBorder(BorderFactory.createEtchedBorder());
    nextButton.setToolTipText("Page to next available selections");
    nextButton.setText("Next");
    nextButton.setEnabled(false);
    nextButton.addActionListener(new DoubleTreeTab_nextButton_actionAdapter(this));
    this.add(topPanel, java.awt.BorderLayout.NORTH);
    topPanel.add(refreshButton);
    topPanel.add(languageCombo);
    topPanel.add(newSubclassButton);
    topPanel.add(newInstanceButton);
    topPanel.add(editButton);
    topPanel.add(removeButton);
    this.add(jSplitPane1, java.awt.BorderLayout.CENTER);
    jSplitPane1.add(jScrollPane1, JSplitPane.LEFT);
    jScrollPane1.getViewport().add(leftTree);
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(jScrollPane2, BorderLayout.CENTER);
    rightPanel.add(xPanel, BorderLayout.NORTH);
    xPanel.add(previousButton);
    xPanel.add(nextButton);

    jSplitPane1.add(rightPanel, JSplitPane.RIGHT);
    jScrollPane2.getViewport().add(rightTree);
    removeButton.setEnabled(false);
    enableButtons(false);
  }

  JPanel xPanel = new JPanel(new FlowLayout());
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel topPanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JSplitPane jSplitPane1 = new JSplitPane();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTree leftTree = new JTree();
  JTree rightTree = new JTree();
  TitledBorder titledBorder1 = new TitledBorder("");
  Border border1 = BorderFactory.createLineBorder(new Color(127, 157, 185), 2);
  Border border2 = new TitledBorder(border1, "UpperTypology");
  JButton newSubclassButton = new JButton();
  JButton editButton = new JButton();
  JButton removeButton = new JButton();
  JButton newInstanceButton = new JButton();
  JComboBox languageCombo = new JComboBox();
  JButton refreshButton = new JButton();
  JButton previousButton = new JButton();
  JButton nextButton = new JButton();
  
  void enableButtons(boolean t) {
	  newSubclassButton.setEnabled(t);
	  newInstanceButton.setEnabled(t);
	  editButton.setEnabled(t);
	//  removeButton.setEnabled(t);
  }

  public void leftTree_mouseClicked(MouseEvent e) {
	  int ct = e.getClickCount();
	  if (ct > 1) {
		  enableButtons(false);
		  if (leftTree.getSelectionPath() != null) {
			  Object o = leftTree.getSelectionPath().getLastPathComponent();
			  System.out.println("SEL "+o);
			  if (o != null) {
				  TopicTreeNode rt = (TopicTreeNode)o;
				  rightTreeRootLocator = rt.getLocator();
				  rightTreeOffset = 0;
				  IResult r = database.loadTree(rightTreeRootLocator, this.leftDepth,0, pageSize, credentials);
				  ITreeNode root = (ITreeNode)r.getResultObject();
				  rt = nodeToTreeNode(root);
				  expandTree(rt,root);
				  rightModel = new DefaultTreeModel(rt);
				  rightTree.setModel(rightModel);
				  this.selectedNodeLocator = null;
				  rightTreeOffset = rt.getChildCount();
				  nextButton.setEnabled((rightTreeOffset >= pageSize)); 
			  }
		  }
	  }
  }

  public void rightTree_mouseClicked(MouseEvent e) {
	  int ct = e.getClickCount();
	  if (ct > 1) {
		  enableButtons(false);
		  Object o = rightTree.getSelectionPath().getLastPathComponent();
		  System.out.println("SEL "+o);
		  if (o != null) {
			  this.previousLocatorStack.push(rightTreeRootLocator);
			  this.previousOffsetStack.push(rightTreeOffset);
			  System.out.println("SSSS "+rightTreeRootLocator+" "+rightTreeOffset);
			  previousButton.setEnabled(true);
			  TopicTreeNode rt = (TopicTreeNode)o;
			  rightTreeRootLocator = rt.getLocator();
			  System.out.println("XXXX "+rightTreeRootLocator);
			  rightTreeOffset = 0;
			  IResult r = database.loadTree(rightTreeRootLocator, this.leftDepth,0, pageSize, credentials);
			  ITreeNode root = (ITreeNode)r.getResultObject();
			  rt = nodeToTreeNode(root);
			  expandTree(rt,root);
			  rightModel = new DefaultTreeModel(rt);
			  rightTree.setModel(rightModel);
			  this.selectedNodeLocator = null;
			  rightTreeOffset = rt.getChildCount();
			  nextButton.setEnabled((rightTreeOffset >= pageSize)); 
		  }
	  } else {
		  System.out.println("DOUBLETREE "+rightTree.getSelectionPath());
		  if (rightTree.getSelectionPath() != null) {
			  enableButtons(true);
			  //can be null, not sure why, something about clicking to expand
			  //a node
			  Object o = rightTree.getSelectionPath().getLastPathComponent();
			  System.out.println("SEL "+o);
			  if (o != null) {
				  TopicTreeNode n = (TopicTreeNode)o;
				  selectedNodeLocator = n.getLocator();
			  }
		  }
	  }
  }

  public void newButton_actionPerformed(ActionEvent e) {
	  INode n = model.newSubclassNode(selectedNodeLocator, "CHANGE ME", "CHANGE ME", "en", ITopicQuestsOntology.SYSTEM_USER, "CHANGE OR REMOVE ME", "CHANGE OR REMOVE ME", false);
	  editor.setTextToEdit(n.toXML());
  }

  public void editButton_actionPerformed(ActionEvent e) {
	  IResult r = database.getNode(selectedNodeLocator, credentials);
	  INode nn = (INode)r.getResultObject();
	  String xml = nn.toXML();
	  editor.setTextToEdit(xml);
  }

  public void removeButton_actionPerformed(ActionEvent e) {

  }

  public void newInstanceButton_actionPerformed(ActionEvent e) {
	  INode n = model.newInstanceNode(selectedNodeLocator, "CHANGE ME", "CHANGE ME", "en", ITopicQuestsOntology.SYSTEM_USER, "CHANGE OR REMOVE ME", "CHANGE OR REMOVE ME", false);
	  editor.setTextToEdit(n.toXML());
  }

  public void refreshButton_actionPerformed(ActionEvent e) {
	  this.enableButtons(false);
	  this.nextButton.setEnabled(false);
	  this.previousButton.setEnabled(false);
	  this.populateLeftTree();
  }

  public void nextButton_actionPerformed(ActionEvent e) {
	  IResult r = database.loadTree(rightTreeRootLocator, this.leftDepth,0, pageSize, credentials);
	  ITreeNode root = (ITreeNode)r.getResultObject();
	  TopicTreeNode rt = nodeToTreeNode(root);
	  expandTree(rt,root);
	  int count = rt.getChildCount();
	  if ( count > 0) {
		  rightModel = new DefaultTreeModel(rt);
		  rightTree.setModel(rightModel);
		  this.selectedNodeLocator = null;
		  rightTreeOffset = rt.getChildCount();
		  nextButton.setEnabled((rightTreeOffset >= pageSize)); 
		  previousLocatorStack.push(rightTreeRootLocator);
		  previousOffsetStack.push(leftDepth);
		  previousButton.setEnabled(true);
		  nextButton.setEnabled((count >= pageSize));
	  } else 
		  nextButton.setEnabled(false);

  }

  public void previousButton_actionPerformed(ActionEvent e) {
	  if (previousLocatorStack.isEmpty()) {
		  previousButton.setEnabled(false);
		  return;
	  }
	  String lox = this.previousLocatorStack.pop();
	  Integer offset = this.previousOffsetStack.pop();
	  if (previousLocatorStack.isEmpty()) {
		  previousButton.setEnabled(false);
	  }
	  System.out.println("PREVIOUS "+lox+" "+offset);
	  IResult r = database.loadTree(lox, this.leftDepth,0, pageSize, credentials);
	  ITreeNode root = (ITreeNode)r.getResultObject();
	  TopicTreeNode rt = nodeToTreeNode(root);
	  expandTree(rt,root);
	  int count = rt.getChildCount();
	  System.out.println("AAAA "+count+" "+root.getNodeLocator());
	  if ( count > 0) {
		  rightModel = new DefaultTreeModel(rt);
		  rightTree.setModel(rightModel);
		  this.selectedNodeLocator = null;
		  rightTreeOffset = rt.getChildCount();
		  nextButton.setEnabled((rightTreeOffset >= pageSize)); 
		 // previousLocatorStack.push(rightTreeRootLocator);
		 // previousOffsetStack.push(leftDepth);
		  if (offset > 0)
			  previousButton.setEnabled(true);
		  nextButton.setEnabled((count >= pageSize));
	  } else 
		  nextButton.setEnabled(false);
	  

  }

	/**
	 * Callback from TextEditorDialog
	 * @param text
	 */
	@Override
	public void acceptText(String text) {
		System.out.println(text);
		database.updateNodeFromXML(text);
	}
}

class DoubleTreeTab_previousButton_actionAdapter
    implements ActionListener {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_previousButton_actionAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.previousButton_actionPerformed(e);
  }
}

class DoubleTreeTab_nextButton_actionAdapter
    implements ActionListener {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_nextButton_actionAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.nextButton_actionPerformed(e);
  }
}

class DoubleTreeTab_refreshButton_actionAdapter
    implements ActionListener {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_refreshButton_actionAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.refreshButton_actionPerformed(e);
  }
}

class DoubleTreeTab_newInstanceButton_actionAdapter
    implements ActionListener {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_newInstanceButton_actionAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.newInstanceButton_actionPerformed(e);
  }
}

class DoubleTreeTab_removeButton_actionAdapter
    implements ActionListener {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_removeButton_actionAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.removeButton_actionPerformed(e);
  }
}

class DoubleTreeTab_editButton_actionAdapter
    implements ActionListener {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_editButton_actionAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.editButton_actionPerformed(e);
  }
}

class DoubleTreeTab_newButton_actionAdapter
    implements ActionListener {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_newButton_actionAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.newButton_actionPerformed(e);
  }
}

class DoubleTreeTab_rightTree_mouseAdapter
    extends MouseAdapter {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_rightTree_mouseAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseClicked(MouseEvent e) {
    adaptee.rightTree_mouseClicked(e);
  }
}

class DoubleTreeTab_leftTree_mouseAdapter
    extends MouseAdapter {
  private DoubleTreeTab adaptee;
  DoubleTreeTab_leftTree_mouseAdapter(DoubleTreeTab adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseClicked(MouseEvent e) {
    adaptee.leftTree_mouseClicked(e);
  }
}
