package lb2.ownagents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import lb2.environment.wumpusworld.WumpusAction;
import lb2.environment.wumpusworld.WumpusPercept;

public class NavigatorSpeech {
	private final Random randomGenerator;
	private final Map<WumpusAction, List<String>> actionSentences;

	public NavigatorSpeech() {
		randomGenerator = new Random();

		actionSentences = new HashMap<>();
		actionSentences.put(WumpusAction.TURN_LEFT, Phrases.NavigatorPhrases.turnLeft);
		actionSentences.put(WumpusAction.TURN_RIGHT, Phrases.NavigatorPhrases.turnRight);
		actionSentences.put(WumpusAction.FORWARD, Phrases.NavigatorPhrases.goForward);
		actionSentences.put(WumpusAction.SHOOT, Phrases.NavigatorPhrases.shoot);
		actionSentences.put(WumpusAction.GRAB, Phrases.NavigatorPhrases.grab);
		actionSentences.put(WumpusAction.CLIMB, Phrases.NavigatorPhrases.climb);

	}

	public String tellAction(WumpusAction action) {
		List<String> sentences = actionSentences.get(action);
		int index = randomGenerator.nextInt(sentences.size());
		return sentences.get(index);
	}

	public WumpusPercept recognize(String speech) {
		List<String> feelings = Arrays.stream(speech.split(". ")).map(String::toLowerCase).collect(Collectors.toList());
		WumpusPercept wumpusPercept = new WumpusPercept();
		for(String feeling : feelings) {
			for(String word : Phrases.PerceptKeyWords.stench) {
				if(feeling.contains(word)) {
					wumpusPercept.setStench();
				}
				break;
			}
			for(String word : Phrases.PerceptKeyWords.breeze) {
				if(feeling.contains(word)) {
					wumpusPercept.setBreeze();
				}
				break;
			}
			for(String word : Phrases.PerceptKeyWords.glitter) {
				if(feeling.contains(word)) {
					wumpusPercept.setGlitter();
				}
				break;
			}
			for(String word : Phrases.PerceptKeyWords.bump) {
				if(feeling.contains(word)) {
					wumpusPercept.setBump();
				}
				break;
			}
			for(String word : Phrases.PerceptKeyWords.scream) {
				if(feeling.contains(word)) {
					wumpusPercept.setScream();
				}
				break;
			}
		}
		return wumpusPercept;
	}
}
