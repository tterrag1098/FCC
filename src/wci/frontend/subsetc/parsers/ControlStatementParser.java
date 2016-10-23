package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCTokenType.LEFT_BRACE;
import static wci.frontend.subsetc.SubsetCTokenType.LEFT_PAREN;
import static wci.frontend.subsetc.SubsetCTokenType.RIGHT_PAREN;

import java.util.EnumSet;

import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCErrorCode;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.ICodeNode;
import wci.intermediate.ICodeFactory;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class ControlStatementParser extends StatementParser {

	public ControlStatementParser(SubsetCParserTD parent) {
		super(parent);
	}
	
	@Override
	public ICodeNode parse(Token token) throws Exception {
		
		ICodeNode controlNode = ICodeFactory.createICodeNode(token.getType() == SubsetCTokenType.WHILE ? LOOP : IF);

		token = nextToken(); // Consume the control statement

		if (token.getType() != LEFT_PAREN) {
			errorHandler.flag(token, SubsetCErrorCode.INVALID_EXPRESSION, this);
		} else {
			token = nextToken();
		}
		
		while (token.getType() != RIGHT_PAREN) {
			ExpressionParser expr = new ExpressionParser(this);
			ICodeNode node = expr.parse(token);
			controlNode.addChild(node);
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
		
		controlNode.addChild(parser.parse(token));
		return controlNode;
	}

}
