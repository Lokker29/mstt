package lb2.probability;

public interface BeliefState<Action, Percept> {
	void update(Action action, Percept percept);

}
