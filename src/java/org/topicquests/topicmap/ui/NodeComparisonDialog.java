package org.topicquests.topicmap.ui;

import javax.swing.*;

import java.awt.*;
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
public class NodeComparisonDialog
    extends JDialog {
	private SuggestedMergeTab host;
	
  public NodeComparisonDialog() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setHost(SuggestedMergeTab h) {
	  host = h;
  }
  
  public void setNodes(String leftXML, String rightXML) {
	  this.leftArea.setText(leftXML);
	  this.rightArea.setText(rightXML);
	  this.setVisible(true);
  }
  
  private void jbInit() throws Exception {
    this.setTitle("Node Comparison Dialog");
    this.getContentPane().setLayout(borderLayout1);
    rightArea.setLineWrap(true);
    leftArea.setLineWrap(true);
    mergeButton.setToolTipText("Merge the two nodes");
    mergeButton.setText("Merge");
    mergeButton.addActionListener(new
        NodeComparisonDialog_mergeButton_actionAdapter(this));
    flowLayout1.setAlignment(FlowLayout.LEFT);
    noMergeButton.setToolTipText("Do not merge the two nodes");
    noMergeButton.setText("No Merge");
    noMergeButton.addActionListener(new
        NodeComparisonDialog_noMergeButton_actionAdapter(this));
    jLabel1.setText("Reason: ");
    reasonField.setPreferredSize(new Dimension(240, 20));
    jPanel3.setLayout(flowLayout2);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new
        NodeComparisonDialog_cancelButton_actionAdapter(this));
    jPanel1.add(mergeButton);
    jPanel1.add(noMergeButton);
    jPanel1.add(jLabel1);
    jPanel1.add(reasonField);
    gridLayout1.setColumns(2);
    jPanel2.setLayout(gridLayout1);
    this.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);
    leftArea.setText("");
    rightArea.setText("");
    jPanel2.add(jScrollPane1);
    jScrollPane1.getViewport().add(leftArea);
    jPanel2.add(jScrollPane2);
    jScrollPane2.getViewport().add(rightArea);
    this.getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);
    jPanel3.add(cancelButton);

    this.getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);
    jPanel1.setLayout(flowLayout1);
    // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    this.setLocation( (screenSize.width - frameSize.width) / 2,
                      (screenSize.height - frameSize.height) / 2);
  }

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel2 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextArea leftArea = new JTextArea();
  JTextArea rightArea = new JTextArea();
  JButton mergeButton = new JButton();
  JButton noMergeButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JTextField reasonField = new JTextField();
  JPanel jPanel3 = new JPanel();
  FlowLayout flowLayout2 = new FlowLayout();
  JButton cancelButton = new JButton();
  
  public void mergeButton_actionPerformed(ActionEvent e) {
	  String reason = this.reasonField.getText();
	  if (reason.equals("")) {
		  JOptionPane.showMessageDialog(this, "Please enter a Reason for the merge");
	  }
	  else {
		  host.doTheMerge(reason);
		  this.setVisible(false);
	  }
	  
  }

  public void noMergeButton_actionPerformed(ActionEvent e) {
	  String reason = this.reasonField.getText();
	  if (reason.equals("")) {
		  JOptionPane.showMessageDialog(this, "Please enter a Reason for the merge");
	  }
	  else {
		  host.doNoMerge(reason);
		  this.setVisible(false);
	  }
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
	  this.setVisible(false);
  }
}

class NodeComparisonDialog_cancelButton_actionAdapter
    implements ActionListener {
  private NodeComparisonDialog adaptee;
  NodeComparisonDialog_cancelButton_actionAdapter(NodeComparisonDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}

class NodeComparisonDialog_noMergeButton_actionAdapter
    implements ActionListener {
  private NodeComparisonDialog adaptee;
  NodeComparisonDialog_noMergeButton_actionAdapter(NodeComparisonDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.noMergeButton_actionPerformed(e);
  }
}

class NodeComparisonDialog_mergeButton_actionAdapter
    implements ActionListener {
  private NodeComparisonDialog adaptee;
  NodeComparisonDialog_mergeButton_actionAdapter(NodeComparisonDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.mergeButton_actionPerformed(e);
  }
}
