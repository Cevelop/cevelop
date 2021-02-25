package com.cevelop.conanator.tests.parser.support;

import org.junit.Before;
import org.junit.runner.RunWith;

import com.cevelop.conanator.utility.SectionParser;


@RunWith(ParserTestRunner.class)
public abstract class ParserTest<ParserType extends SectionParser<KeyType>, KeyType> {

    private String     fSourceText;
    private ParserType fParser;

    /**
     * Get the 'conanfile.txt' content associated with the current test
     *
     * @return A string containing the virtual Conanfile's content
     */
    protected final String getSourceText() {
        return fSourceText;
    }

    /**
     * Get the parser associated with the current test
     *
     * @return The parser used during execution of the current test
     */
    protected final ParserType getParser() {
        return fParser;
    }

    /**
     * Create the parser for the current test class
     * <p>
     * Subclasses must implement this method to provide the testing infrastructure
     * with the required parser.
     * </p>
     *
     * @return
     * @throws Exception
     */
    protected abstract ParserType createParser() throws Exception;

    @Before
    public void setUp() throws Exception {
        fParser = createParser();
    }
}
