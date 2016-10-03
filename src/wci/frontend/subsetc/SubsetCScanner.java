package wci.frontend.subsetc;

import static wci.frontend.Source.*;
import wci.frontend.EofToken;
import wci.frontend.Scanner;
import wci.frontend.Source;
import wci.frontend.Token;
import wci.frontend.subsetc.tokens.SubsetCErrorToken;
import wci.frontend.subsetc.tokens.SubsetCNumberToken;
import wci.frontend.subsetc.tokens.SubsetCSpecialSymbolToken;
import wci.frontend.subsetc.tokens.SubsetCStringToken;
import wci.frontend.subsetc.tokens.SubsetCWordToken;
import static wci.frontend.subsetc.SubsetCErrorCode.*;

/**
 * <h1>PascalScanner</h1>
 *
 * <p>The Pascal scanner.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubsetCScanner extends Scanner
{
    /**
     * Constructor
     * @param source the source to be used with this scanner.
     */
    public SubsetCScanner(Source source)
    {
        super(source);
    }

    /**
     * Extract and return the next Pascal token from the source.
     * @return the next token.
     * @throws Exception if an error occurred.
     */
    protected Token extractToken()
        throws Exception
    {
        skipWhiteSpace();

        Token token;
        char currentChar = currentChar();

        // Construct the next token.  The current character determines the
        // token type.
        if (currentChar == EOF) {
            token = new EofToken(source);
        }
        else if (Character.isLetter(currentChar)) {
            token = new SubsetCWordToken(source);
        }
        else if (Character.isDigit(currentChar)) {
            token = new SubsetCNumberToken(source);
        }
        else if (currentChar == '\"') {
            token = new SubsetCStringToken(source);
        }
        else if (SubsetCTokenType.SPECIAL_SYMBOLS
                 .containsKey(Character.toString(currentChar))) {
            token = new SubsetCSpecialSymbolToken(source);
        }
        else {
            token = new SubsetCErrorToken(source, INVALID_CHARACTER,
                                         Character.toString(currentChar));
            nextChar();  // consume character
        }

        return token;
    }

    /**
     * Skip whitespace characters by consuming them.  A comment is whitespace.
     * @throws Exception if an error occurred.
     */
    private void skipWhiteSpace()
        throws Exception
    {
        char currentChar = currentChar();

        while (Character.isWhitespace(currentChar) || (currentChar == '/')) {

            // Start of a comment?
            if (currentChar == '/') {
            	currentChar = nextChar();
            	if (currentChar == '*') {
            		do {
            			currentChar = nextChar();
            			if (currentChar == '*') {
            				currentChar = nextChar();
            			}
            		} while (currentChar != '/' && currentChar != EOF);
            	} else if (currentChar == '/') {
            		do {
            			currentChar = nextChar();
            		} while (currentChar != EOL && currentChar != EOF);
            	}                
            }

            // Not a comment.
            else {
                currentChar = nextChar();  // consume whitespace character
            }
        }
    }
}
