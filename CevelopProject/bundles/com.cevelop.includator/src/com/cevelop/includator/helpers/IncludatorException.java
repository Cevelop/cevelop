/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

public class IncludatorException extends RuntimeException {

    private static final long serialVersionUID = -7849022653495477841L;
    private final String      affectedPath;

    public IncludatorException(String message) {
        this(message, (String) null);
    }

    public IncludatorException(String message, String affectedPath) {
        super(message);
        this.affectedPath = affectedPath;
    }

    public IncludatorException(Throwable cause) {
        this(cause, null);
    }

    public IncludatorException(Throwable cause, String affectedPath) {
        super(cause);
        this.affectedPath = affectedPath;
    }

    public IncludatorException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public IncludatorException(String message, Throwable cause, String affectedPath) {
        super(message, cause);
        this.affectedPath = affectedPath;
    }

    public String getAffectedPath() {
        return affectedPath;
    }

}
