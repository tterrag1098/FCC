package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCTokenType.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.LOOP;

import java.util.EnumSet;

import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCErrorCode;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;

public class ControlStatementParser extends StatementParser {

	public ControlStatementParser(SubsetCParserTD parent) {
		super(parent);
	}
	
	@Override
	public ICodeNode parse(Token token, SymTabEntry parentId) throws Exception {
		
		ICodeNode controlNode = ICodeFactory.createICodeNode(token.getType() == SubsetCTokenType.WHILE ? LOOP : ICodeNodeTypeImpl.IF);

		token = nextToken(); // Consume the control statement

		if (token.getType() != LEFT_PAREN) {
			errorHandler.flag(token, SubsetCErrorCode.INVALID_EXPRESSION, this);
		} else {
			token = nextToken();
		}
		
		while (token.getType() != RIGHT_PAREN && token.getType() != ERROR) {
			ICodeNode testNode = controlNode;
			if (controlNode.getType() == LOOP) {
				testNode = testNode.addChild(ICodeFactory.createICodeNode(ICodeNodeTypeImpl.TEST));
			}
			ExpressionParser expr = new ExpressionParser(this);
			ICodeNode node = expr.parse(token);
			if (controlNode.getType() == LOOP) {
				/*
				 * So here's the deal. Whoever wrote the backend code for this is a moron.
				 * 
				 * In pascal, all loops are done REPEAT { ... } UNTIL ...
				 * This means that when converting to a while-style loop,
				 * the condition must be negated.
				 * 
				 * This is done automatically **IN THE BACKEND**!! You know,
				 * the part that was supposed to be language agnostic.
				 * 
				 * So, as a workaround, to implement proper C-style while loops,
				 * we have to negate our condition, so that it is AGAIN NEGATED
				 * by the backend. Otherwise all while loop conditions are inverted.
				 * 
				 * </rant>
				 */
				ICodeNode not = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);
				not.addChild(node);
				node = not;
			}
			testNode.addChild(node);
			token = currentToken();
		}
		
		synchronize(EnumSet.of(RIGHT_PAREN));
		token = nextToken();
		
		StatementParser parser;
		if (token.getType() == LEFT_BRACE) {
			parser = new CompoundStatementParser(this);
		} else {
			parser = new StatementParser(this);
		}
		
		controlNode.addChild(parser.parse(token, parentId));
		return controlNode;
	}

}
