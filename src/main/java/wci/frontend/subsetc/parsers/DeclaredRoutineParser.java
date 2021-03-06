package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCErrorCode.*;
import static wci.frontend.subsetc.SubsetCTokenType.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.symtabimpl.RoutineCodeImpl.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.subsetc.SubsetCErrorCode;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCTokenType;
import wci.intermediate.Definition;
import wci.intermediate.ICode;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeForm;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.Predefined;
import wci.intermediate.symtabimpl.SymTabKeyImpl;
import wci.intermediate.typeimpl.TypeFormImpl;
import wci.util.CrossReferencer;

/**
 * <h1>DeclaredRoutineParser</h1>
 *
 * <p>Parse a main program routine or a declared procedure or function.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class DeclaredRoutineParser extends DeclarationsParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public DeclaredRoutineParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    private static int dummyCounter = 0;  // counter for dummy routine names
    
    private TypeSpec returnType;
    
    public void setReturnType(TypeSpec type) {
    	this.returnType = type;
    }

    /**
     * Parse a standard subroutine declaration.
     * @param token the initial token.
     * @param parentId the symbol table entry of the parent routine's name.
     * @return the symbol table entry of the declared routine's name.
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        Definition routineDefn = null;
        String dummyName = null;
        SymTabEntry routineId = null;
        TokenType routineType = token.getType();

        // Initialize.
        switch ((SubsetCTokenType) routineType) {

            case VOID: {
                token = nextToken();  // consume PROCEDURE
                routineDefn = DefinitionImpl.PROCEDURE;
                dummyName = "DummyProcedureName_".toLowerCase() +
                            String.format("%03d", ++dummyCounter);
                break;
            }

            case IDENTIFIER: {
                routineDefn = DefinitionImpl.FUNCTION;
                dummyName = "DummyFunctionName_".toLowerCase() +
                            String.format("%03d", ++dummyCounter);
                break;
            }

            default: {
                routineDefn = DefinitionImpl.PROGRAM;
                dummyName = "DummyProgramName".toLowerCase();
                break;
            }
        }

        // Parse the routine name.
        routineId = parseRoutineName(token, dummyName);
        routineId.setDefinition(routineDefn);

        token = currentToken();

        // Create new intermediate code for the routine.
        ICode iCode = ICodeFactory.createICode();
        routineId.setAttribute(ROUTINE_ICODE, iCode);
        routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<SymTabEntry>());

		routineId.setAttribute(SymTabKeyImpl.ROUTINE_SYMTAB, symTabStack.push());
		
        // Program: Set the program identifier in the symbol table stack.
        if (routineId.getName().equals("main") && routineDefn == DefinitionImpl.FUNCTION) {
            symTabStack.getProgramId().setAttribute(ROUTINE_ICODE, routineId.getAttribute(ROUTINE_ICODE));
            symTabStack.getProgramId().setAttribute(MAIN_METHOD_ROUTINE, routineId);
            symTabStack.getLocalSymTab().nextSlotNumber();  // bump slot number
        }

        // Non-forwarded procedure or function: Append to the parent's list
        //                                      of routines.
        else if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
            ArrayList<SymTabEntry> subroutines = (ArrayList<SymTabEntry>)
                                       parentId.getAttribute(ROUTINE_ROUTINES);
            subroutines.add(routineId);
        }

        // If the routine was forwarded, there should not be
        // any formal parameters or a function return type.
        // But parse them anyway if they're there.
        if (routineId.getAttribute(ROUTINE_CODE) == FORWARD) {
            if (token.getType() != SEMICOLON) {
                errorHandler.flag(token, ALREADY_FORWARDED, this);
                parseHeader(token, routineId);
            }
        }

        // Parse the routine's formal parameters and function return type.
        else {
            parseHeader(token, routineId);
        }

        // Look for the semicolon.
        token = currentToken();
        if (token.getType() == LEFT_BRACE) {
            
            routineId.setAttribute(ROUTINE_CODE, DECLARED);

            BlockParser blockParser = new BlockParser(this);
            ICodeNode rootNode = blockParser.parse(token, routineId);
            iCode.setRoot(rootNode);
        }
        

        CrossReferencer cr = new CrossReferencer();
        PrintStream replace = new PrintStream(System.out) {
        	@Override
        	public void println(String x) {
        		nestedStacks.append(x + "\n");
        	}
        	
        	public void print(String s) {
        		nestedStacks.append(s);
        	}
        	
        	@Override
        	public void println() {
        		nestedStacks.append("\n");
        	}
        };
        
        SymTab blockstack = symTabStack.pop();

        PrintStream replaced = System.out;
        System.setOut(replace);
        nestedStacks.append("\n=== NESTED SYMBOL TABLE ===\n\n");
        cr.printSymTab(blockstack, new ArrayList<>());
        System.setOut(replaced);
        nestedStacks.append("\n");

        return routineId;
    }

    /**
     * Parse a routine's name.
     * @param token the current token.
     * @param routineDefn how the routine is defined.
     * @param dummyName a dummy name in case of parsing problem.
     * @return the symbol table entry of the declared routine's name.
     * @throws Exception if an error occurred.
     */
    private SymTabEntry parseRoutineName(Token token, String dummyName)
        throws Exception
    {
        SymTabEntry routineId = null;

        // Parse the routine name identifier.
        if (token.getType() == IDENTIFIER) {
            String routineName = token.getText().toLowerCase();
            routineId = symTabStack.lookupLocal(routineName);

            // Not already defined locally: Enter into the local symbol table.
            if (routineId == null) {
                routineId = symTabStack.enterLocal(routineName);
            }

            // If already defined, it should be a forward definition.
            else if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
                routineId = null;
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }
            if (token == currentToken()) token = nextToken();
            token = synchronize(EnumSet.of(LEFT_PAREN)); // consume routine name identifier
        }
        else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }

        // If necessary, create a dummy routine name symbol table entry.
        if (routineId == null) {
            routineId = symTabStack.enterLocal(dummyName);
        }

        return routineId;
    }

    /**
     * Parse a routine's formal parameter list and the function return type.
     * @param token the current token.
     * @param routineId the symbol table entry of the declared routine's name.
     * @throws Exception if an error occurred.
     */
    private void parseHeader(Token token, SymTabEntry routineId)
        throws Exception
    {
        // Parse the routine's formal parameters.
        parseFormalParameters(token, routineId);
        token = currentToken();

        // If this is a function, parse and set its return type.
		if (routineId.getDefinition() == DefinitionImpl.FUNCTION) {
			if (returnType == null) {
				VariableDeclarationParser variableDeclarationsParser = new VariableDeclarationParser(this);
				variableDeclarationsParser.setDefinition(DefinitionImpl.FUNCTION);
				returnType = variableDeclarationsParser.parseTypeSpec(token);
			}

            token = currentToken();

            // The return type cannot be an array or record.
            if (returnType != null) {
                TypeForm form = returnType.getForm();
                if ((form == TypeFormImpl.ARRAY) ||
                    (form == TypeFormImpl.RECORD))
                {
                    errorHandler.flag(token, INVALID_TYPE, this);
                }
            }

            // Missing return type.
            else {
                returnType = Predefined.undefinedType;
            }

            routineId.setTypeSpec(returnType);
            token = currentToken();
        }
    }

    // Synchronization set for a formal parameter sublist.
    private static final EnumSet<SubsetCTokenType> PARAMETER_SET =
        DeclarationsParser.DECLARATION_START_SET.clone();
    static {
        PARAMETER_SET.add(IDENTIFIER);
        PARAMETER_SET.add(RIGHT_PAREN);
    }

    // Synchronization set for the opening left parenthesis.
    private static final EnumSet<SubsetCTokenType> LEFT_PAREN_SET =
        DeclarationsParser.DECLARATION_START_SET.clone();
    static {
        LEFT_PAREN_SET.add(LEFT_PAREN);
        LEFT_PAREN_SET.add(SEMICOLON);
        LEFT_PAREN_SET.add(COLON);
    }

    // Synchronization set for the closing right parenthesis.
    private static final EnumSet<SubsetCTokenType> RIGHT_PAREN_SET =
        LEFT_PAREN_SET.clone();
    static {
        RIGHT_PAREN_SET.remove(LEFT_PAREN);
        RIGHT_PAREN_SET.add(RIGHT_PAREN);
    }

    /**
     * Parse a routine's formal parameter list.
     * @param token the current token.
     * @param routineId the symbol table entry of the declared routine's name.
     * @throws Exception if an error occurred.
     */
    protected void parseFormalParameters(Token token, SymTabEntry routineId)
        throws Exception
    {
        // Parse the formal parameters if there is an opening left parenthesis.
        token = synchronize(LEFT_PAREN_SET);
        if (token.getType() == LEFT_PAREN) {
            token = nextToken();  // consume (

            ArrayList<SymTabEntry> parms = new ArrayList<SymTabEntry>();

            token = synchronize(PARAMETER_SET);
            TokenType tokenType = token.getType();

            // Loop to parse sublists of formal parameter declarations.
            while ((tokenType == IDENTIFIER)) {
                parms.add(parseParm(token, routineId));
                token = currentToken();
                tokenType = token.getType();
            }

            // Closing right parenthesis.
            if (token.getType() == RIGHT_PAREN) {
                token = nextToken();  // consume )
            }
            else {
                errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
            }

            routineId.setAttribute(ROUTINE_PARMS, parms);
        }
    }

    // Synchronization set to follow a formal parameter identifier.
    private static final EnumSet<SubsetCTokenType> PARAMETER_FOLLOW_SET =
        EnumSet.of(COLON, RIGHT_PAREN, SEMICOLON);
    static {
        PARAMETER_FOLLOW_SET.addAll(DeclarationsParser.DECLARATION_START_SET);
    }

    // Synchronization set for the , token.
    private static final EnumSet<SubsetCTokenType> COMMA_SET =
        EnumSet.of(COMMA, SEMICOLON, RIGHT_PAREN);

    /**
     * Parse a sublist of formal parameter declarations.
     * @param token the current token.
     * @param routineId the symbol table entry of the declared routine's name.
     * @return the sublist of symbol table entries for the parm identifiers.
     * @throws Exception if an error occurred.
     */
    private SymTabEntry parseParm(Token token,
                                                    SymTabEntry routineId)
        throws Exception
    {
        TypeSpec type;

        VariableDeclarationParser variableDeclarationsParser =
	            new VariableDeclarationParser(this);
        
        if (token.getType() != IDENTIFIER) {
        	errorHandler.flag(token, SubsetCErrorCode.INVALID_TYPE, this);
        	return null;
        } else {
	        // Parse the parameter sublist and its type specification.
	        variableDeclarationsParser.setDefinition(VALUE_PARM);
        	type = variableDeclarationsParser.parseTypeSpec(token);
        }
        token = currentToken();
        if (token.getType() != IDENTIFIER) {
        	errorHandler.flag(token, SubsetCErrorCode.MISSING_IDENTIFIER, this);
        	synchronize(PARAMETER_FOLLOW_SET);
        	return null;
        }
        
    	SymTabEntry parm = variableDeclarationsParser.parseIdentifier(token);
    	parm.setTypeSpec(type);

        token = nextToken();

        token = synchronize(COMMA_SET);
        if (token.getType() == COMMA) {
        	token = nextToken(); // Consume commma
        }

        return parm;
    }
}
