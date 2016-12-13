package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCTokenType.FLOAT;
import static wci.frontend.subsetc.SubsetCTokenType.IDENTIFIER;
import static wci.frontend.subsetc.SubsetCTokenType.INT;
import static wci.frontend.subsetc.SubsetCTokenType.VOID;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

import java.util.EnumSet;

import wci.frontend.EofToken;
import wci.frontend.Token;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;
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

    static final EnumSet<SubsetCTokenType> ROUTINE_START_SET =
        VAR_START_SET.clone();
    static {
        ROUTINE_START_SET.add(VOID);
    }

    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry entry)
        throws Exception
    {
    	do {
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
			// }

			SymTabEntry varType = symTabStack.lookup(token.getText());
			if (VAR_START_SET.contains(token.getType()) && varType != null
					&& varType.getDefinition() == DefinitionImpl.TYPE) {
				VariableDeclarationParser declarationParser = new VariableDeclarationParser(this);
				declarationParser.setDefinition(VARIABLE);
				declarationParser.parse(token, entry);
			} else {

				token = synchronize(ROUTINE_START_SET);
				
				DeclaredRoutineParser routineParser = new DeclaredRoutineParser(this);
				routineParser.parse(token, entry);
			}

			token = currentToken();
    	} while (!(token instanceof EofToken));
	        
        return null;
    }
}
