package wci.frontend.subsetc;

import static wci.frontend.subsetc.SubsetCErrorCode.*;
import static wci.message.MessageType.SYNTAX_ERROR;
import wci.frontend.Parser;
import wci.frontend.Token;
import wci.frontend.pascal.PascalErrorCode;
import wci.message.Message;

/**
 * <h1>PascalErrorHandler</h1>
 *
 * <p>Error handler Pascal syntax errors.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubsetCErrorHandler
{
    private static final int MAX_ERRORS = 25;

    private static int errorCount = 0;   // count of syntax errors

    /**
     * Getter.
     * @return the syntax error count.
     */
    public int getErrorCount()
    {
        return errorCount;
    }

    /**
     * Flag an error in the source line.
     * @param token the bad token.
     * @param errorCode the error code.
     * @param parser the parser.
     * @return the flagger string.
     */
    public void flag(Token token, Object errorCode, Parser parser)
    {
        // Notify the parser's listeners.
        parser.sendMessage(new Message(SYNTAX_ERROR,
                                       new Object[] {token.getLineNumber(),
                                                     token.getPosition(),
                                                     token.getText(),
                                                     errorCode.toString()}));

        if (++errorCount > MAX_ERRORS) {
            abortTranslation(TOO_MANY_ERRORS, parser);
        }
    }

    /**
     * Abort the translation.
     * @param errorCode the error code.
     * @param parser the parser.
     */
    public void abortTranslation(SubsetCErrorCode errorCode, Parser parser)
    {
        // Notify the parser's listeners and then abort.
        String fatalText = "FATAL ERROR: " + errorCode.toString();
        parser.sendMessage(new Message(SYNTAX_ERROR,
                                       new Object[] {0,
                                                     0,
                                                     "",
                                                     fatalText}));
        System.exit(errorCode.getStatus());
    }
}
