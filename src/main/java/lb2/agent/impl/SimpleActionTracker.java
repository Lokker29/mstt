package lb2.agent.impl;

import lb2.agent.Agent;
import lb2.agent.Environment;
import lb2.agent.EnvironmentListener;

/**
 * Environment listener implementation which logs performed action and
 * provides a comma-separated String with all actions performed so far.
 *
 * @author Ruediger Lunde
 */
public class SimpleActionTracker implements EnvironmentListener<Object, Object> {
	protected final StringBuilder actions = new StringBuilder();

	public String getActions() {
		return actions.toString();
	}

	@Override
	public void notify(String msg) {
		// Do nothing by default.
	}

	@Override
	public void agentAdded(Agent<?, ?> agent, Environment<?, ?> source) {
		// Do nothing by default.
	}

	@Override
	public void agentActed(Agent<?, ?> agent, Object percept, Object action, Environment<?, ?> source) {
		if(actions.length() > 0)
			actions.append(", ");
		actions.append(action);
	}
}
