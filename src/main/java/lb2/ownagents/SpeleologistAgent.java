package lb2.ownagents;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lb2.environment.wumpusworld.WumpusPercept;
import lombok.SneakyThrows;
import lb2.environment.wumpusworld.WumpusAction;

import java.util.*;

public class SpeleologistAgent extends Agent {
	private AID environmentAid;
	private AID navigatorAid;

	private final Random randomGenerator = new Random();
	private final Map<List<String>, WumpusAction> actionKeyWords = new HashMap<>();

	@Override
	protected void setup() {
		DFAgentDescription dfAgentDescription = new DFAgentDescription();
		dfAgentDescription.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("speleologist");
		sd.setName("wumpus-world");
		dfAgentDescription.addServices(sd);
		try {
			DFService.register(this, dfAgentDescription);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}

		actionKeyWords.put(Messages.ActionKeyWords.turnLeft, WumpusAction.TURN_LEFT);
		actionKeyWords.put(Messages.ActionKeyWords.turnRight, WumpusAction.TURN_RIGHT);
		actionKeyWords.put(Messages.ActionKeyWords.goForward, WumpusAction.FORWARD);
		actionKeyWords.put(Messages.ActionKeyWords.shoot, WumpusAction.SHOOT);
		actionKeyWords.put(Messages.ActionKeyWords.grab, WumpusAction.GRAB);
		actionKeyWords.put(Messages.ActionKeyWords.climb, WumpusAction.CLIMB);

		System.out.println("Speleologist-agent " + getAID().getName() + " is ready.");

		addBehaviour(new SpeleologistBehaviour());
	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		System.out.println("Speleologist-agent " + getAID().getName() + " terminating.");
		System.exit(0);
	}

	class SpeleologistBehaviour extends Behaviour {
		ObjectMapper objectMapper = new ObjectMapper();
		WumpusAction wumpusAction;
		private MessageTemplate messageTemplate;
		private Answer answer;
		private int step = 0;

		@SneakyThrows
		@Override
		public void action() {
			switch(step) {
				case 0:
					DFAgentDescription template1 = new DFAgentDescription();
					ServiceDescription sd1 = new ServiceDescription();
					sd1.setType("environment");
					template1.addServices(sd1);

					DFAgentDescription template2 = new DFAgentDescription();
					ServiceDescription sd2 = new ServiceDescription();
					sd2.setType("navigator");
					template2.addServices(sd2);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template1);
						environmentAid = result[0].getName();
						DFAgentDescription[] result2 = DFService.search(myAgent, template2);
						navigatorAid = result2[0].getName();
					} catch(FIPAException fe) {
						fe.printStackTrace();
					}

					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					request.setContent("Current state");
					request.addReceiver(environmentAid);
					myAgent.send(request);

					messageTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchReplyTo(new AID[]{myAgent.getAID()}));
					step = 1;
					break;
				case 1:
					ACLMessage reply = myAgent.receive(messageTemplate);
					if (reply != null) {
						if(reply.getPerformative() == ACLMessage.INFORM) {
							String state = reply.getContent();
							System.out.println("Env: State = " + state);
							answer = objectMapper.readValue(state, Answer.class);
						}
						step = 2;
					} else {
						block();
					}
					break;
				case 2:
					ACLMessage state = new ACLMessage(ACLMessage.INFORM);

					String feelings = generatePercept(answer.wumpusPercept);
					System.out.println("Spl: Feelings = " + feelings);

					state.setLanguage("English");
					state.setOntology("WumpusWorld");
					state.addReceiver(navigatorAid);
					state.setContent(feelings);
					myAgent.send(state);

					messageTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
							MessageTemplate.MatchReplyTo(new AID[]{myAgent.getAID()}));
					step = 3;
					break;
				case 3:
					ACLMessage reply2 = myAgent.receive(messageTemplate);
					if(reply2 != null) {
						String action = reply2.getContent();
						wumpusAction = recognize(action);
						System.out.println("Nav: Action = " + action);
						step = 4;
					} else {
						block();
					}
					break;
				case 4:
					ACLMessage action = new ACLMessage(ACLMessage.CFP);
					action.setConversationId("environment");
					action.addReceiver(environmentAid);
					action.setContent(wumpusAction.getSymbol());
					myAgent.send(action);
					messageTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
							MessageTemplate.MatchReplyTo(new AID[]{myAgent.getAID()}));
					step = 5;
					break;
				case 5:
					ACLMessage envReply = myAgent.receive(messageTemplate);
					if(envReply != null) {
						if(envReply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
							if(WumpusAction.CLIMB.equals(wumpusAction)) {
								System.out.println("Spl: I climbed. Done!");
								step = 6;
							} else {
								System.out.println();
								step = 0;
							}

						}
					} else {
						block();
					}
					break;
				case 6:
					step = 7;
					doDelete();
					break;
			}
		}

		@Override
		public boolean done() {
			return step == 7;
		}
	}

	public WumpusAction recognize(String message) {
		String lowerMessage = message.toLowerCase();
		return actionKeyWords.keySet().stream()
				.filter(keyWords -> keyWords.stream().anyMatch(lowerMessage::contains))
				.findFirst()
				.map(actionKeyWords::get)
				.orElseThrow();
	}

	public String generatePercept(WumpusPercept wumpusPercept) {
		List<String> feelings = new ArrayList<>();

		if(wumpusPercept.isBreeze()) {
			feelings.add(getRandomMessage(Messages.SpeleologistPhrases.pitNear));
		}
		if(wumpusPercept.isStench()) {
			feelings.add(getRandomMessage(Messages.SpeleologistPhrases.wumpusNear));
		}
		if(wumpusPercept.isGlitter()) {
			feelings.add(getRandomMessage(Messages.SpeleologistPhrases.goldNear));
		}
		if(wumpusPercept.isBump()) {
			feelings.add(getRandomMessage(Messages.SpeleologistPhrases.wallNear));
		}
		if(wumpusPercept.isScream()) {
			feelings.add(getRandomMessage(Messages.SpeleologistPhrases.wumpusKilledNear));
		}
		if(feelings.isEmpty()) {
			feelings.add(getRandomMessage(Messages.SpeleologistPhrases.nothing));
		}

		return String.join(". ", feelings);
	}

	private String getRandomMessage(List<String> sentences) {
		int index = randomGenerator.nextInt(sentences.size());
		return sentences.get(index);
	}
}
