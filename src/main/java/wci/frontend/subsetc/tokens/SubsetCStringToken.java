package wci.frontend.subsetc.tokens;

import static wci.frontend.Source.*;
import static wci.frontend.subsetc.SubsetCErrorCode.UNEXPECTED_EOF;
import static wci.frontend.subsetc.SubsetCTokenType.ERROR;
import static wci.frontend.subsetc.SubsetCTokenType.STRING;
import wci.frontend.Source;
import wci.frontend.subsetc.SubsetCToken;

/**
 * <h1>PascalStringToken</h1>
 *
 * <p> Pascal string tokens.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubsetCStringToken extends SubsetCToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public SubsetCStringToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Pascal string token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial quote
        textBuffer.append('"');

        // Get string characters.
        do {
            // Replace any whitespace character with a blank.
            if (Character.isWhitespace(currentChar)) {
                currentChar = ' ';
            }

            if (currentChar != '"' && currentChar != '\\' && currentChar != EOF) {
                textBuffer.append(currentChar);
                valueBuffer.append(currentChar);
                currentChar = nextChar();  // consume character
            }

            // Quote?  Each pair of backslash/quote represents a quote.
            if (currentChar == '\\') {
            	if (peekChar() == EOL) {
            		textBuffer.append("\\\\n");
            		valueBuffer.append(" ");
				} else if (peekChar() == '"') {
					textBuffer.append("\\\"");
					valueBuffer.append(peekChar()); // append quote
				}
				currentChar = nextChar(); // consume escape
				currentChar = nextChar();
            }
        } while ((currentChar != '"') && (currentChar != EOF));

        if (currentChar == '"') {
            nextChar();  // consume final quote
            textBuffer.append('"');

            type = STRING;
            value = valueBuffer.toString();
        }
        else {
            type = ERROR;
            value = UNEXPECTED_EOF;
        }

        text = textBuffer.toString();
    }
}
