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
import lb2.environment.wumpusworld.AgentPosition;
import lb2.environment.wumpusworld.EfficientHybridWumpusAgent;
import lb2.environment.wumpusworld.WumpusAction;
import lb2.environment.wumpusworld.WumpusPercept;

import java.util.*;
import java.util.stream.Collectors;

public class NavigatorAgent extends Agent {
	EfficientHybridWumpusAgent agent;
	private AID speleologistAid;
	private WumpusPercept wumpusPercept;

	private final Random randomGenerator = new Random();
	private final Map<WumpusAction, List<String>> actionSentences = new HashMap<>();

	@Override
	protected void setup() {
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

		actionSentences.put(WumpusAction.TURN_LEFT, Messages.NavigatorPhrases.turnLeft);
		actionSentences.put(WumpusAction.TURN_RIGHT, Messages.NavigatorPhrases.turnRight);
		actionSentences.put(WumpusAction.FORWARD, Messages.NavigatorPhrases.goForward);
		actionSentences.put(WumpusAction.SHOOT, Messages.NavigatorPhrases.shoot);
		actionSentences.put(WumpusAction.GRAB, Messages.NavigatorPhrases.grab);
		actionSentences.put(WumpusAction.CLIMB, Messages.NavigatorPhrases.climb);

		agent = new EfficientHybridWumpusAgent(
				4, 4,
				new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH)
		);

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
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				String state = msg.getContent();
				wumpusPercept = parseText(state);
				addBehaviour(new MakeDecisionBehaviour());
			} else {
				block();
			}
		}
	}

	private class MakeDecisionBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			WumpusAction action = agent.act(wumpusPercept).orElseThrow();
			String actionSentence = generateText(action);

			ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
			reply.setLanguage("English");
			reply.setOntology("WumpusWorld");
			reply.setContent(actionSentence);
			reply.addReplyTo(speleologistAid);
			reply.addReceiver(speleologistAid);
			myAgent.send(reply);
		}
	}

	public String generateText(WumpusAction action) {
		List<String> sentences = actionSentences.get(action);
		int index = randomGenerator.nextInt(sentences.size());
		return sentences.get(index);
	}

	public WumpusPercept parseText(String speech) {
		List<String> feelings = Arrays.stream(
				speech.split(". ")
		).map(String::toLowerCase).collect(Collectors.toList());

		WumpusPercept wumpusPercept = new WumpusPercept();
		for (String feeling : feelings) {
			for (String word : Messages.PerceptKeyWords.stench) {
				if (feeling.contains(word)) {
					wumpusPercept.setStench();
				}
				break;
			}
			for (String word : Messages.PerceptKeyWords.breeze) {
				if (feeling.contains(word)) {
					wumpusPercept.setBreeze();
				}
				break;
			}
			for (String word : Messages.PerceptKeyWords.glitter) {
				if (feeling.contains(word)) {
					wumpusPercept.setGlitter();
				}
				break;
			}
			for (String word : Messages.PerceptKeyWords.bump) {
				if (feeling.contains(word)) {
					wumpusPercept.setBump();
				}
				break;
			}
			for (String word : Messages.PerceptKeyWords.scream) {
				if (feeling.contains(word)) {
					wumpusPercept.setScream();
				}
				break;
			}
		}
		return wumpusPercept;
	}
}
