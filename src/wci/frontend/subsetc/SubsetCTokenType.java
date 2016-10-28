package wci.frontend.subsetc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import wci.frontend.TokenType;

/**
 * <h1>PascalTokenType</h1>
 *
 * <p>Pascal token types.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum SubsetCTokenType implements TokenType
{
    // Reserved words.
	INT, FLOAT,
	
	IF, ELSE, WHILE,
	
	TRUE, FALSE,
	
	VOID, RETURN,

    // Special symbols.
    PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"),
    COMMA(","), SEMICOLON(";"), COLON(":"), QUOTE("\""),
    EQUALS("="), NOT_EQUALS("!="), LESS_THAN("<"), LESS_EQUALS("<="),
    GREATER_EQUALS(">="), GREATER_THAN(">"), LEFT_PAREN("("), RIGHT_PAREN(")"),
    LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
    UP_ARROW("^"), MOD("%"),
    
    AND("&&"), OR("||"),

    STRING, // do we need this?
    
    IDENTIFIER, ERROR, END_OF_FILE;

    private static final int FIRST_RESERVED_INDEX = INT.ordinal();
    private static final int LAST_RESERVED_INDEX  = RETURN.ordinal();

    private static final int FIRST_SPECIAL_INDEX = PLUS.ordinal();
    private static final int LAST_SPECIAL_INDEX  = OR.ordinal();

    private String text;  // token text

    /**
     * Constructor.
     */
    SubsetCTokenType()
    {
        this.text = this.toString().toLowerCase(Locale.US);
    }

    /**
     * Constructor.
     * @param text the token text.
     */
    SubsetCTokenType(String text)
    {
        this.text = text;
    }

    /**
     * Getter.
     * @return the token text.
     */
    public String getText()
    {
        return text;
    }

    // Set of lower-cased Pascal reserved word text strings.
    public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
    static {
        SubsetCTokenType values[] = SubsetCTokenType.values();
        for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
            RESERVED_WORDS.add(values[i].getText().toLowerCase());
        }
    }

    // Hash table of Pascal special symbols.  Each special symbol's text
    // is the key to its Pascal token type.
    public static Map<String, SubsetCTokenType> SPECIAL_SYMBOLS = new HashMap<>();
    static {
        SubsetCTokenType values[] = SubsetCTokenType.values();
        for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i) {
            SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
        }
    }
}
