package guru.springframework.sfgjms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SfgJmsApplicationTests {

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private ObjectMapper objectMapper;
	@Test
	public void contextLoads() throws Exception {

		assertTrue(((HelloWorldMessage)jmsTemplate.receiveAndConvert(JmsConfig.MY_QUEUE)).getMessage().equals("Hello World!"));


		HelloWorldMessage message = HelloWorldMessage
				.builder()
				.id(UUID.randomUUID())
				.message("test")
				.build();

        Message receivedMessage = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE,  session -> {

		   Message helloMessage = null;

		   try {
			   helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
			   helloMessage.setStringProperty("_type", "guru.springframework.sfgjms.model.HelloWorldMessage");


			   return helloMessage;

		   } catch (JsonProcessingException e) {
			   throw new JMSException("boom");
		   }

	   });

        assertTrue((receivedMessage.getBody(String.class).contains("test World!!")));
    }

}
