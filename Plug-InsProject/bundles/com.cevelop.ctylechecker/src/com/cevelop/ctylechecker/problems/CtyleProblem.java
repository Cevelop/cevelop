package com.cevelop.ctylechecker.problems;

import java.net.URL;
import java.util.Collections;
import java.util.Map;


public class CtyleProblem {

    private final String           problem;
    private final String           explanation;
    private final Map<String, URL> resources;

    public CtyleProblem() {
        this("", "", Collections.emptyMap());
    }

    public CtyleProblem(final String problem, final String explanation, final Map<String, URL> resources) {
        this.problem = problem;
        this.explanation = explanation;
        this.resources = resources;
    }

    public String getProblem() {
        return problem;
    }

    public String getExplanation() {
        return explanation;
    }

    public Map<String, URL> getResources() {
        return resources;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((explanation == null) ? 0 : explanation.hashCode());
        result = prime * result + ((problem == null) ? 0 : problem.hashCode());
        result = prime * result + ((resources == null) ? 0 : resources.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CtyleProblem other = (CtyleProblem) obj;
        if (explanation == null) {
            if (other.explanation != null) {
                return false;
            }
        } else if (!explanation.equals(other.explanation)) {
            return false;
        }
        if (problem == null) {
            if (other.problem != null) {
                return false;
            }
        } else if (!problem.equals(other.problem)) {
            return false;
        }
        if (resources == null) {
            if (other.resources != null) {
                return false;
            }
        } else if (!resources.equals(other.resources)) {
            return false;
        }
        return true;
    }
}
