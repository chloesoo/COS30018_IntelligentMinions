package part4;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DAAgent extends Agent {

    private int capacity = 10; // Capacity

    protected void setup() {
        // Add a behavior
        addBehaviour(new ReceiveMessageBehaviour());
    }

    private class ReceiveMessageBehaviour extends jade.core.behaviours.CyclicBehaviour {
        public void action() {
            // Receive messages
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                // Receive message from MasterRoutingAgent
                System.out.println("Received message from MasterRoutingAgent: " + msg.getContent());
                if (msg.getContent().equals("What is your capacity?")) {
                    // Respond with the capacity
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.AGREE);
                    reply.setContent("My capacity is: " + capacity);
                    send(reply);
                    System.out.println("Responded to MasterRoutingAgent: " + reply.getContent());
                } else if (msg.getContent().equals("Can you deliver now?")) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.AGREE);
                    reply.setContent("Yes, I can deliver now");
                    send(reply);
                    System.out.println("Responded to MasterRoutingAgent: " + reply.getContent());
                }
            } else {
                block();
            }

            // Receive delivery information from MasterRoutingAgent
            ACLMessage deliveryMsg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (deliveryMsg != null) {
                // Process the inform message from MasterRoutingAgent
                if (deliveryMsg.getContent().startsWith("Deliver location:")) {
                    informLocation(deliveryMsg.getContent());
                } else if (deliveryMsg.getContent().startsWith("Deliver capacity:")) {
                    informCapacity(deliveryMsg.getContent());
                }
                // Inform MasterRoutingAgent that delivery information has been received
                ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);
                informMsg.addReceiver(deliveryMsg.getSender());
                informMsg.setContent("Received delivery information");
                send(informMsg);
                System.out.println("Message Received");
            }
        }

        // Handle delivery location
        private void informLocation(String locationContent) {
            // Extract delivery location
            String location = locationContent.substring("Deliver location: ".length());
            System.out.println("Received delivery location from MasterRoutingAgent: " + location);
        }

        // Handle delivery capacity
        private void informCapacity(String capacityContent) {
            // Extract capacity
            String capacityStr = capacityContent.substring("Deliver capacity: ".length());
            int capacity = Integer.parseInt(capacityStr);
            System.out.println("Received delivery capacity from MasterRoutingAgent: " + capacity);
        }
    }
}
