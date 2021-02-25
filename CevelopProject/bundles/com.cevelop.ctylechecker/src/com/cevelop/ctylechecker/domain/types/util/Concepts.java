package com.cevelop.ctylechecker.domain.types.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPAliasTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumeration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPEnumerator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPField;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFieldTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunctionTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethodTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespace;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceAlias;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateTypeParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariableTemplate;

import com.cevelop.ctylechecker.common.ConceptNames;
import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.types.Concept;


@SuppressWarnings("restriction")
public final class Concepts {

    //Can't access ObjectStyleMacro.class, is in unvisible class
    public static final String CPP_MACRO = "ObjectStyleMacro";

    public static final String CPP_NAMESPACE       = CPPNamespace.class.getSimpleName();
    public static final String CPP_NAMESPACE_ALIAS = CPPNamespaceAlias.class.getSimpleName();

    public static final String CPP_VARIABLE          = CPPVariable.class.getSimpleName();
    public static final String CPP_TEMPLATE_VARIABLE = CPPVariableTemplate.class.getSimpleName();

    public static final String CPP_PARAMETER = CPPParameter.class.getSimpleName();

    public static final String CPP_TEMPLATE_PARAMETER = CPPTemplateTypeParameter.class.getSimpleName();

    public static final String CPP_FIELD          = CPPField.class.getSimpleName();
    public static final String CPP_TEMPLATE_FIELD = CPPFieldTemplate.class.getSimpleName();

    public static final String CPP_TYPEDEF = CPPTypedef.class.getSimpleName();

    public static final String CPP_ENUMARATION = CPPEnumeration.class.getSimpleName();
    public static final String CPP_ENUMARATOR  = CPPEnumerator.class.getSimpleName();

    public static final String CPP_FUNCTION          = CPPFunction.class.getSimpleName();
    public static final String CPP_FUNCTION_TEMPLATE = CPPFunctionTemplate.class.getSimpleName();

    public static final String CPP_METHOD          = CPPMethod.class.getSimpleName();
    public static final String CPP_METHOD_TEMPLATE = CPPMethodTemplate.class.getSimpleName();

    public static final String CPP_ALIAS_TEMPLATE = CPPAliasTemplate.class.getSimpleName();

    public static final String CPP_CLASS_TYPE     = CPPClassType.class.getSimpleName();
    public static final String CPP_CLASS_TEMPLATE = CPPClassTemplate.class.getSimpleName();

    private static final Map<String, String> nameMap = new HashMap<String, String>() {

        private static final long serialVersionUID = -1373300097622087230L;
        {
            put(ConceptNames.NAME_SOURCE_FILE, ConceptNames.NAME_SOURCE_FILE);
            put(ConceptNames.NAME_HEADER_FILE, ConceptNames.NAME_HEADER_FILE);
            put(CPP_MACRO, ConceptNames.NAME_MACRO);

            put(CPP_NAMESPACE, ConceptNames.NAME_NAMESPACES);
            put(CPP_NAMESPACE_ALIAS, ConceptNames.NAME_NAMESPACE_ALIASES);

            put(CPP_VARIABLE, ConceptNames.NAME_VARIABLES);
            put(CPP_TEMPLATE_VARIABLE, ConceptNames.NAME_TEMPLATE_VARIABLES);

            put(CPP_PARAMETER, ConceptNames.NAME_PARAMETERS);
            put(CPP_TEMPLATE_PARAMETER, ConceptNames.NAMES_TEMPLATE_PARAMETERS);

            put(CPP_TYPEDEF, ConceptNames.NAME_TYPEDEFINITIONS);

            put(CPP_FIELD, ConceptNames.NAME_FIELDS);
            put(CPP_TEMPLATE_FIELD, ConceptNames.NAME_TEMPLATE_FIELDS);

            put(CPP_ENUMARATION, ConceptNames.NAME_ENUMARATIONS);
            put(CPP_ENUMARATOR, ConceptNames.NAME_ENUMARATORS);

            put(CPP_FUNCTION, ConceptNames.NAME_FUNCTIONS);
            put(CPP_FUNCTION_TEMPLATE, ConceptNames.NAME_FUNCTION_TEMPLATES);

            put(CPP_METHOD, ConceptNames.NAME_METHODS);
            put(CPP_METHOD_TEMPLATE, ConceptNames.NAME_METHOD_TEMPLATES);

            put(CPP_ALIAS_TEMPLATE, ConceptNames.NAME_TEMPLATE_ALIASES);

            put(CPP_CLASS_TYPE, ConceptNames.NAME_CLASS_TYPES);
            put(CPP_CLASS_TEMPLATE, ConceptNames.NAME_TEMPLATE_CLASSES);
        }
    };

    private static final Map<String, String> reverseNameMap = new HashMap<String, String>() {

        private static final long serialVersionUID = -7448370567497095784L;
        {
            for (Entry<String, String> entry : nameMap.entrySet()) {
                put(entry.getValue(), entry.getKey());
            }
        }
    };

    private static final Map<String, IConcept> conceptsMap = new HashMap<String, IConcept>() {

        private static final long serialVersionUID = -7448370567497095784L;
        {
            for (Entry<String, String> entry : nameMap.entrySet()) {
                put(entry.getKey(), new Concept(entry.getKey()));
            }
        }
    };

    static {
        initQualifiers();
    }

    private static void initQualifiers() {
        conceptsMap.get(CPP_VARIABLE).setQualifiers(Qualifiers.variableQualifiers());
        conceptsMap.get(CPP_TEMPLATE_VARIABLE).setQualifiers(Qualifiers.variableQualifiers());

        conceptsMap.get(CPP_FIELD).setQualifiers(Qualifiers.fieldQualifiers());
        conceptsMap.get(CPP_TEMPLATE_FIELD).setQualifiers(Qualifiers.fieldQualifiers());

        conceptsMap.get(CPP_FUNCTION).setQualifiers(Qualifiers.functionQualifiers());
        conceptsMap.get(CPP_FUNCTION_TEMPLATE).setQualifiers(Qualifiers.functionQualifiers());

        conceptsMap.get(CPP_METHOD).setQualifiers(Qualifiers.methodQualifiers());
        conceptsMap.get(CPP_METHOD_TEMPLATE).setQualifiers(Qualifiers.methodQualifiers());

        conceptsMap.get(ConceptNames.NAME_SOURCE_FILE).setQualifiers(Qualifiers.fileQualifiers());
        conceptsMap.get(ConceptNames.NAME_HEADER_FILE).setQualifiers(Qualifiers.fileQualifiers());
    }

    public static List<String> all() {
        return ListUtil.asSortedList(nameMap.keySet());
    }

    public static Optional<IConcept> getConcept(String pName) {
        IConcept binding = conceptsMap.get(pName);
        if (binding == null) {
            binding = conceptsMap.get(originalName(pName));
        }
        return Optional.ofNullable(binding);
    }

    public static String simpleName(String pOriginalName) {
        String simpleName = nameMap.get(pOriginalName);
        return simpleName != null ? simpleName : pOriginalName;
    }

    public static String originalName(String pSimpleName) {
        String originalName = reverseNameMap.get(pSimpleName);
        return originalName != null ? originalName : pSimpleName;
    }
}
