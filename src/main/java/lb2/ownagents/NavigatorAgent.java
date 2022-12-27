package lb2.ownagents;

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
import lb2.environment.wumpusworld.WumpusAction;
import lb2.environment.wumpusworld.WumpusPercept;

public class NavigatorAgent extends Agent {
	EfficientHybridWumpusAgent agent;
	NavigatorSpeech speech;
	private AID speleologistAid;

	@Override
	protected void setup() {
		agent = new EfficientHybridWumpusAgent(4, 4, new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH));
		speech = new NavigatorSpeech();

		DFAgentDescription dfAgentDescription = new DFAgentDescription();
		dfAgentDescription.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("navigator");
		sd.setName("wumpus-world");
		dfAgentDescription.addServices(sd);
		try {
			DFService.register(this, dfAgentDescription);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("speleologist");
		template.addServices(sd2);

		try {
			DFAgentDescription[] result = DFService.search(this, template);
			speleologistAid = result[0].getName();
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Navigator-agent " + getAID().getName() + " is ready.");

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

		System.out.println("Navigator-agent " + getAID().getName() + " terminating.");
	}

	private class CheckMailBehavior extends CyclicBehaviour {
		@SneakyThrows
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				String state = msg.getContent();
				WumpusPercept wumpusPercept = speech.recognize(state);
				addBehaviour(new FindActionBehaviour(wumpusPercept));
			} else {
				block();
			}
		}
	}

	private class FindActionBehaviour extends OneShotBehaviour {
		WumpusPercept wumpusPercept;

		FindActionBehaviour(WumpusPercept wumpusPercept) {
			this.wumpusPercept = wumpusPercept;
		}

		@SneakyThrows
		@Override
		public void action() {
			WumpusAction action = agent.act(wumpusPercept).orElseThrow();

			ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
			String actionSentence = speech.tellAction(action);

			reply.setLanguage("English");
			reply.setOntology("WumpusWorld");
			reply.setContent(actionSentence);
			reply.addReplyTo(speleologistAid);
			reply.addReceiver(speleologistAid);
			myAgent.send(reply);
		}
	}
}
