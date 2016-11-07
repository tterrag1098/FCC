package wci.frontend.subsetc;

import static wci.frontend.subsetc.SubsetCErrorCode.IO_ERROR;
import static wci.frontend.subsetc.SubsetCErrorCode.MISSING_PERIOD;
import static wci.frontend.subsetc.SubsetCErrorCode.UNEXPECTED_TOKEN;
import static wci.frontend.subsetc.SubsetCTokenType.LEFT_BRACE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_ICODE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_SYMTAB;

import java.util.EnumSet;

import wci.frontend.EofToken;
import wci.frontend.Parser;
import wci.frontend.Scanner;
import wci.frontend.Token;
import wci.frontend.subsetc.parsers.BlockParser;
import wci.intermediate.ICode;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.Predefined;
import wci.message.Message;
import static wci.message.MessageType.PARSER_SUMMARY;

/**
 * <h1>PascalParserTD</h1>
 *
 * <p>The top-down Pascal parser.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubsetCParserTD extends Parser
{
    protected static SubsetCErrorHandler errorHandler = new SubsetCErrorHandler();

    private SymTabEntry routineId;  // name of the routine being parsed

    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public SubsetCParserTD(Scanner scanner)
    {
        super(scanner);
    }
    /**
     * 
     * Constructor for subclasses.
     * @param parent the parent parser.
     */
    public SubsetCParserTD(SubsetCParserTD parent)
    {
        super(parent.getScanner());
    }

    /**
     * Parse a Pascal source program and generate the symbol table
     * and the intermediate code.
     * @throws Exception if an error occurred.
     */
    public void parse()
        throws Exception
    {
        long startTime = System.currentTimeMillis();

        ICode iCode = ICodeFactory.createICode();
        Predefined.initialize(symTabStack);

        // Create a dummy program identifier symbol table entry.
        routineId = symTabStack.enterLocal("DummyProgramName".toLowerCase());
        routineId.setDefinition(DefinitionImpl.PROGRAM);
        symTabStack.setProgramId(routineId);

        // Push a new symbol table onto the symbol table stack and set
        // the routine's symbol table and intermediate code.
        routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
        routineId.setAttribute(ROUTINE_ICODE, iCode);
        
        BlockParser blockParser = new BlockParser(this);

        try {
            Token token = nextToken();

            // Parse a block.
            ICodeNode rootNode = blockParser.parse(token, routineId);
            iCode.setRoot(rootNode);
            symTabStack.pop();

            token = currentToken();

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(PARSER_SUMMARY,
                                    new Number[] {token.getLineNumber(),
                                                  getErrorCount(),
                                                  elapsedTime}));
        }
        catch (java.io.IOException ex) {
            errorHandler.abortTranslation(IO_ERROR, this);
        }
    }

    /**
     * Return the number of syntax errors found by the parser.
     * @return the error count.
     */
    public int getErrorCount()
    {
        return errorHandler.getErrorCount();
    }

    /**
     * Synchronize the parser.
     * @param syncSet the set of token types for synchronizing the parser.
     * @return the token where the parser has synchronized.
     * @throws Exception if an error occurred.
     */
    public Token synchronize(EnumSet<SubsetCTokenType> syncSet)
        throws Exception
    {
        Token token = currentToken();

        // If the current token is not in the synchronization set,
        // then it is unexpected and the parser must recover.
        if (!syncSet.contains(token.getType())) {

            // Flag the unexpected token.
            errorHandler.flag(token, UNEXPECTED_TOKEN, this);

            // Recover by skipping tokens that are not
            // in the synchronization set.
            do {
                token = nextToken();
            } while (!(token instanceof EofToken) &&
                     !syncSet.contains(token.getType()));
       }

       return token;
    }
}
