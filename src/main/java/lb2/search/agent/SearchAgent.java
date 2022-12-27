package lb2.search.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import lb2.agent.impl.SimpleAgent;
import lb2.search.framework.Metrics;
import lb2.search.framework.SearchForActions;
import lb2.search.framework.problem.Problem;

/**
 * @param <P> The type used to represent percepts
 * @param <S> The type used to represent states
 * @param <A> The type of the actions to be used to navigate through the state space
 * @author Ravi Mohan
 * @author Ruediger Lunde
 */
public class SearchAgent<P, S, A> extends SimpleAgent<P, A> {
	private List<A> actionList;
	private Iterator<A> actionIterator;
	private Metrics searchMetrics;

	public SearchAgent(Problem<S, A> p, SearchForActions<S, A> search) {
		Optional<List<A>> actions = search.findActions(p);
		actionList = new ArrayList<>();
		actions.ifPresent(as -> actionList.addAll(as));

		actionIterator = actionList.iterator();
		searchMetrics = search.getMetrics();
	}

	@Override
	public Optional<A> act(P p) {
		if(actionIterator.hasNext())
			return Optional.of(actionIterator.next());
		return Optional.empty(); // no success or at goal
	}

	public boolean isDone() {
		return !actionIterator.hasNext();
	}

	public List<A> getActions() {
		return actionList;
	}

	public Properties getInstrumentation() {
		Properties result = new Properties();
		searchMetrics.keySet().forEach(key -> result.setProperty(key, searchMetrics.get(key)));
		return result;
	}
}