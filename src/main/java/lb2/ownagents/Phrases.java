package lb2.ownagents;

import java.util.List;

public final class Phrases {
	public static final class PerceptKeyWords {
		public static List<String> breeze = List.of("breeze");
		public static List<String> stench = List.of("stench", "stinky", "smell");
		public static List<String> glitter = List.of("glitter", "shiny");
		public static List<String> bump = List.of("bump", "hit");
		public static List<String> scream = List.of("scream", "hear");

	}

	public static final class SpeleologistPhrases {
		public static List<String> pitNear = List.of("I feel breeze", "It's breezy here", "There is a breeze");
		public static List<String> wumpusNear = List.of("I smell something", "It's stinky here", "There is a stench");
		public static List<String> goldNear = List.of("I see something shiny", "It's glittery here", "There is a glitter");
		public static List<String> wallNear = List.of("I hit the wall", "It's bumping here", "There is a bump");
		public static List<String> wumpusKilledNear = List.of("I hear something", "It's screaming here", "There is a scream");
		public static List<String> nothing = List.of("I see nothing", "There is nothing");
	}

	public static final class ActionKeyWords {
		public static List<String> turnLeft = List.of("left");
		public static List<String> turnRight = List.of("right");
		public static List<String> goForward = List.of("forward", "ahead", "straight");
		public static List<String> grab = List.of("grab");
		public static List<String> shoot = List.of("shoot");
		public static List<String> climb = List.of("climb");
	}

	public static final class NavigatorPhrases {
		public static List<String> goForward = List.of("Go forward", "Go straight", "Go ahead");
		public static List<String> turnLeft = List.of("Turn left", "Turn to the left");
		public static List<String> turnRight = List.of("Turn right", "Turn to the right");
		public static List<String> shoot = List.of("Shoot", "Shoot the wumpus");
		public static List<String> grab = List.of("Grab", "Grab the gold");
		public static List<String> climb = List.of("Climb", "Climb the ladder");
	}
}
