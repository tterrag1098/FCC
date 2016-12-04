package wci.frontend.subsetc.parsers;

import static wci.frontend.pascal.PascalErrorCode.INCOMPATIBLE_TYPES;
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
import wci.frontend.subsetc.parsers.VariableParser;
import wci.frontend.subsetc.SubsetCErrorCode;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.Predefined;
import wci.intermediate.typeimpl.TypeChecker;

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

        VariableParser variableParser = new VariableParser(this);
        
        ICodeNode targetNode = variableParser.parse(token);
                
        TypeSpec targetType = targetNode != null ? targetNode.getTypeSpec()
                : Predefined.undefinedType;
        
        // Look up the target identifer in the symbol table stack.
        // Enter the identifier into the table if it's not found.
        String targetName = token.getText();
        SymTabEntry targetId = symTabStack.lookup(targetName);
        if (targetId != null) {
            targetId.appendLineNumber(token.getLineNumber());
        } else {
        	errorHandler.flag(token, SubsetCErrorCode.IDENTIFIER_UNDEFINED, this);
        }

        token = currentToken();

        // Create the variable node and set its name attribute.
        ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
        variableNode.setAttribute(ID, targetId);
        variableNode.setTypeSpec(targetId.getTypeSpec());

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
        ICodeNode exprNode = expressionParser.parse(token);
        assignNode.addChild(exprNode);
        
        token = currentToken();
        if (token.getType() == SEMICOLON) {
        	token = nextToken();
        } else {
        	errorHandler.flag(token, MISSING_SEMICOLON, this);
        }
        
        // Type check: Assignment compatible?
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.areAssignmentCompatible(targetType, exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }
        
        assignNode.setTypeSpec(targetType);
        return assignNode;
    }
}
