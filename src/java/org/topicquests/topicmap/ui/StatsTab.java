package org.topicquests.topicmap.ui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.topicquests.model.api.IEnvironment;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: StoryReader Engine</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008 Jack Park</p>
 *
 * <p>Company: NexistGroup</p>
 *
 * @author Jack Park
 */
public class StatsTab extends JPanel {
	private JSONTopicmapEnvironment environment;
	private JButton refreshButton = new JButton("Refresh");
	private JTextArea statsArea = new JTextArea();
	
    public StatsTab() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void init(JSONTopicmapEnvironment env) {
    	environment = env;
    	this.refreshButton_actionPerformed();
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        JPanel p1 = new JPanel(new FlowLayout());
        this.add(p1, BorderLayout.NORTH);
        p1.add(refreshButton);
        refreshButton.addActionListener(new
                StatsTabRefreshButton_actionAdapter(this));
        this.add(statsArea, BorderLayout.CENTER);
        statsArea.setText("");
        
    }

    public void refreshButton_actionPerformed() {
    	statsArea.setText(environment.getStats().getStats());
    }
    
    BorderLayout borderLayout1 = new BorderLayout();
}
class StatsTabRefreshButton_actionAdapter
		implements ActionListener {
	private StatsTab adaptee;
	StatsTabRefreshButton_actionAdapter(StatsTab adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.refreshButton_actionPerformed();
	}
}

