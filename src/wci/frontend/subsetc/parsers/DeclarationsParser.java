package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCTokenType.*;
import static wci.frontend.subsetc.SubsetCErrorCode.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

import java.util.EnumSet;

import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.symtabimpl.DefinitionImpl;

/**
 * <h1>DeclarationsParser</h1>
 *
 * <p>Parse Pascal declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class DeclarationsParser extends SubsetCParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public DeclarationsParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<SubsetCTokenType> DECLARATION_START_SET =
        EnumSet.of(INT, FLOAT, VOID, IDENTIFIER);

//    static final EnumSet<SubsetCTokenType> TYPE_START_SET =
//        DECLARATION_START_SET.clone();
//    static {
//        TYPE_START_SET.remove(CONST);
//    }

    static final EnumSet<SubsetCTokenType> VAR_START_SET = EnumSet.of(IDENTIFIER);
//        TYPE_START_SET.clone();
//    static {
//        VAR_START_SET.remove(TYPE);
//    }

//    static final EnumSet<SubsetCTokenType> ROUTINE_START_SET =
//        VAR_START_SET.clone();
//    static {
//        ROUTINE_START_SET.remove(VAR);
//    }

    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public void parse(Token token)
        throws Exception
    {
        token = synchronize(DECLARATION_START_SET);

//        if (token.getType() == CONST) {
//            token = nextToken();  // consume CONST
//
//            ConstantDefinitionsParser constantDefinitionsParser =
//                new ConstantDefinitionsParser(this);
//            constantDefinitionsParser.parse(token);
//        }

//        token = synchronize(TYPE_START_SET);
//
//        if (token.getType() == TYPE) {
//            token = nextToken();  // consume TYPE
//
//            TypeDefinitionsParser typeDefinitionsParser =
//                new TypeDefinitionsParser(this);
//            typeDefinitionsParser.parse(token);
//        }

        token = synchronize(VAR_START_SET);

        while (VAR_START_SET.contains(token.getType()) && symTabStack.lookup(token.getText()).getDefinition() == DefinitionImpl.TYPE) {
            VariableDeclarationParser variableDeclarationsParser =
                new VariableDeclarationParser(this);
            variableDeclarationsParser.setDefinition(VARIABLE);
            variableDeclarationsParser.parse(token);
            
            token = currentToken();
        }

//        token = synchronize(ROUTINE_START_SET);
    }
}
