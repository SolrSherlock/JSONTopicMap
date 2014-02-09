package org.topicquests.topicmap.ui;

import javax.swing.*;

import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.model.TicketPojo;
import org.topicquests.model.api.ITicket;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.SearchEnvironment;
import org.topicquests.topicmap.json.model.api.IExtendedEnvironment;

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
public class SearchTab
    extends JPanel {
	private SearchEnvironment host;
	private ITicket credentials;
	private int start = 0; //TODO we need to be able to page
	private int count = 50;
	
  public SearchTab() {
    try {
      jbInit();
      credentials = new TicketPojo(ITopicQuestsOntology.SYSTEM_USER);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public void setEnvironment(IExtendedEnvironment e) {
	  host = e.getSearchEnvironment();
	  if (host == null)
		  throw new RuntimeException("SearchTab null SearchEnvironment");
  }
  
	public void addSearchHit(String hit) {
		this.hitArea.append(hit+"\n");
	}
	
	public void setSearchHits(String allHist) {
		this.hitArea.setText(allHist);
	}


  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    searchField.setPreferredSize(new Dimension(200, 20));
    searchField.setText("");
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    labelButton.setToolTipText("Search on labels");
    labelButton.setText("Label");
    labelButton.addActionListener(new SearchTab_labelButton_actionAdapter(this));
    keywordButton.setToolTipText("Search on keywords");
    keywordButton.setText("Keywords");
    keywordButton.addActionListener(new SearchTab_keywordButton_actionAdapter(this));
    hitArea.setEnabled(false);
    hitArea.setText("");
    hitArea.setLineWrap(true);
    jPanel1.add(searchField);
    jPanel1.add(labelButton);
    jPanel1.add(keywordButton);
    jScrollPane1.getViewport().add(hitArea);
    this.add(jScrollPane1, java.awt.BorderLayout.CENTER);
    this.add(jPanel1, java.awt.BorderLayout.NORTH);
  }

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextField searchField = new JTextField();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton labelButton = new JButton();
  JButton keywordButton = new JButton();
  JTextArea hitArea = new JTextArea();
  
  public void labelButton_actionPerformed(ActionEvent e) {
	  String language = "en"; //TODO fix this
	  String q = searchField.getText();
	  hitArea.setText("");
	  if (!q.equals(""))
		  host.acceptLabelQuery(q,language,start,count,credentials);
  }

  public void keywordButton_actionPerformed(ActionEvent e) {
	  String language = "en"; //TODO fix this
	  String q = searchField.getText();
	  hitArea.setText("");
	  if (!q.equals(""))
		  host.acceptKeywordQuery(q,language,start,count,credentials);
  }
}

class SearchTab_keywordButton_actionAdapter
    implements ActionListener {
  private SearchTab adaptee;
  SearchTab_keywordButton_actionAdapter(SearchTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.keywordButton_actionPerformed(e);
  }
}

class SearchTab_labelButton_actionAdapter
    implements ActionListener {
  private SearchTab adaptee;
  SearchTab_labelButton_actionAdapter(SearchTab adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.labelButton_actionPerformed(e);
  }
}
