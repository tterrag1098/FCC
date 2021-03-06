package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCErrorCode.MISSING_BEGIN;
import static wci.frontend.subsetc.SubsetCErrorCode.MISSING_END;
import static wci.frontend.subsetc.SubsetCTokenType.LEFT_BRACE;
import static wci.frontend.subsetc.SubsetCTokenType.RIGHT_BRACE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;

/**
 * <h1>BlockParser</h1>
 *
 * <p>Parse a Pascal block.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class BlockParser extends SubsetCParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public BlockParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a block.
     * @param token the initial token.
     * @param routineId the symbol table entry of the routine name.
     * @return the root node of the parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry routineId)
        throws Exception
    {
        StatementParser statementParser = new StatementParser(this);

        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        // Look for the BEGIN token to parse a compound statement.
        if (tokenType == LEFT_BRACE) {
            rootNode = statementParser.parse(token, routineId);
        }

        // Missing BEGIN: Attempt to parse anyway if possible.
        else {
            errorHandler.flag(token, MISSING_BEGIN, this);

            if (StatementParser.STMT_START_SET.contains(tokenType)) {
                rootNode = ICodeFactory.createICodeNode(COMPOUND);
                statementParser.parseList(token, rootNode, routineId, RIGHT_BRACE, MISSING_END);
            }
        }

        return rootNode;
    }
}
