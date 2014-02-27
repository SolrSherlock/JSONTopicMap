/**
 * 
 */
package org.topicquests.topicmap.json.merge;

import java.util.*;

import org.topicquests.common.api.IResult;
import org.topicquests.model.api.node.INode;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;
import org.topicquests.topicmap.json.model.api.IMergeResultsListener;
import org.topicquests.topicmap.json.model.api.IVirtualizer;

/**
 * @author park
 *
 */
public class VirtualizerHandler {
	private JSONTopicmapEnvironment environment;
	private IVirtualizer virtualizer;
	private Worker worker;
	
	/**
	 * 
	 */
	public VirtualizerHandler(JSONTopicmapEnvironment env) {
		environment = env;
		virtualizer = environment.getVirtualizer();
		worker = new Worker();
		worker.start();
	}
	
	public void performMerge(INode primary, INode merge,
			Map<String,Double> mergeData, double confidence,
			String userLocator, IMergeResultsListener listener) {
		worker.addWorkerObject(new WorkerObject(primary,merge,mergeData,
				confidence,userLocator,listener));
	}
	
	public void shutDown() {
		if (worker != null)
			worker.shutDown();
	}
	class WorkerObject {
		public INode primary, merge;
		public Map<String,Double>mergeData;
		public double confidence;
		public String userLocator;
		public IMergeResultsListener listener;
		
		public WorkerObject(INode primary, INode merge,
				Map<String,Double> mergeData, double confidence,
				String userLocator,IMergeResultsListener listener) {
			this.primary = primary;
			this.merge = merge;
			this.mergeData = mergeData;
			this.confidence = confidence;
			this.userLocator = userLocator;
			this.listener = listener;
		}
	}
	
	class Worker extends Thread {
		private List<WorkerObject>objects = new ArrayList<WorkerObject>();
		private boolean isRunning = true;
		
		public void addWorkerObject(WorkerObject o) {
			synchronized(objects) {
				objects.add(o);
				objects.notify();
			}
		}
		
		public void shutDown() {
			synchronized(objects) {
				isRunning = false;
				objects.notify();
			}
		}
		
		public void run() {
			WorkerObject theO = null;
			while (isRunning) {
				synchronized(objects) {
					if (objects.isEmpty()) {
						try {
							objects.wait();
						} catch (Exception e) {}
					}
					if (isRunning && !objects.isEmpty())
						theO = objects.remove(0);
				}
				if (isRunning && theO != null) {
					doIt(theO);
					theO = null;
				}
			}
		}
		
		void doIt(WorkerObject wo) {
			IResult r = virtualizer.createVirtualNode(wo.primary, wo.merge, 
					wo.mergeData, wo.confidence, wo.userLocator);
			if (wo.listener != null) {
				if (r.getResultObject() != null)
					wo.listener.acceptMergeResults((String)r.getResultObject(), wo.primary.getLocator(),
							wo.merge.getLocator(), r.getErrorString());
			}
		}
	}

}
