/**
 * 
 */
package org.topicquests.topicmap.json.mp;
import org.apache.qpid.proton.ProtonFactory;
import org.apache.qpid.proton.ProtonFactoryImpl;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.message.Message;
import org.apache.qpid.proton.message.impl.MessageFactoryImpl;
import org.apache.qpid.proton.message.impl.MessageImpl;
import org.apache.qpid.proton.messenger.Messenger;
import org.apache.qpid.proton.messenger.impl.MessengerFactoryImpl;
import org.apache.qpid.proton.messenger.impl.MessengerImpl;
import org.topicquests.topicmap.json.model.JSONTopicmapEnvironment;

/**
 * @author park
 *
 */
public class AMPQHandler {
	private JSONTopicmapEnvironment environment;
	private Messenger messenger;
	private MessageFactoryImpl messageFactory;

	/**
	 * 
	 */
	public AMPQHandler(JSONTopicmapEnvironment env) {
		environment = env;
		MessengerFactoryImpl f = new MessengerFactoryImpl();
		messenger = f.createMessenger();
		messageFactory = new MessageFactoryImpl();
	}

	//TODO create an API
}
