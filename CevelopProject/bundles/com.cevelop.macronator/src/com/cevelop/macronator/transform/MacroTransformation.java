package com.cevelop.macronator.transform;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ProblemRuntimeException;

import com.cevelop.macronator.common.Parser;


@SuppressWarnings("restriction")
public class MacroTransformation {

    private final boolean     isValid;
    private final String      transformedCode;
    private final Transformer transformer;

    public MacroTransformation(final Transformer transformer) {
        this.transformer = transformer;
        final TransformationResult result = transform();
        isValid = result.isValid && !containsUnsupportedBuiltinMacros(result.code) && transformer.isValid();
        transformedCode = result.code;
    }

    private TransformationResult transform() {
        try {
            if (transformer.isValid()) {
                final String transformedCode = transformer.generateTransformationCode();
                final Parser parser = new Parser(transformedCode);
                final IASTTranslationUnit translationUnit = parser.parse();
                if (!parser.encounteredErrors()) {
                    return TransformationResult.valid(new ASTWriter().write(translationUnit));
                }
            }
            return TransformationResult.invalid();
        } catch (final ProblemRuntimeException e) {
            return TransformationResult.invalid();
        }
    }

    static class TransformationResult {

        final String  code;
        final boolean isValid;

        public TransformationResult(final String code, final boolean isValid) {
            this.code = code;
            this.isValid = isValid;
        }

        static TransformationResult invalid() {
            return new TransformationResult("", false);
        }

        static TransformationResult valid(final String code) {
            return new TransformationResult(code, true);
        }
    }

    /**
     * Returns the transformation for the supplied macro, as a correctly
     * formatted String. If no valid transformation exists (isValid() == false),
     * the empty String is returned. valid the macro transformation or the empty
     * String if no valid transformation exists.
     *
     * @return the transformation or the empty string
     */
    public String getCode() {
        return transformedCode;
    }

    /**
     * Returns true if a valid transformation for the supplied macro exists.
     *
     * @return true if a valid transformation exists
     */
    public boolean isValid() {
        return isValid;
    }

    private boolean containsUnsupportedBuiltinMacros(final String code) {
        return (code.contains("__LINE__") || code.contains("__FILE__"));
    }
}
