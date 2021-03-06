package wci.frontend.subsetc.tokens;

import static wci.frontend.subsetc.SubsetCErrorCode.INVALID_CHARACTER;
import static wci.frontend.subsetc.SubsetCTokenType.ERROR;
import static wci.frontend.subsetc.SubsetCTokenType.SPECIAL_SYMBOLS;
import wci.frontend.Source;
import wci.frontend.subsetc.SubsetCToken;

/**
 * <h1>PascalSpecialSymbolToken</h1>
 *
 * <p> Pascal special symbol tokens.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubsetCSpecialSymbolToken extends SubsetCToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public SubsetCSpecialSymbolToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Pascal special symbol token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        char currentChar = currentChar();

        text = Character.toString(currentChar);
        type = null;

        switch (currentChar) {

            // Single-character special symbols.
            case '+':  case '-':  case '*':  case '/':  case ',':
            case ';':  case '\'': case '(':  case ')':  case '[':  
            case ']':  case '{':  case '}':  case '^':  case ':': {
                nextChar();  // consume character
                break;
            }

            case '=':
            case '!':
            case '<':
            case '>': {
                currentChar = nextChar();  // consume first char

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            case '&': 
            case '|': {
            	char nextChar = nextChar(); // consume first '&' or '|'
            	if (currentChar == nextChar) {
            		text += currentChar;
            		nextChar();
            	}
            	break;
            }

            default: {
                nextChar();  // consume bad character
                type = ERROR;
                value = INVALID_CHARACTER;
            }
        }

        // Set the type if it wasn't an error.
        if (type == null) {
            type = SPECIAL_SYMBOLS.get(text);
        }
    }
}
