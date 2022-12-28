package lb2.ownagents;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import lb2.environment.wumpusworld.AgentPosition;
import lb2.environment.wumpusworld.EfficientHybridWumpusAgent;
import lb2.environment.wumpusworld.HybridWumpusAgent;
import lb2.environment.wumpusworld.WumpusAction;
import lb2.environment.wumpusworld.WumpusCave;
import lb2.environment.wumpusworld.WumpusEnvironment;
import lb2.environment.wumpusworld.WumpusPercept;

public class EnvironmentAgent extends Agent {
	private WumpusEnvironment wumpusEnvironment;
	private AID speleologistAID;
	private HybridWumpusAgent speleologist;
	private WumpusPercept wumpusPercept;
	private int iteration = 0;

	@Override
	protected void setup() {
		DFAgentDescription dfAgentDescription = new DFAgentDescription();
		dfAgentDescription.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType("environment");
		sd.setName("wumpus-world");
		dfAgentDescription.addServices(sd);
		try {
			DFService.register(this, dfAgentDescription);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}

		wumpusEnvironment = new WumpusEnvironment(new WumpusCave(4, 4, ""
    		+ ". . . P "
			+ "W . P . "
			+ ". . . . "
			+ "S . P G "
		));
		speleologist = new EfficientHybridWumpusAgent(4, 4, new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH));
		wumpusPercept = new WumpusPercept();
		wumpusEnvironment.addAgent(speleologist);

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("speleologist");
		template.addServices(sd2);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			speleologistAID = result[0].getName();
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}

		System.out.println("Environment-agent " + getAID().getName() + " is ready.");

		addBehaviour(new CheckMailBehavior());
	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		System.out.println("Environment-agent " + getAID().getName() + " terminating.");
	}

	private class CheckMailBehavior extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchPerformative(ACLMessage.CFP));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				if (ACLMessage.REQUEST == msg.getPerformative()) {
					addBehaviour(new RequestBehaviour());
				} else if (ACLMessage.CFP == msg.getPerformative()) {
					String message = msg.getContent();
					wumpusEnvironment.execute(speleologist, WumpusAction.fromString(message));
					addBehaviour(new AcceptBehaviour());
				} else {
					block();
				}
			} else {
				block();
			}
		}
	}

	private class RequestBehaviour extends OneShotBehaviour {
		ObjectMapper objectMapper = new ObjectMapper();

		@SneakyThrows
		@Override
		public void action() {
			AgentPosition agentPosition = wumpusEnvironment.getAgentPosition(speleologist);
			System.out.println("Env: Position: " + agentPosition);

			wumpusPercept = wumpusEnvironment.getPerceptSeenBy(speleologist);
			ACLMessage report = new ACLMessage(ACLMessage.INFORM);
			report.setContent(objectMapper.writeValueAsString(new Answer(wumpusPercept, iteration++)));
			report.addReceiver(speleologistAID);
			report.addReplyTo(speleologistAID);
			myAgent.send(report);
		}
	}

	private class AcceptBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage report = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			report.setContent("OK");
			report.addReceiver(speleologistAID);
			report.addReplyTo(speleologistAID);
			myAgent.send(report);
		}
	}
}
