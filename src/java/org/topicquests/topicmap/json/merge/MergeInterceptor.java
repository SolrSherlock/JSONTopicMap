/*
 * Copyright 2013, TopicQuests
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
package org.topicquests.topicmap.json.merge;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import org.topicquests.model.api.node.INode;
import org.topicquests.util.ConfigurationHelper;

/**
 * @author park
 * <p>This behaves as an <em>interceptor</em> to send out
 * {@link INode} objects for merge
 * </p>
 */
public class MergeInterceptor {
	private final String agentTag = "NewDocument";
	private int myPort;
	private Properties p = new Properties();
	private boolean isRunning = true;
	private Worker thread;

	/**
	 * 
	 */
	public MergeInterceptor() throws Exception {
		//file must be in classpath
		File f = new File(ConfigurationHelper.findPath("agents.properties"));
		FileInputStream fis = new FileInputStream(f);
		p.load(fis);
		fis.close();
		String portx = p.getProperty("port");
		myPort = Integer.parseInt(portx);
		thread = new Worker();
	}
	
	public void acceptNodeForMerge(INode node) {
		synchronized(thread) {
			thread.addDocument(node);
			thread.notify();
		}
	}

	public void shutDown() {
		synchronized(thread) {
			isRunning = false;
			thread.notify();
		}
	}

	/**
	 * Send the data
	 * @param data
	 */
	void serveData(String data) {
		ServerSocket srvr = null;
		Socket skt = null;
	    try {
	        srvr = new ServerSocket(myPort);
	        //java.net.BindException: Address already in use: JVM_Bind
	        System.out.println("DocumentProcessor socket "+srvr);
	        skt = srvr.accept();
	        System.out.print("Server has connected!\n");
	        PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
	        System.out.print("Sending string: '" + data + "'\n");
	        out.print(data);
	        out.flush();
	        out.close();
	    }
	    catch(Exception e) {
	    	//NOTE: this especially happens if there is no listener running
	        System.out.print("Whoops! MergeInterceptor didn't work!\n");
	        e.printStackTrace();
	        //TODO figure out how to get this into Solr's logging system
	    } finally {
	    	try {
	    		if (skt != null)
	    			skt.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	try {
	    		if (srvr != null)
	    			srvr.close();
	    	} catch (Exception x) {
	    		x.printStackTrace();
	    	}

	    }
	}
	
	class Worker extends Thread {
		private List<INode>documents;

		Worker() {
			documents = new ArrayList<INode>();
			this.start();
		}
		
		public void halt() {
			synchronized(documents) {
				isRunning = false;
				documents.notify();
			}
		}
		public void addDocument(INode doc) {
			synchronized(documents) {
				documents.add(doc);
				documents.notify();
			}
		}
		public void run() {
			INode theDoc = null;
			while (isRunning) {
				synchronized(documents) {
					if (documents.isEmpty()) {
						try {
							documents.wait();
						} catch (Exception e) {}
					}
					if (isRunning && !documents.isEmpty()) {
						theDoc = documents.remove(0);
					}
				}
				if (isRunning && theDoc != null) {
					serveData(theDoc.toJSON());
					theDoc = null;
				}
			}
		}
	}
}
