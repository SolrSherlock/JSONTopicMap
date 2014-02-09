package org.topicquests.topicmap.ui;

import javax.swing.*;

import org.topicquests.topicmap.json.model.api.IJSONDataProvider;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

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
public class TextEditorDialog
    extends JDialog {
	private IEditorDialogListener host;
	
  public TextEditorDialog() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setListener(IEditorDialogListener l) {
	  host = l;
  }
  
  private void jbInit() throws Exception {
    this.setTitle("Editor");
    this.setSize(new Dimension(600, 600));
    this.setLayout(borderLayout1);
    saveButton.setText("Save");
    saveButton.addActionListener(new TextEditorTab_saveButton_actionAdapter(this));
    jPanel1.setLayout(flowLayout1);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new TextEditorTab_cancelButton_actionAdapter(this));
    editArea.setText("");
    editArea.setLineWrap(true);
    editArea.addKeyListener(new TextEditorTab_editArea_keyAdapter(this));
    this.add(jPanel1, java.awt.BorderLayout.SOUTH);
    jPanel1.add(saveButton);
    jPanel1.add(cancelButton);
    this.add(jScrollPane1, java.awt.BorderLayout.CENTER);
    jScrollPane1.getViewport().add(editArea);
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
  
  boolean saveEnabled = false;
  
  public void setTextToEdit(String text) {
	  saveButton.setEnabled(false);
	  saveEnabled = false;
	  editArea.setText(text);
	  this.setVisible(true);
  }

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JButton saveButton = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton cancelButton = new JButton();
  JTextArea editArea = new JTextArea();
  
  public void saveButton_actionPerformed(ActionEvent e) {
	  host.acceptText(editArea.getText());
	  this.setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
	  this.setVisible(false);
  }
  
  public void editArea_keyTyped(KeyEvent e) {
	  if (!saveEnabled) {
		  saveButton.setEnabled(true);
		  saveEnabled = true;
	  }
  }
}

class TextEditorTab_editArea_keyAdapter
    extends KeyAdapter {
  private TextEditorDialog adaptee;
  TextEditorTab_editArea_keyAdapter(TextEditorDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void keyTyped(KeyEvent e) {
    adaptee.editArea_keyTyped(e);
  }
}

class TextEditorTab_cancelButton_actionAdapter
    implements ActionListener {
  private TextEditorDialog adaptee;
  TextEditorTab_cancelButton_actionAdapter(TextEditorDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}

class TextEditorTab_saveButton_actionAdapter
    implements ActionListener {
  private TextEditorDialog adaptee;
  TextEditorTab_saveButton_actionAdapter(TextEditorDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.saveButton_actionPerformed(e);
  }
}
