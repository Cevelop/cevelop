package com.cevelop.conanator.tests.parser.support;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Provide the source text of a 'conanfile.txt' file to a Conanator unit test.
 *
 * @author Felix Morgner
 * @since 1.9.1
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Conanfile {

    /**
     * The content of the 'conanfile.txt' file
     */
    public String value();

}
