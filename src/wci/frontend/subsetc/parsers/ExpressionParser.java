package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCErrorCode.IDENTIFIER_UNDEFINED;
import static wci.frontend.subsetc.SubsetCErrorCode.MISSING_RIGHT_PAREN;
import static wci.frontend.subsetc.SubsetCErrorCode.UNEXPECTED_TOKEN;
import static wci.frontend.subsetc.SubsetCTokenType.EQUALS;
import static wci.frontend.subsetc.SubsetCTokenType.GREATER_EQUALS;
import static wci.frontend.subsetc.SubsetCTokenType.GREATER_THAN;
import static wci.frontend.subsetc.SubsetCTokenType.LESS_EQUALS;
import static wci.frontend.subsetc.SubsetCTokenType.LESS_THAN;
import static wci.frontend.subsetc.SubsetCTokenType.MINUS;
import static wci.frontend.subsetc.SubsetCTokenType.NOT_EQUALS;
import static wci.frontend.subsetc.SubsetCTokenType.PLUS;
import static wci.frontend.subsetc.SubsetCTokenType.RIGHT_PAREN;
import static wci.frontend.subsetc.SubsetCTokenType.SLASH;
import static wci.frontend.subsetc.SubsetCTokenType.STAR;
import static wci.frontend.subsetc.SubsetCTokenType.TRUE;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.ADD;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.BOOLEAN_CONSTANT;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.EQ;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.FLOAT_DIVIDE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.GE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.GT;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.INTEGER_CONSTANT;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.LE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.LT;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.MULTIPLY;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.NE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.NEGATE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.REAL_CONSTANT;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.STRING_CONSTANT;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.SUBTRACT;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.VARIABLE;

import java.util.EnumSet;
import java.util.HashMap;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.ICodeNodeType;
import wci.intermediate.SymTabEntry;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;

