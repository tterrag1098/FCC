package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCErrorCode.IDENTIFIER_UNDEFINED;
import static wci.frontend.subsetc.SubsetCErrorCode.INVALID_TYPE;
import static wci.frontend.subsetc.SubsetCErrorCode.NOT_TYPE_IDENTIFIER;
import static wci.frontend.subsetc.SubsetCTokenType.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.CONSTANT;
import static wci.intermediate.symtabimpl.DefinitionImpl.ENUMERATION_CONSTANT;

import java.util.EnumSet;

import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.Definition;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.DefinitionImpl;

/**
 * <h1>SimpleTypeParser</h1>
 *
 * <p>Parse a simple Pascal type (identifier, subrange, enumeration)
 * specification.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
class SimpleTypeParser extends TypeSpecificationParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected SimpleTypeParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a simple type specification.
    static final EnumSet<SubsetCTokenType> SIMPLE_TYPE_START_SET = EnumSet.noneOf(SubsetCTokenType.class);
//        ConstantDefinitionsParser.CONSTANT_START_SET.clone();
    static {
//        SIMPLE_TYPE_START_SET.add(LEFT_PAREN);
//        SIMPLE_TYPE_START_SET.add(COMMA);
        SIMPLE_TYPE_START_SET.add(IDENTIFIER);
    }

    /**
     * Parse a simple Pascal type specification.
     * @param token the current token.
     * @return the simple type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token)
        throws Exception
    {
        // Synchronize at the start of a simple type specification.
        token = synchronize(SIMPLE_TYPE_START_SET);

        switch ((SubsetCTokenType) token.getType()) {

            case IDENTIFIER: {
                String name = token.getText().toLowerCase();
                SymTabEntry id = symTabStack.lookup(name);

                if (id != null) {
                    Definition definition = id.getDefinition();

                    // It's either a type identifier
                    // or the start of a subrange type.
                    if (definition == DefinitionImpl.TYPE) {
                        id.appendLineNumber(token.getLineNumber());
                        token = nextToken();  // consume the identifier

                        // Return the type of the referent type.
                        return id.getTypeSpec();
                    }
                    else if ((definition != CONSTANT) &&
                             (definition != ENUMERATION_CONSTANT)) {
                        errorHandler.flag(token, NOT_TYPE_IDENTIFIER, this);
                        token = nextToken();  // consume the identifier
                        return null;
                    }
                    else {
//                        SubrangeTypeParser subrangeTypeParser =
//                            new SubrangeTypeParser(this);
//                        return subrangeTypeParser.parse(token);
                    }
                }
                else {
                    errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
                    token = nextToken();  // consume the identifier
                    return null;
                }
            }

//            case LEFT_PAREN: {
//                EnumerationTypeParser enumerationTypeParser =
//                    new EnumerationTypeParser(this);
//                return enumerationTypeParser.parse(token);
//            }

            case COMMA:
            case SEMICOLON: {
                errorHandler.flag(token, INVALID_TYPE, this);
                return null;
            }
            
            default: return null;

//            default: {
//                SubrangeTypeParser subrangeTypeParser =
//                    new SubrangeTypeParser(this);
//                return subrangeTypeParser.parse(token);
//            }
        }
    }
}
