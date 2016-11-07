package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCErrorCode.MISSING_COLON_EQUALS;
import static wci.frontend.subsetc.SubsetCErrorCode.MISSING_SEMICOLON;
import static wci.frontend.subsetc.SubsetCTokenType.EQUALS;
import static wci.frontend.subsetc.SubsetCTokenType.FLOAT;
import static wci.frontend.subsetc.SubsetCTokenType.INT;
import static wci.frontend.subsetc.SubsetCTokenType.SEMICOLON;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.ASSIGN;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.VARIABLE;
import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCErrorCode;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;

/**
 * <h1>AssignmentStatementParser</h1>
 *
 * <p>Parse a Pascal assignment statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class AssignmentStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public AssignmentStatementParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse an assignment statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        // Create the ASSIGN node.
        ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);
        
        if (token.getType() == FLOAT || token.getType() == INT) {
        	Token type = token;
        	token = nextToken();
        	if (symTabStack.lookup(token.getText().toLowerCase()) == null) {
        		errorHandler.flag(token, SubsetCErrorCode.IDENTIFIER_UNDEFINED, this);
        	}
        }

        // Look up the target identifer in the symbol table stack.
        // Enter the identifier into the table if it's not found.
        String targetName = token.getText().toLowerCase();
        SymTabEntry targetId = symTabStack.lookup(targetName);
        if (targetId == null) {
            targetId = symTabStack.enterLocal(targetName);
        }
        targetId.appendLineNumber(token.getLineNumber());

        token = nextToken();  // consume the identifier token

        // Create the variable node and set its name attribute.
        ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
        variableNode.setAttribute(ID, targetId);

        // The ASSIGN node adopts the variable node as its first child.
        assignNode.addChild(variableNode);

        // Look for the := token.
        if (token.getType() == EQUALS) {
            token = nextToken();  // consume the :=
        }
        else {
            errorHandler.flag(token, MISSING_COLON_EQUALS, this);
        }

        // Parse the expression.  The ASSIGN node adopts the expression's
        // node as its second child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        assignNode.addChild(expressionParser.parse(token));
        
        token = currentToken();
        if (token.getType() == SEMICOLON) {
        	token = nextToken();
        } else {
        	errorHandler.flag(token, MISSING_SEMICOLON, this);
        }

        return assignNode;
    }
}
