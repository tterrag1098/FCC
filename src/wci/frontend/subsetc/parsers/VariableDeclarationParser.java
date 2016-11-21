package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCErrorCode.*;
import static wci.frontend.subsetc.SubsetCTokenType.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.PROGRAM_PARM;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.Definition;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeSpec;

/**
 * <h1>VariableDeclarationsParser</h1>
 *
 * <p>Parse Pascal variable declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class VariableDeclarationParser extends DeclarationsParser
{
    private Definition definition;  // how to define the identifier

    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public VariableDeclarationParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    /**
     * Setter.
     * @param definition the definition to set.
     */
    protected void setDefinition(Definition definition)
    {
        this.definition = definition;
    }

    // Synchronization set for a variable identifier.
    static final EnumSet<SubsetCTokenType> IDENTIFIER_SET =
        DeclarationsParser.VAR_START_SET.clone();
    static {
        IDENTIFIER_SET.add(IDENTIFIER);
        IDENTIFIER_SET.add(SEMICOLON);
    }

    // Synchronization set for the start of the next definition or declaration.
    static final EnumSet<SubsetCTokenType> NEXT_START_SET = EnumSet.noneOf(SubsetCTokenType.class);
//        DeclarationsParser.ROUTINE_START_SET.clone();
    static {
        NEXT_START_SET.add(IDENTIFIER);
        NEXT_START_SET.add(SEMICOLON);
    }

    /**
     * Parse variable declarations.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        TypeSpec type = parseTypeSpec(token);

        token = synchronize(IDENTIFIER_SET);

        Token peek = nextToken();
        if (peek.getType() == LEFT_PAREN) {
        	DeclaredRoutineParser routineParser = new DeclaredRoutineParser(this);
        	routineParser.setReturnType(type);
        	return routineParser.parse(token, parentId);
        }
        
        // Loop to parse a sequence of variable declarations
        // separated by semicolons.
        while (token.getType() != SEMICOLON && token.getType() == IDENTIFIER) {

            // Parse the identifier sublist and its type specification.
            parseIdentifierSublist(token, type, IDENTIFIER_FOLLOW_SET, COMMA_SET);
            token = currentToken();
        }

        // Look for one or more semicolons after a definition.
        if (token.getType() == SEMICOLON) {
            while (token.getType() == SEMICOLON) {
                token = nextToken();  // consume the ;
            }
        }
        // If at the start of the next definition or declaration,
        // then missing a semicolon.
        else {
            errorHandler.flag(token, MISSING_SEMICOLON, this);
        }
        
        return parentId;
    }

    // Synchronization set to start a sublist identifier.
    static final EnumSet<SubsetCTokenType> IDENTIFIER_START_SET =
        EnumSet.of(IDENTIFIER, COMMA);

    // Synchronization set to follow a sublist identifier.
    private static final EnumSet<SubsetCTokenType> IDENTIFIER_FOLLOW_SET =
        EnumSet.of(COLON, SEMICOLON);

    // Synchronization set for the , token.
    private static final EnumSet<SubsetCTokenType> COMMA_SET =
        EnumSet.of(COMMA, SEMICOLON, RIGHT_PAREN);

    /**
     * Parse a sublist of identifiers and their type specification.
     * @param token the current token.
     * @param followSet the synchronization set to follow an identifier.
     * @return the sublist of identifiers in a declaration.
     * @throws Exception if an error occurred.
     */
    protected ArrayList<SymTabEntry> parseIdentifierSublist(
                                         Token token,
                                         TypeSpec type,
                                         EnumSet<SubsetCTokenType> followSet,
                                         EnumSet<SubsetCTokenType> commaSet)
        throws Exception
    {
        ArrayList<SymTabEntry> sublist = new ArrayList<SymTabEntry>();

        do {
//            token = synchronize(IDENTIFIER_START_SET);
            SymTabEntry id = parseIdentifier(token);
            token = currentToken();
            if (token.getType() == IDENTIFIER) token = nextToken();

            if (id != null) {
                sublist.add(id);
            }

            token = synchronize(commaSet);
            TokenType tokenType = token.getType();

            // Look for the comma.
            if (tokenType == COMMA) {
                token = nextToken();  // consume the comma

                if (followSet.contains(token.getType())) {
                    errorHandler.flag(token, MISSING_IDENTIFIER, this);
                }
            }
            else if (IDENTIFIER_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_COMMA, this);
            }
        } while (!followSet.contains(token.getType()));

        if (definition != PROGRAM_PARM) {

            // Assign the type specification to each identifier in the list.
            for (SymTabEntry variableId : sublist) {
                variableId.setTypeSpec(type);
            }
        }

        return sublist;
    }
    
    /**
     * Parse an identifier.
     * @param token the current token.
     * @return the symbol table entry of the identifier.
     * @throws Exception if an error occurred.
     */
    private SymTabEntry parseIdentifier(Token token)
        throws Exception
    {
        SymTabEntry id = null;

        if (token.getType() == IDENTIFIER) {
            String name = token.getText().toLowerCase();
            id = symTabStack.lookupLocal(name);

            // Enter a new identifier into the symbol table.
            if (id == null) {
                id = symTabStack.enterLocal(name);
                id.setDefinition(definition);
                id.appendLineNumber(token.getLineNumber());
            }
            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }
        }
        else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }

        return id;
    }

    // Synchronization set for the : token.
    private static final EnumSet<SubsetCTokenType> COLON_SET =
        EnumSet.of(COLON, SEMICOLON);

    /**
     * Parse the type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurs.
     */
    protected TypeSpec parseTypeSpec(Token token)
        throws Exception
    {
        // Parse the type specification.
        TypeSpecificationParser typeSpecificationParser =
            new TypeSpecificationParser(this);
        TypeSpec type = typeSpecificationParser.parse(token);

        return type;
    }
}
