package wci.frontend.subsetc.parsers;

import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.intermediate.SymTabEntry;

/**
 * <h1>ProgramParser</h1>
 *
 * <p>Parse a Pascal program.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ProgramParser extends DeclarationsParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public ProgramParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a program.
     * @param token the initial token.
     * @param parentId the symbol table entry of the parent routine's name.
     * @return null
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        // Parse the program.
    	DeclarationsParser declarationsParser = new DeclarationsParser(this);
    	declarationsParser.parse(token, parentId);

        return null;
    }
}
