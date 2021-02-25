package com.cevelop.branding.startup;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.osgi.service.prefs.Preferences;


public class StartupHook implements IStartup {

    @Override
    public void earlyStartup() {
        installCevelopFormatter();
        introScreen();
    }

    private void introScreen() {

        String showIntro = System.getenv("CEVELOP_INTRO");
        if (showIntro == null || showIntro.equals("true")) {
            // Do nothing, the intro screen is shown
        } else {

            final IWorkbench workbench = PlatformUI.getWorkbench();
            workbench.getDisplay().asyncExec(() -> {
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window != null) {
                    IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
                    IIntroPart intro = introManager.getIntro();
                    introManager.closeIntro(intro);
                }
            });
        }
    }

    private void installCevelopFormatter() {
        PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
            IEclipsePreferences rootCS = Platform.getPreferencesService().getRootNode();
            Preferences cdtUiNode = rootCS.node(InstanceScope.SCOPE).node(CUIPlugin.PLUGIN_ID);

            boolean formatterIsAbsent = cdtUiNode.get("org.eclipse.cdt.ui.formatterprofiles", null) == null;
            if (formatterIsAbsent) {
                addCevelopFormatterProfile(cdtUiNode);
                setCevelopFormatterAsActive(cdtUiNode);
                removeFileTemplateComments(cdtUiNode);
                setIndividualCevelopFormatterPreferences(rootCS);
            }
        });
    }

    /**
     * Individually set each preference of the Cevelop formatter. This is needed
     * to actually apply the settings of the formatter.
     */
    private void setIndividualCevelopFormatterPreferences(IEclipsePreferences rootCS) {
        Preferences cdtCoreNode = rootCS.node(InstanceScope.SCOPE).node(CCorePlugin.PLUGIN_ID);
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_for", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_method_declaration", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_in_empty_block", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.lineSplit", "150");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_member_access", "0");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_base_types", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.keep_else_statement_on_same_line", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_constructor_initializer_list", "0");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_switchstatements_compare_to_switch", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_brace_in_array_initializer", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_declaration_parameters", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_if", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_exception_specification", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_parenthesized_expression", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_base_types", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_body_declarations_compare_to_access_specifier", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_exception_specification", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_template_arguments", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_block", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_method_declaration", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_labeled_statement", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.use_tabs_only_for_leading_indentations", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_case", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_enum_declarations", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_array_initializer", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.comment.min_distance_between_code_and_line_comment", "1");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_expressions_in_array_initializer", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_declarator_list", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_for", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_bracket", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_prefix_operator", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.tabulation.size", "4");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_before_else_in_if_statement", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_parenthesized_expression", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_enumerator_list", "48");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_method_declaration", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_declarator_list", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_switch", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_empty_lines", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_parenthesized_expression", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_switchstatements_compare_to_cases", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_method_declaration", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.keep_empty_array_initializer_on_one_line", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_switch", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.put_empty_statement_on_new_line", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_cast", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.brace_position_for_method_declaration", "end_of_line");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_braces_in_array_initializer", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_while", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_question_in_conditional", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_semicolon", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_angle_bracket_in_template_arguments", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_base_clause", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_breaks_compare_to_cases", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_unary_operator", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_declarator_list", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.join_wrapped_lines", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_arguments_in_method_invocation", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.comment.never_indent_line_comments_on_first_column", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_while", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_bracket", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_brackets", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_parameters_in_method_declaration", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_before_closing_brace_in_array_initializer", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.number_of_empty_lines_to_preserve", "1");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_method_invocation", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_brace_in_array_initializer", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.brace_position_for_block", "end_of_line");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.comment.preserve_white_space_between_code_and_line_comments", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_conditional", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_semicolon_in_for", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_assignment_operator", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.brace_position_for_type_declaration", "end_of_line");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_expression_list", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_angle_bracket_in_template_arguments", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_angle_bracket_in_template_parameters", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.continuation_indentation", "2");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_method_declaration", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_expression_list", "0");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_template_parameters", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_binary_operator", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_default", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_conditional_expression", "34");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_method_invocation", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_array_initializer", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_if", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.format_guardian_clause_on_one_line", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_cast", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_access_specifier_extra_spaces", "0");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_access_specifier_compare_to_type_header", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_type_declaration", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_declaration_parameters", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_labeled_statement", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.continuation_indentation_for_array_initializer", "2");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_semicolon_in_for", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_method_invocation", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_body_declarations_compare_to_namespace_header", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_brace_in_block", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_assignment_operator", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_compact_if", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_array_initializer", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_assignment", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_at_end_of_file_if_missing", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_template_parameters", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_conditional_expression_chain", "18");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_expression_list", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_exception_specification", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_question_in_conditional", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_binary_operator", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_before_identifier_in_function_declaration", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_base_clause_in_type_declaration", "80");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_declaration_throws", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_declaration_compare_to_template_header", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_invocation_arguments", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_exception_specification", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_unary_operator", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_switch", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_declaration_throws", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_statements_compare_to_body", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_binary_expression", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indent_statements_compare_to_block", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_throws_clause_in_method_declaration", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_before_catch_in_try_statement", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_template_arguments", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_method_invocation", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_paren_in_cast", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_catch", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_angle_bracket_in_template_parameters", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.tabulation.char", "tab");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_angle_bracket_in_template_parameters", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_before_colon_in_constructor_initializer_list", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_while", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_invocation_arguments", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.brace_position_for_block_in_case", "end_of_line");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_postfix_operator", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.compact_else_if", "true");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_base_clause", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_after_template_declaration", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_catch", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.keep_then_statement_on_same_line", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.brace_position_for_switch", "end_of_line");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.alignment_for_overloaded_left_shift_chain", "16");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_switch", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_if", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.indentation.size", "4");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_after_opening_brace_in_array_initializer", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.keep_imple_if_on_one_line", "false");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.brace_position_for_namespace_declaration", "end_of_line");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_conditional", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_enum_declarations", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_angle_bracket_in_template_arguments", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_prefix_operator", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.brace_position_for_array_initializer", "end_of_line");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_case", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_catch", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_namespace_declaration", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_postfix_operator", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_bracket", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_new_line_before_while_in_do_statement", "do not insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_for", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_angle_bracket_in_template_parameters", "insert");
        cdtCoreNode.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_angle_bracket_in_template_arguments", "do not insert");
    }

    private void setCevelopFormatterAsActive(Preferences cdtUiNode) {
        cdtUiNode.put("formatter_profile", "_" + "Cevelop (K&R based) [built-in]");
    }

    private void addCevelopFormatterProfile(Preferences cdtUiNode) {
        String cs_c_xml =
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><profiles version=\"1\"><profile kind=\"CodeFormatterProfile\" name=\"Cevelop (K&amp;R based) [built-in]\" version=\"1\"><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_for\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_method_declaration\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_in_empty_block\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.lineSplit\" value=\"150\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_member_access\" value=\"0\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_base_types\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.keep_else_statement_on_same_line\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_constructor_initializer_list\" value=\"0\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_switchstatements_compare_to_switch\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_brace_in_array_initializer\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_declaration_parameters\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_if\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_exception_specification\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_parenthesized_expression\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_base_types\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_body_declarations_compare_to_access_specifier\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_exception_specification\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_template_arguments\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_block\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_method_declaration\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_colon_in_labeled_statement\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.use_tabs_only_for_leading_indentations\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_colon_in_case\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_enum_declarations\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_array_initializer\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.comment.min_distance_between_code_and_line_comment\" value=\"1\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_expressions_in_array_initializer\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_declarator_list\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_for\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_bracket\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_prefix_operator\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.tabulation.size\" value=\"4\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_before_else_in_if_statement\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_parenthesized_expression\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_enumerator_list\" value=\"48\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_method_declaration\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_declarator_list\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_switch\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_empty_lines\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_parenthesized_expression\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_switchstatements_compare_to_cases\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_method_declaration\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.keep_empty_array_initializer_on_one_line\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_switch\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.put_empty_statement_on_new_line\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_cast\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.brace_position_for_method_declaration\" value=\"end_of_line\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_between_empty_braces_in_array_initializer\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_while\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_question_in_conditional\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_semicolon\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_closing_angle_bracket_in_template_arguments\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_colon_in_base_clause\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_breaks_compare_to_cases\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_unary_operator\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_declarator_list\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.join_wrapped_lines\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_arguments_in_method_invocation\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.comment.never_indent_line_comments_on_first_column\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_while\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_bracket\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_between_empty_brackets\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_parameters_in_method_declaration\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_before_closing_brace_in_array_initializer\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.number_of_empty_lines_to_preserve\" value=\"1\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_method_invocation\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_brace_in_array_initializer\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.brace_position_for_block\" value=\"end_of_line\"/><setting id=\"org.eclipse.cdt.core.formatter.comment.preserve_white_space_between_code_and_line_comments\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_colon_in_conditional\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_semicolon_in_for\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_assignment_operator\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.brace_position_for_type_declaration\" value=\"end_of_line\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_expression_list\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_angle_bracket_in_template_arguments\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_angle_bracket_in_template_parameters\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.continuation_indentation\" value=\"2\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_method_declaration\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_expression_list\" value=\"0\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_template_parameters\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_binary_operator\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_colon_in_default\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_conditional_expression\" value=\"34\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_method_invocation\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_array_initializer\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_if\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.format_guardian_clause_on_one_line\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_cast\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_access_specifier_extra_spaces\" value=\"0\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_access_specifier_compare_to_type_header\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_type_declaration\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_declaration_parameters\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_colon_in_labeled_statement\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.continuation_indentation_for_array_initializer\" value=\"2\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_semicolon_in_for\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_method_invocation\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_body_declarations_compare_to_namespace_header\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_closing_brace_in_block\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_assignment_operator\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_compact_if\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_array_initializer\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_assignment\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_at_end_of_file_if_missing\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_template_parameters\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_conditional_expression_chain\" value=\"18\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_expression_list\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_exception_specification\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_question_in_conditional\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_binary_operator\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_before_identifier_in_function_declaration\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_base_clause_in_type_declaration\" value=\"80\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_declaration_throws\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_declaration_compare_to_template_header\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_invocation_arguments\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_exception_specification\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_unary_operator\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_switch\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_declaration_throws\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_statements_compare_to_body\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_binary_expression\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.indent_statements_compare_to_block\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_throws_clause_in_method_declaration\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_before_catch_in_try_statement\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_template_arguments\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_method_invocation\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_closing_paren_in_cast\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_catch\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_angle_bracket_in_template_parameters\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.tabulation.char\" value=\"tab\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_angle_bracket_in_template_parameters\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_before_colon_in_constructor_initializer_list\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_while\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_invocation_arguments\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.brace_position_for_block_in_case\" value=\"end_of_line\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_postfix_operator\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.compact_else_if\" value=\"true\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_colon_in_base_clause\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_after_template_declaration\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_catch\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.keep_then_statement_on_same_line\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.brace_position_for_switch\" value=\"end_of_line\"/><setting id=\"org.eclipse.cdt.core.formatter.alignment_for_overloaded_left_shift_chain\" value=\"16\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_switch\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_if\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.indentation.size\" value=\"4\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_after_opening_brace_in_array_initializer\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.keep_imple_if_on_one_line\" value=\"false\"/><setting id=\"org.eclipse.cdt.core.formatter.brace_position_for_namespace_declaration\" value=\"end_of_line\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_colon_in_conditional\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_comma_in_enum_declarations\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_angle_bracket_in_template_arguments\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_prefix_operator\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.brace_position_for_array_initializer\" value=\"end_of_line\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_colon_in_case\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_catch\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_namespace_declaration\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_postfix_operator\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_closing_bracket\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_new_line_before_while_in_do_statement\" value=\"do not insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_for\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_closing_angle_bracket_in_template_parameters\" value=\"insert\"/><setting id=\"org.eclipse.cdt.core.formatter.insert_space_after_opening_angle_bracket_in_template_arguments\" value=\"do not insert\"/></profile></profiles>";
        cdtUiNode.put("org.eclipse.cdt.ui.formatterprofiles", cs_c_xml);
    }

    private void removeFileTemplateComments(Preferences cdtUiNode) {
        String file_comment_template_xml =
                                         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><templates><template autoinsert=\"false\" context=\"org.eclipse.cdt.ui.text.codetemplates.filecomment_context\" deleted=\"false\" description=\"Comment for created C/C++ files\" enabled=\"true\" id=\"org.eclipse.cdt.ui.text.codetemplates.filecomment\" name=\"filecomment\"/></templates>";
        cdtUiNode.put("org.eclipse.cdt.ui.text.custom_code_templates", file_comment_template_xml);
    }
}
