package fr.epsi.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import fr.epsi.service.order.dto.ProductCommandDto;
import fr.epsi.service.order.exceptions.OrderPublishingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderPublisher {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderPublisher(final PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publishOrderIds(List<ProductCommandDto> commands) throws OrderPublishingException {
        try {
            String json = objectMapper.writeValueAsString(commands);
            pubSubTemplate.publish("order-created", json);
        } catch (JsonProcessingException e) {
            throw new OrderPublishingException("Erreur lors de la sérialisation Pub/Sub", e);
        }
    }
}
