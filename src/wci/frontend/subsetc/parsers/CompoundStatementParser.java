package wci.frontend.subsetc.parsers;

import static wci.frontend.subsetc.SubsetCErrorCode.MISSING_END;
import static wci.frontend.subsetc.SubsetCTokenType.RIGHT_BRACE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import wci.frontend.Token;
import wci.frontend.pascal.PascalTokenType;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.util.CrossReferencer;

/**
 * <h1>CompoundStatementParser</h1>
 *
 * <p>Parse a Pascal compound statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CompoundStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public CompoundStatementParser(SubsetCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a compound statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        token = nextToken();  // consume the BEGIN

        // Create the COMPOUND node.
        ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);
        symTabStack.push();
        
        // Parse the statement list terminated by the END token.
        StatementParser statementParser = new StatementParser(this);
        statementParser.parseList(token, compoundNode, parentId, RIGHT_BRACE, MISSING_END);

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
        PrintStream replaced = System.out;
        System.setOut(replace);
        nestedStacks.append("\n=== NESTED SYMBOL TABLE ===\n\n");
        cr.printSymTab(symTabStack.pop(), new ArrayList<>());
        System.setOut(replaced);
        nestedStacks.append("\n");
        return compoundNode;
    }
}
