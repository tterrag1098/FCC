package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCTokenType.ELSE;
import static wci.frontend.subsetc.SubsetCTokenType.FLOAT;
import static wci.frontend.subsetc.SubsetCTokenType.IDENTIFIER;
import static wci.frontend.subsetc.SubsetCTokenType.INT;
import static wci.frontend.subsetc.SubsetCTokenType.SEMICOLON;
import static wci.frontend.subsetc.SubsetCTokenType.WHILE;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.SLOT;

import java.util.EnumSet;

import wci.frontend.EofToken;
import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCErrorCode;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.SymTabKeyImpl;

/**
 * <h1>StatementParser</h1>
 *
 * <p>Parse a Pascal statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class StatementParser extends SubsetCParserTD
{
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public StatementParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a statement.
    protected static final EnumSet<SubsetCTokenType> STMT_START_SET =
        EnumSet.of(INT, FLOAT, WHILE, SubsetCTokenType.IF, WHILE, IDENTIFIER, SEMICOLON);

    // Synchronization set for following a statement.
    protected static final EnumSet<SubsetCTokenType> STMT_FOLLOW_SET =
        EnumSet.of(SEMICOLON, ELSE);
    
    /**
     * Parse a statement.
     * To be overridden by the specialized statement parser subclasses.
     * @param token the initial token.
     * @param parentId 
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        ICodeNode statementNode = null;

        switch ((SubsetCTokenType) token.getType()) {

            case LEFT_BRACE: {
                CompoundStatementParser compoundParser =
                    new CompoundStatementParser(this);
                statementNode = compoundParser.parse(token, parentId);
                break;
            }
            
            case IF:
            case WHILE: {
            	ControlStatementParser controlParser = new ControlStatementParser(this);
            	statementNode = controlParser.parse(token, parentId);
            	break;
            }

            // An assignment statement begins with a variable's identifier.
            case IDENTIFIER: {
            	SymTabEntry entry = symTabStack.lookup(token.getText());
            	if (entry == null) {
            		errorHandler.flag(token, SubsetCErrorCode.IDENTIFIER_UNDEFINED, this);
            		synchronize(STMT_FOLLOW_SET);
            		break;
            	}
            	switch((DefinitionImpl) entry.getDefinition()) {
            	case TYPE:
            		VariableDeclarationParser declarationParser = new VariableDeclarationParser(this);
            		declarationParser.setDefinition(DefinitionImpl.VARIABLE);
            		declarationParser.parse(token, parentId);
            		break;
            	case FUNCTION:
            	case PROCEDURE:
            		// Call parsing
            		CallParser callParser = new CallParser(this);
            	    statementNode = callParser.parse(token);
            		break;
            	default:
            		AssignmentStatementParser assignmentParser = new AssignmentStatementParser(this);
            		statementNode = assignmentParser.parse(token);
            	}
            	break;
            }
            
            case RETURN: {
                ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);
                assignNode.setTypeSpec(parentId.getTypeSpec());
            	SymTabEntry targetId = symTabStack.enterLocal(parentId.getName());
            	targetId.setDefinition(DefinitionImpl.VARIABLE);
            	targetId.setTypeSpec(parentId.getTypeSpec());
                
            	// Set its slot number in the local variables array.
                int slot = targetId.getSymTab().nextSlotNumber();
                targetId.setAttribute(SLOT, slot);

                // Create the variable node and set its name attribute.
                ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
                variableNode.setAttribute(ID, targetId);
                variableNode.setTypeSpec(parentId.getTypeSpec());

                // The ASSIGN node adopts the variable node as its first child.
                assignNode.addChild(variableNode);
                
            	ExpressionParser expressionParser = new ExpressionParser(this);
            	token = nextToken(); // Consume RETURN
                assignNode.addChild(expressionParser.parse(token));
                
                statementNode = assignNode;
                token = nextToken(); // Consume semicolon
                break;
            }

            default: {
                statementNode = ICodeFactory.createICodeNode(NO_OP);
                token = nextToken();
                break;
            }
        }
        
        if (currentToken().getType() == SEMICOLON) {
        	token = nextToken();
        }

        // Set the current line number as an attribute.
        setLineNumber(statementNode, token);
        return statementNode;
    }

    /**
     * Set the current line number as a statement node attribute.
     * @param node ICodeNode
     * @param token Token
     */
    protected void setLineNumber(ICodeNode node, Token token)
    {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    /**
     * Parse a statement list.
     * @param token the curent token.
     * @param parentNode the parent node of the statement list.
     * @param routineId 
     * @param terminator the token type of the node that terminates the list.
     * @param errorCode the error code if the terminator token is missing.
     * @throws Exception if an error occurred.
     */
    protected void parseList(Token token, ICodeNode parentNode,
                             SymTabEntry routineId, 
                             SubsetCTokenType terminator,
                             SubsetCErrorCode errorCode)
        throws Exception
    {
        // Loop to parse each statement until the END token
        // or the end of the source file.
        while (!(token instanceof EofToken) &&
               (token.getType() != terminator)) {

            // Parse a statement.  The parent node adopts the statement node.
            ICodeNode statementNode = parse(token, routineId);
            parentNode.addChild(statementNode);

            token = currentToken();
        }

        // Look for the terminator token.
        if (token.getType() == terminator) {
            token = nextToken();  // consume the terminator token
        }
        else {
            errorHandler.flag(token, errorCode, this);
        }
    }
}
