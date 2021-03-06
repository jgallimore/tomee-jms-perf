package com.github.exabrial.tomeejmsperf;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "maxSessions", propertyValue = "8"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "com.github.exabrial.tomeejmsperf") })
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestMDB implements MessageListener {
	@Inject
	private PerfTestRunner runner;
	@Inject
	private Session session;

	@Override
	public void onMessage(Message message) {
		try {
			Destination destination = session.createQueue("com.github.exabrial.tomeejmsperf");
			MessageProducer producer = session.createProducer(destination);
			TextMessage egressMessage = session.createTextMessage(message.getBody(String.class));
			producer.send(egressMessage);
			runner.increment();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
