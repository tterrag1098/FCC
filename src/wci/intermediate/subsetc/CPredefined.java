package wci.intermediate.subsetc;

import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;

import static wci.intermediate.typeimpl.TypeFormImpl.*;

/**
 * <h1>Predefined</h1>
 *
 * <p>Enter the predefined Pascal types, identifiers, and constants
 * into the symbol table.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CPredefined
{
    // Predefined types.
    public static TypeSpec intType;
    public static TypeSpec floatType;
//    public static TypeSpec booleanType;
//    public static TypeSpec charType;
    public static TypeSpec undefinedType;

    // Predefined identifiers.
    public static SymTabEntry intId;
    public static SymTabEntry floatId;
//    public static SymTabEntry booleanId;
//    public static SymTabEntry charId;
//    public static SymTabEntry falseId;
//    public static SymTabEntry trueId;

    /**
     * Initialize a symbol table stack with predefined identifiers.
     * @param symTab the symbol table stack to initialize.
     */
    public static void initialize(SymTabStack symTabStack)
    {
        initializeTypes(symTabStack);
        initializeConstants(symTabStack);
    }

    /**
     * Initialize the predefined types.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeTypes(SymTabStack symTabStack)
    {
        // Type integer.
        intId = symTabStack.enterLocal("int");
        intType = TypeFactory.createType(SCALAR);
        intType.setIdentifier(intId);
        intId.setDefinition(DefinitionImpl.TYPE);
        intId.setTypeSpec(intType);

        // Type real.
        floatId = symTabStack.enterLocal("float");
        floatType = TypeFactory.createType(SCALAR);
        floatType.setIdentifier(floatId);
        floatId.setDefinition(DefinitionImpl.TYPE);
        floatId.setTypeSpec(floatType);

//        // Type boolean.
//        booleanId = symTabStack.enterLocal("boolean");
//        booleanType = TypeFactory.createType(ENUMERATION);
//        booleanType.setIdentifier(booleanId);
//        booleanId.setDefinition(DefinitionImpl.TYPE);
//        booleanId.setTypeSpec(booleanType);
//
//        // Type char.
//        charId = symTabStack.enterLocal("char");
//        charType = TypeFactory.createType(SCALAR);
//        charType.setIdentifier(charId);
//        charId.setDefinition(DefinitionImpl.TYPE);
//        charId.setTypeSpec(charType);

        // Undefined type.
        undefinedType = TypeFactory.createType(SCALAR);
    }

    /**
     * Initialize the predefined constant.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeConstants(SymTabStack symTabStack)
    {
//        // Boolean enumeration constant false.
//        falseId = symTabStack.enterLocal("false");
//        falseId.setDefinition(DefinitionImpl.ENUMERATION_CONSTANT);
//        falseId.setTypeSpec(booleanType);
//        falseId.setAttribute(CONSTANT_VALUE, new Integer(0));
//
//        // Boolean enumeration constant true.
//        trueId = symTabStack.enterLocal("true");
//        trueId.setDefinition(DefinitionImpl.ENUMERATION_CONSTANT);
//        trueId.setTypeSpec(booleanType);
//        trueId.setAttribute(CONSTANT_VALUE, new Integer(1));
//
//        // Add false and true to the boolean enumeration type.
//        ArrayList<SymTabEntry> constants = new ArrayList<SymTabEntry>();
//        constants.add(falseId);
//        constants.add(trueId);
//        booleanType.setAttribute(ENUMERATION_CONSTANTS, constants);
    }
}
