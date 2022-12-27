package lb2.agent.impl;

import lb2.agent.State;

/**
 * @author Ciaran O'Reilly
 */
public class DynamicState extends ObjectWithDynamicAttributes implements State {
	public DynamicState() {
	}

	@Override
	public String describeType() {
		return State.class.getSimpleName();
	}
}