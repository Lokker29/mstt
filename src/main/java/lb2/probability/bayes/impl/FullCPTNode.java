package lb2.probability.bayes.impl;

import lb2.probability.RandomVariable;
import lb2.probability.bayes.ConditionalProbabilityDistribution;
import lb2.probability.bayes.ConditionalProbabilityTable;
import lb2.probability.bayes.FiniteNode;
import lb2.probability.bayes.Node;

/**
 * Default implementation of the FiniteNode interface that uses a fully
 * specified Conditional Probability Table to represent the Node's conditional
 * distribution.
 *
 * @author Ciaran O'Reilly
 */
public class FullCPTNode extends AbstractNode implements FiniteNode {
	private ConditionalProbabilityTable cpt = null;

	public FullCPTNode(RandomVariable var, double[] distribution) {
		this(var, distribution, (Node[]) null);
	}

	public FullCPTNode(RandomVariable var, double[] values, Node... parents) {
		super(var, parents);

		RandomVariable[] conditionedOn = new RandomVariable[getParents().size()];
		int i = 0;
		for(Node p : getParents()) {
			conditionedOn[i++] = p.getRandomVariable();
		}

		cpt = new CPT(var, values, conditionedOn);
	}

	//
	// START-Node
	@Override
	public ConditionalProbabilityDistribution getCPD() {
		return getCPT();
	}

	// END-Node
	//

	//
	// START-FiniteNode

	@Override
	public ConditionalProbabilityTable getCPT() {
		return cpt;
	}

	// END-FiniteNode
	//
}
