package lb2.logic.propositional.transformations;

import lb2.logic.propositional.parsing.AbstractPLVisitor;
import lb2.logic.propositional.parsing.ast.ComplexSentence;
import lb2.logic.propositional.parsing.ast.Connective;
import lb2.logic.propositional.parsing.ast.Sentence;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): page 254.<br>
 * <br>
 * Move ~ inwards by repeated application of the following equivalences:<br>
 * ~(~&alpha;) &equiv; &alpha; (double-negation elimination)<br>
 * ~(&alpha; & &beta;) &equiv; (~&alpha; | ~&beta;) (De Morgan)<br>
 * ~(&alpha; | &beta;) &equiv; (~&alpha; & ~&beta;) (De Morgan)<br>
 *
 * @author Ciaran O'Reilly
 * @author Ruediger Lunde
 */
public class MoveNotInwards extends AbstractPLVisitor<Object> {

	/**
	 * Move ~ inwards.
	 *
	 * @param sentence a propositional logic sentence that has had it biconditionals
	 * and implications removed.
	 * @return an equivalent Sentence to the input with all negations moved
	 * inwards.
	 * @throws IllegalArgumentException if a biconditional or implication is encountered in the
	 * input.
	 */
	public static Sentence apply(Sentence sentence) {
		return sentence.accept(new MoveNotInwards(), null);
	}

	@Override
	public Sentence visitUnarySentence(ComplexSentence s, Object arg) {
		Sentence result;

		Sentence negated = s.getSimplerSentence(0);
		if(negated.isPropositionSymbol()) {
			// Already moved in fully
			result = s;
		} else if(negated.isNotSentence()) {
			// ~(~&alpha;) &equiv; &alpha; (double-negation elimination)
			Sentence alpha = negated.getSimplerSentence(0);
			result = alpha.accept(this, arg);
		} else if(negated.isAndSentence() || negated.isOrSentence()) {
			Sentence alpha = negated.getSimplerSentence(0);
			Sentence beta = negated.getSimplerSentence(1);

			// This ensures double-negation elimination happens
			Sentence notAlpha = (new ComplexSentence(Connective.NOT, alpha))
					.accept(this, null);
			Sentence notBeta = (new ComplexSentence(Connective.NOT, beta))
					.accept(this, null);
			if(negated.isAndSentence()) {
				// ~(&alpha; & &beta;) &equiv; (~&alpha; | ~&beta;) (De Morgan)
				result = new ComplexSentence(Connective.OR, notAlpha, notBeta);
			} else {
				// ~(&alpha; | &beta;) &equiv; (~&alpha; & ~&beta;) (De Morgan)
				result = new ComplexSentence(Connective.AND, notAlpha, notBeta);
			}
		} else {
			throw new IllegalArgumentException(
					"Biconditionals and Implications should not exist in input: "
							+ s);
		}

		return result;
	}
}