package lb2.ownagents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lb2.environment.wumpusworld.WumpusAction;
import lb2.environment.wumpusworld.WumpusPercept;

public class SpeleologistSpeech {
	private final Random randomGenerator;
	private final Map<List<String>, WumpusAction> actionKeyWords;

	public SpeleologistSpeech() {
		randomGenerator = new Random();

		actionKeyWords = new HashMap<>();
		actionKeyWords.put(Phrases.ActionKeyWords.turnLeft, WumpusAction.TURN_LEFT);
		actionKeyWords.put(Phrases.ActionKeyWords.turnRight, WumpusAction.TURN_RIGHT);
		actionKeyWords.put(Phrases.ActionKeyWords.goForward, WumpusAction.FORWARD);
		actionKeyWords.put(Phrases.ActionKeyWords.shoot, WumpusAction.SHOOT);
		actionKeyWords.put(Phrases.ActionKeyWords.grab, WumpusAction.GRAB);
		actionKeyWords.put(Phrases.ActionKeyWords.climb, WumpusAction.CLIMB);
	}

	public WumpusAction recognize(String speech) {
		String finalSpeech = speech.toLowerCase();
		return actionKeyWords.keySet().stream()
				.filter(keyWords -> keyWords.stream().anyMatch(finalSpeech::contains))
				.findFirst()
				.map(actionKeyWords::get)
				.orElseThrow();
	}

	public String tellPercept(WumpusPercept wumpusPercept) {
		List<String> feelings = new ArrayList<>();

		if(wumpusPercept.isBreeze()) {
			feelings.add(getRandomPhrase(Phrases.SpeleologistPhrases.pitNear));
		}
		if(wumpusPercept.isStench()) {
			feelings.add(getRandomPhrase(Phrases.SpeleologistPhrases.wumpusNear));
		}
		if(wumpusPercept.isGlitter()) {
			feelings.add(getRandomPhrase(Phrases.SpeleologistPhrases.goldNear));
		}
		if(wumpusPercept.isBump()) {
			feelings.add(getRandomPhrase(Phrases.SpeleologistPhrases.wallNear));
		}
		if(wumpusPercept.isScream()) {
			feelings.add(getRandomPhrase(Phrases.SpeleologistPhrases.wumpusKilledNear));
		}
		if(feelings.isEmpty()) {
			feelings.add(getRandomPhrase(Phrases.SpeleologistPhrases.nothing));
		}

		return String.join(". ", feelings);
	}

	private String getRandomPhrase(List<String> sentences) {
		int index = randomGenerator.nextInt(sentences.size());
		return sentences.get(index);
	}
}