/**
 * <h1>ExpressionParser</h1>
 *
 * <p>Parse a Pascal expression.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ExpressionParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public ExpressionParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse an expression.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        return parseExpression(token);
    }

    // Set of relational operators.
    private static final EnumSet<SubsetCTokenType> REL_OPS =
        EnumSet.of(EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUALS,
                   GREATER_THAN, GREATER_EQUALS);

    // Map relational operator tokens to node types.
    private static final HashMap<SubsetCTokenType, ICodeNodeType>
        REL_OPS_MAP = new HashMap<>();
    static {
        REL_OPS_MAP.put(EQUALS, EQ);
        REL_OPS_MAP.put(NOT_EQUALS, NE);
        REL_OPS_MAP.put(LESS_THAN, LT);
        REL_OPS_MAP.put(LESS_EQUALS, LE);
        REL_OPS_MAP.put(GREATER_THAN, GT);
        REL_OPS_MAP.put(GREATER_EQUALS, GE);
    };

    /**
     * Parse an expression.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseExpression(Token token)
        throws Exception
    {
        // Parse a simple expression and make the root of its tree
        // the root node.
        ICodeNode rootNode = parseSimpleExpression(token);

        token = currentToken();
        TokenType tokenType = token.getType();

        // Look for a relational operator.
        if (REL_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = REL_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse the second simple expression.  The operator node adopts
            // the simple expression's tree as its second child.
            opNode.addChild(parseSimpleExpression(token));

            // The operator node becomes the new root node.
            rootNode = opNode;
        }

        return rootNode;
    }

    // Set of additive operators.
    private static final EnumSet<SubsetCTokenType> ADD_OPS =
        EnumSet.of(PLUS, MINUS, SubsetCTokenType.OR);

    // Map additive operator tokens to node types.
    private static final HashMap<SubsetCTokenType, ICodeNodeTypeImpl>
        ADD_OPS_OPS_MAP = new HashMap<>();
    static {
        ADD_OPS_OPS_MAP.put(PLUS, ADD);
        ADD_OPS_OPS_MAP.put(MINUS, SUBTRACT);
        ADD_OPS_OPS_MAP.put(SubsetCTokenType.OR, ICodeNodeTypeImpl.OR);
    };

    /**
     * Parse a simple expression.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseSimpleExpression(Token token)
        throws Exception
    {
        TokenType signType = null;  // type of leading sign (if any)

        // Look for a leading + or - sign.
        TokenType tokenType = token.getType();
        if ((tokenType == PLUS) || (tokenType == MINUS)) {
            signType = tokenType;
            token = nextToken();  // consume the + or -
        }

        // Parse a term and make the root of its tree the root node.
        ICodeNode rootNode = parseTerm(token);

        // Was there a leading - sign?
        if (signType == MINUS) {

            // Create a NEGATE node and adopt the current tree
            // as its child. The NEGATE node becomes the new root node.
            ICodeNode negateNode = ICodeFactory.createICodeNode(NEGATE);
            negateNode.addChild(rootNode);
            rootNode = negateNode;
        }

        token = currentToken();
        tokenType = token.getType();

        // Loop over additive operators.
        while (ADD_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = ADD_OPS_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse another term.  The operator node adopts
            // the term's tree as its second child.
            opNode.addChild(parseTerm(token));

            // The operator node becomes the new root node.
            rootNode = opNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    // Set of multiplicative operators.
    private static final EnumSet<SubsetCTokenType> MULT_OPS =
        EnumSet.of(STAR, SLASH, SubsetCTokenType.MOD, SubsetCTokenType.AND);

    // Map multiplicative operator tokens to node types.
    private static final HashMap<SubsetCTokenType, ICodeNodeType>
        MULT_OPS_OPS_MAP = new HashMap<>();
    static {
        MULT_OPS_OPS_MAP.put(STAR, MULTIPLY);
        MULT_OPS_OPS_MAP.put(SLASH, FLOAT_DIVIDE);
        MULT_OPS_OPS_MAP.put(SubsetCTokenType.MOD, ICodeNodeTypeImpl.MOD);
        MULT_OPS_OPS_MAP.put(SubsetCTokenType.AND, ICodeNodeTypeImpl.AND);
    };

    /**
     * Parse a term.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseTerm(Token token)
        throws Exception
    {
        // Parse a factor and make its node the root node.
        ICodeNode rootNode = parseFactor(token);

        token = currentToken();
        TokenType tokenType = token.getType();

        // Loop over multiplicative operators.
        while (MULT_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = MULT_OPS_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse another factor.  The operator node adopts
            // the term's tree as its second child.
            opNode.addChild(parseFactor(token));

            // The operator node becomes the new root node.
            rootNode = opNode;

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    /**
     * Parse a factor.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseFactor(Token token)
        throws Exception
    {
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        switch ((SubsetCTokenType) tokenType) {

            case IDENTIFIER: {
                // Look up the identifier in the symbol table stack.
                // Flag the identifier as undefined if it's not found.
                String name = token.getText().toLowerCase();
                SymTabEntry id = symTabStack.lookup(name);
                if (id == null) {
                    errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
                    id = symTabStack.enterLocal(name);
                }

                rootNode = ICodeFactory.createICodeNode(VARIABLE);
                rootNode.setAttribute(ID, id);
                id.appendLineNumber(token.getLineNumber());

                token = nextToken();  // consume the identifier
                break;
            }

            case INT: {
                // Create an INTEGER_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number
                break;
            }

            case FLOAT: {
                // Create an REAL_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number
                break;
            }
            
            case TRUE:
            case FALSE: {
            	rootNode = ICodeFactory.createICodeNode(BOOLEAN_CONSTANT);
            	rootNode.setAttribute(VALUE, tokenType == TRUE ? true : false);
            	
            	token = nextToken();
            	break;
            }

            case STRING: {
                String value = (String) token.getValue();

                // Create a STRING_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
                rootNode.setAttribute(VALUE, value);

                token = nextToken();  // consume the string
                break;
            }

            case LEFT_PAREN: {
                token = nextToken();      // consume the (

                // Parse an expression and make its node the root node.
                rootNode = parseExpression(token);

                // Look for the matching ) token.
                token = currentToken();
                if (token.getType() == RIGHT_PAREN) {
                    token = nextToken();  // consume the )
                }
                else {
                    errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
                }

                break;
            }

            default: {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
                break;
            }
        }

        return rootNode;
    }
}
