package wci.frontend;

import wci.frontend.pascal.PascalParserTD;
import wci.frontend.pascal.PascalScanner;
import wci.frontend.subsetc.SubsetCParserTD;
import wci.frontend.subsetc.SubsetCScanner;

/**
 * <h1>FrontendFactory</h1>
 *
 * <p>A factory class that creates parsers for specific source languages.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class FrontendFactory
{
    /**
     * Create a parser.
     * @param language the name of the source language (e.g., "Pascal").
     * @param type the type of parser (e.g., "top-down").
     * @param source the source object.
     * @return the parser.
     * @throws Exception if an error occurred.
     */
    public static Parser createParser(String language, String type,
                                      Source source)
        throws Exception
    {
    	Scanner scanner;
    	switch(language) {
    	case "Pascal":
            scanner = new PascalScanner(source);
            return new PascalParserTD(scanner);
    	case "SubsetC": 
    		scanner = new SubsetCScanner(source);
    		return new SubsetCParserTD(scanner);
    	default:
            throw new Exception("Parser factory: Invalid language '" +
                    language + "'");
    	}
    }
}
