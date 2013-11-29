/*
 * Copyright 2012, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.topicmap.ui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
//import javax.swing.JToolBar;
//import javax.swing.JButton;
//import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.topicquests.common.api.IConsoleDisplay;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.util.TextFileHandler;
/**
 * @author Jack Park
 * @version 1.0
 */
public class MainFrame
    extends JFrame implements IConsoleDisplay {
	private JSONTopicmapEnvironment environment;
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JLabel statusBar = new JLabel();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JScrollPane consoleTab = new JScrollPane();
  JTextArea consoleArea = new JTextArea();
  JMenu solrMenu = new JMenu();
  JMenuItem importSolrItem = new JMenuItem();
  JMenuItem importCarrotFileItem = new JMenuItem();
  JMenuItem importCarrotDirItem = new JMenuItem();
  JMenuItem dumpIndexItem = new JMenuItem();
 // RdbmsManagerPanel rdbmsPanel = new RdbmsManagerPanel();
 // PhraseAnalysisTab analysisTab = new PhraseAnalysisTab();
 // SentenceEditorTab sentenceTab = new SentenceEditorTab();
 // WordGramEditorTab wordgramTab = new WordGramEditorTab();
//  StatsTab statsTab = new StatsTab();
  
  
  public MainFrame() {
    try {
    	//by setting default close to doNothing, we avoid the issue
    	//of quitting without calling shutDown.
    	//To exit this program, must use File:Exit
      setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      jbInit();
      environment = new JSONTopicmapEnvironment();
      environment.setConsole(this);
    //  analysisTab.init(environment);
   //   statsTab.init(environment);
   //   sentenceTab.init(environment);
    //  wordgramTab.init(environment);
    //  rdbmsPanel.setConnection(environment.getRdbmsDatabase().getConnection());
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Component initialization.
   *
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(800, 600));
    setTitle("JSON TopicMap");
    statusBar.setText(" ");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new MainFrame_jMenuFileExit_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new MainFrame_jMenuHelpAbout_ActionAdapter(this));
    consoleArea.setEditable(false);
    consoleArea.setText("");
    consoleArea.setLineWrap(true);
    solrMenu.setText("Text");
    solrMenu.add(importSolrItem);
    importSolrItem.setText("Import Document...");
    importSolrItem.addActionListener(new MainFrame_importSolrItem_actionAdapter(this));
    importCarrotFileItem.setText("Import Carrot2 File...");
    importCarrotFileItem.addActionListener(new MainFrame_importCarrotItem_actionAdapter(this));
    solrMenu.add(importCarrotFileItem);
    importCarrotDirItem.setText("Import Carrot2 Directory...");
    importCarrotDirItem.addActionListener(new MainFrame_importCarrotDirItem_actionAdapter(this));
    solrMenu.add(importCarrotDirItem);
    
    dumpIndexItem.setText("Dump Index");
    dumpIndexItem.addActionListener(new MainFrame_dumpIndexItem_actionAdapter(this));
    solrMenu.addSeparator();
    solrMenu.add(dumpIndexItem);
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuFileExit);
    jMenuBar1.add(solrMenu);
    jMenuBar1.add(jMenuHelp);
    jMenuHelp.add(jMenuHelpAbout);
    setJMenuBar(jMenuBar1);
//    jButton1.setIcon(image1);
//    jButton1.setToolTipText("Open File");
//    jButton2.setIcon(image2);
//    jButton2.setToolTipText("Close File");
//    jButton3.setIcon(image3);
//    jButton3.setToolTipText("Help");
 //   jToolBar.add(jButton1);
 //   jToolBar.add(jButton2);
 //   jToolBar.add(jButton3);
    contentPane.add(statusBar, BorderLayout.SOUTH);
//    contentPane.add(jToolBar, java.awt.BorderLayout.NORTH);
    contentPane.add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    jTabbedPane1.add(consoleTab, "Console");
   // jTabbedPane1.add(sentenceTab,"Sentence Editor");
  //  jTabbedPane1.add(wordgramTab, "WordGram Editor");
  //  jTabbedPane1.add(analysisTab, "Analysis");
  //  jTabbedPane1.add(statsTab, "Statistics");
  //  jTabbedPane1.add(rdbmsPanel, "Database");
    consoleTab.getViewport().add(consoleArea);
    toStatus("Hello");
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
	  environment.shutDown();
    System.exit(0);
  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  public void toConsole(String text) {
    consoleArea.append(text+"\n");
  }

  public void toStatus(String text) {
    statusBar.setText(text);
  }

  public void importSolrItem_actionPerformed(ActionEvent e) {
	  TextFileHandler h = new TextFileHandler();
	  File f = h.openFile();
	  if (f != null) {
//TODO		  utilityHandler.importFile(f);
	  }
  }
  public void importCarrotItem_actionPerformed(ActionEvent e) {
	  TextFileHandler h = new TextFileHandler();
	  File f = h.openFile();
	  if (f != null) {
	//	environment.importCarrot2File(f);
	  }
  }

  public void importCarrotDirItem_actionPerformed(ActionEvent e) {
	  TextFileHandler h = new TextFileHandler();
	  File f = h.openDirectory("Carrot2 XML Files");
	  if (f != null) {
//		environment.importCarrot2Directory(f);
	  }
  }
  public void dumpItem_actionPerformed(ActionEvent e) {
	//TODO	  utilityHandler.dumpIndex();
  }
}

class MainFrame_importSolrItem_actionAdapter
    implements ActionListener {
  private MainFrame adaptee;
  MainFrame_importSolrItem_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.importSolrItem_actionPerformed(e);
  }
}
class MainFrame_importCarrotItem_actionAdapter
		implements ActionListener {
	private MainFrame adaptee;
	MainFrame_importCarrotItem_actionAdapter(MainFrame adaptee) {
		this.adaptee = adaptee;
	}
	
	public void actionPerformed(ActionEvent e) {
		adaptee.importCarrotItem_actionPerformed(e);
	}
}
class MainFrame_importCarrotDirItem_actionAdapter
	implements ActionListener {
	private MainFrame adaptee;
	MainFrame_importCarrotDirItem_actionAdapter(MainFrame adaptee) {
	this.adaptee = adaptee;
	}
	
	public void actionPerformed(ActionEvent e) {
	adaptee.importCarrotDirItem_actionPerformed(e);
	}
}

class MainFrame_dumpIndexItem_actionAdapter
	implements ActionListener {
	private MainFrame adaptee;
	MainFrame_dumpIndexItem_actionAdapter(MainFrame adaptee) {
	this.adaptee = adaptee;
	}
	
	public void actionPerformed(ActionEvent e) {
	adaptee.dumpItem_actionPerformed(e);
	}
}

class MainFrame_jMenuFileExit_ActionAdapter
    implements ActionListener {
  MainFrame adaptee;

  MainFrame_jMenuFileExit_ActionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuFileExit_actionPerformed(actionEvent);
  }
}

class MainFrame_jMenuHelpAbout_ActionAdapter
    implements ActionListener {
  MainFrame adaptee;

  MainFrame_jMenuHelpAbout_ActionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
  }

}
