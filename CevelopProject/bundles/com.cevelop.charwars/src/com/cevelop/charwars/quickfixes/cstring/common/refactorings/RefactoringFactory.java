package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.EnumSet;

import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.ArgMapping.Arg;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;


public class RefactoringFactory {

    public static Refactoring[] createRefactorings() {
        return new Refactoring[] {
                                   //various -> see ExpressionRefactoring class
                                   new ExpressionRefactoring(EnumSet.allOf(Kind.class)),

                                   //strcpy(str, b) -> str = b
                                   new OperatorRefactoring(Function.STRCPY, Function.OP_ASSIGNMENT, EnumSet.of(Kind.Unmodified_String)),

                                   //strcat(str, b) -> str +=b
                                   new OperatorRefactoring(Function.STRCAT, Function.OP_PLUS_ASSIGNMENT, EnumSet.of(Kind.Unmodified_String)),

                                   //strcmp(str, b) == 0 -> str == b
                                   new OperatorRefactoring(Function.STRCMP, Function.OP_EQUALS, EnumSet.of(Kind.Unmodified_String)),

                                   //wcscmp(wstr, b) == 0 -> wstr == b
                                   new OperatorRefactoring(Function.WCSCMP, Function.OP_EQUALS, EnumSet.of(Kind.Unmodified_String)),

                                   //strcmp(str, b) != 0 -> str != b
                                   new OperatorRefactoring(Function.STRCMP, Function.OP_NOT_EQUALS, EnumSet.of(Kind.Unmodified_String)),

                                   //wcscmp(wstr, b) != 0 -> wstr != b
                                   new OperatorRefactoring(Function.WCSCMP, Function.OP_NOT_EQUALS, EnumSet.of(Kind.Unmodified_String)),

                                   //strchr(str,b) == NULL -> str.find(b) == std::string::npos
                                   new ComparisonRefactoring(Function.STRCHR, Function.FIND, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //wcschr(wstr,b) == NULL -> wstr.find(b) == std::wstring::npos
                                   new ComparisonRefactoring(Function.WCSCHR, Function.FIND, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //explicit offset: strchr(str+off,b) == NULL -> str.find(b, off) == std::string::npos
                                   //implicit offset: strchr(str, b) == NULL -> str.find(b, str_pos) == std::string::npos
                                   //alias: strchr(alias, b) == NULL -> str.find(b, alias) == std::string::npos
                                   new ComparisonRefactoring(Function.STRCHR, Function.FIND, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet.allOf(
                                           Kind.class)),

                                   //explicit offset: wcschr(wstr+off,b) == NULL -> wstr.find(b, off) == std::wstring::npos
                                   //implicit offset: wcschr(wstr, b) == NULL -> wstr.find(b, wstr_pos) == std::wstring::npos
                                   //alias: wcschr(alias, b) == NULL -> wstr.find(b, alias) == std::wstring::npos
                                   new ComparisonRefactoring(Function.WCSCHR, Function.FIND, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet.allOf(
                                           Kind.class)),

                                   //strpbrk(str,b) == NULL -> str.find_first_of(b) == std::string::npos
                                   new ComparisonRefactoring(Function.STRPBRK, Function.FIND_FIRST_OF, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //wcspbrk(wstr,b) == NULL -> wstr.find_first_of(b) == std::wstring::npos
                                   new ComparisonRefactoring(Function.WCSPBRK, Function.FIND_FIRST_OF, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //explicit offset: strpbrk(str+off,b) == NULL -> str.find_first_of(b, off) == std::string::npos
                                   //implicit offset: strprbk(str, b) == NULL -> str.find_first_of(b, str_pos) == std::string::npos
                                   //alias: strpbrk(alias, b) == NULL -> str.find_first_of(b, alias) == std::string::npos
                                   new ComparisonRefactoring(Function.STRPBRK, Function.FIND_FIRST_OF, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet
                                           .allOf(Kind.class)),

                                   //explicit offset: wcspbrk(wstr+off,b) == NULL -> wstr.find_first_of(b, off) == std::wstring::npos
                                   //implicit offset: wcsprbk(wstr, b) == NULL -> wstr.find_first_of(b, wstr_pos) == std::wstring::npos
                                   //alias: wcspbrk(alias, b) == NULL -> wstr.find_first_of(b, alias) == std::wstring::npos
                                   new ComparisonRefactoring(Function.WCSPBRK, Function.FIND_FIRST_OF, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet
                                           .allOf(Kind.class)),

                                   //strrchr(str,b) == NULL -> str.rfind(b) == std::string::npos
                                   new ComparisonRefactoring(Function.STRRCHR, Function.RFIND, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //strstr(str,b) == NULL -> str.find(b) == std::string::npos
                                   new ComparisonRefactoring(Function.STRSTR, Function.FIND, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //wcsstr(wstr,b) == NULL -> wstr.find(b) == std::wstring::npos
                                   new ComparisonRefactoring(Function.WCSSTR, Function.FIND, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //explicit offset: strstr(str+off,b) == NULL -> str.find(b, off) == std::string::npos
                                   //implicit offset: strstr(str, b) == NULL -> str.find(b, str_pos) == std::string::npos
                                   //alias: strstr(alias, b) == NULL -> str.find(b, alias) == std::string::npos
                                   new ComparisonRefactoring(Function.STRSTR, Function.FIND, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet.allOf(
                                           Kind.class)),

                                   //explicit offset: wcsstr(wstr+off,b) == NULL -> wstr.find(b, off) == std::wstring::npos
                                   //implicit offset: wcsstr(wstr, b) == NULL -> wstr.find(b, wstr_pos) == std::wstring::npos
                                   //alias: wcsstr(alias, b) == NULL -> wstr.find(b, alias) == std::wstring::npos
                                   new ComparisonRefactoring(Function.WCSSTR, Function.FIND, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet.allOf(
                                           Kind.class)),

                                   //strcspn(str, b) == strlen(str) -> str.find_first_of(b) == std::string::npos
                                   new ComparisonRefactoring(Function.STRCSPN, Function.FIND_FIRST_OF, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //strcspn(str+n, b) == strlen(str) -> str.find_first_of(b, n) == std::string::npos
                                   new ComparisonRefactoring(Function.STRCSPN, Function.FIND_FIRST_OF, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet
                                           .of(Kind.Unmodified_String, Kind.Modified_String)),

                                   //strspn(str, b) == strlen(str) -> str.find_first_not_of(b) == std::string::npos
                                   new ComparisonRefactoring(Function.STRSPN, Function.FIND_FIRST_NOT_OF, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //strspn(str+n, b) == strlen(str) -> str.find_first_not_of(b, n) == std::string::npos
                                   new ComparisonRefactoring(Function.STRSPN, Function.FIND_FIRST_NOT_OF, new ArgMapping(Arg.ARG_1, Arg.OFF_0),
                                           EnumSet.of(Kind.Unmodified_String, Kind.Modified_String)),

                                   //memchr(str, ch, count) == NULL -> std::find(str.begin(), str.end(), ch) == str.end()
                                   new ComparisonRefactoring(Function.MEMCHR, Function.STD_FIND, new ArgMapping(Arg.BEGIN, Arg.END, Arg.ARG_1),
                                           EnumSet.of(Kind.Unmodified_String)),

                                   //strlen(str) -> str.size()
                                   new FunctionRefactoring(Function.STRLEN, Function.SIZE, new ArgMapping(), EnumSet.of(Kind.Unmodified_String)),

                                   //strlen(str+off) -> (str.size() - off)
                                   //strlen(str) -> (str.size() - str_pos)
                                   new FunctionRefactoring(Function.STRLEN, Function.SIZE, new ArgMapping(), EnumSet.allOf(Kind.class)),

                                   //wcslen(wstr) -> wstr.size()
                                   new FunctionRefactoring(Function.WCSLEN, Function.SIZE, new ArgMapping(), EnumSet.of(Kind.Unmodified_String)),

                                   //wcslen(wstr+off) -> (wstr.size() - off)
                                   //wcslen(wstr) -> (wstr.size() - wstr_pos)
                                   new FunctionRefactoring(Function.WCSLEN, Function.SIZE, new ArgMapping(), EnumSet.allOf(Kind.class)),

                                   //explicit offset: memcmp(str+off, b, n) -> str.compare(off, n, b, 0, n)
                                   //implicit offset: memcmp(str, b, n) -> str.compare(str_pos, n, b, 0, n)
                                   //alias: memcmp(alias, b, n) -> str.compare(alias, n, b, 0, n)
                                   new FunctionRefactoring(Function.MEMCMP, Function.COMPARE, new ArgMapping(Arg.OFF_0, Arg.ARG_2, Arg.ARG_1,
                                           Arg.ZERO, Arg.ARG_2), EnumSet.allOf(Kind.class)),

                                   //explicit offset: memcpy(str+off, b, n) -> str.replace(off, n, b, 0, n)
                                   //implicit offset: memcpy(str, b, n) -> str.replace(str_pos, n, b, 0, n)
                                   new FunctionRefactoring(Function.MEMCPY, Function.REPLACE, new ArgMapping(Arg.OFF_0, Arg.ARG_2, Arg.ARG_1,
                                           Arg.ZERO, Arg.ARG_2), EnumSet.of(Kind.Unmodified_String, Kind.Modified_String)),

                                   //memmove(str+off, b, n) -> str.replace(off, n, b, 0, n)
                                   new FunctionRefactoring(Function.MEMMOVE, Function.REPLACE, new ArgMapping(Arg.OFF_0, Arg.ARG_2, Arg.ARG_1,
                                           Arg.ZERO, Arg.ARG_2), EnumSet.of(Kind.Unmodified_String, Kind.Modified_String)),

                                   //strcmp(str,b) -> str.compare(b)
                                   new FunctionRefactoring(Function.STRCMP, Function.COMPARE, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //wcscmp(wstr,b) -> wstr.compare(b)
                                   new FunctionRefactoring(Function.WCSCMP, Function.COMPARE, new ArgMapping(Arg.ARG_1), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //explicit offset: strcmp(str+off, b) -> str.compare(off, std::string::npos, b)
                                   //implicit offset: strcmp(str, b) -> str.compare(str_pos, std::string::npos, b)
                                   //alias: strcmp(alias, b) -> str.compare(alias, std::string::npos, b)
                                   new FunctionRefactoring(Function.STRCMP, Function.COMPARE, new ArgMapping(Arg.OFF_0, Arg.NPOS, Arg.ARG_1), EnumSet
                                           .allOf(Kind.class)),

                                   //explicit offset: wcscmp(wstr+off, b) -> wstr.compare(off, std::string::npos, b)
                                   //implicit offset: wcscmp(wstr, b) -> wstr.compare(wstr_pos, std::string::npos, b)
                                   //alias: wcscmp(alias, b) -> wstr.compare(alias, std::string::npos, b)
                                   new FunctionRefactoring(Function.WCSCMP, Function.COMPARE, new ArgMapping(Arg.OFF_0, Arg.NPOS, Arg.ARG_1), EnumSet
                                           .allOf(Kind.class)),

                                   //explicit offset: strncmp(str+off, b, n) -> str.compare(off, n, b, 0, n)
                                   //implicit offset: strncmp(str, b, n) -> str.compare(str_pos, n, b, 0, n)
                                   //alias: strncmp(alias, b, n) -> str.compare(alias, n, b, 0, n)
                                   new FunctionRefactoring(Function.STRNCMP, Function.COMPARE, new ArgMapping(Arg.OFF_0, Arg.ARG_2, Arg.ARG_1,
                                           Arg.ZERO, Arg.ARG_2), EnumSet.allOf(Kind.class)),

                                   //explicit offset: strcpy(str+off, b) -> str.replace(off, std::string::npos, b)
                                   new FunctionRefactoring(Function.STRCPY, Function.REPLACE, new ArgMapping(Arg.OFF_0, Arg.NPOS, Arg.ARG_1), EnumSet
                                           .of(Kind.Unmodified_String, Kind.Modified_String)),

                                   //strncat(str, b, n) -> str.append(b, 0, n)
                                   new FunctionRefactoring(Function.STRNCAT, Function.APPEND, new ArgMapping(Arg.ARG_1, Arg.ZERO, Arg.ARG_2), EnumSet
                                           .of(Kind.Unmodified_String)),

                                   //strncpy(str+off, b, n) -> str.replace(off, n, b, 0, n)
                                   new FunctionRefactoring(Function.STRNCPY, Function.REPLACE, new ArgMapping(Arg.OFF_0, Arg.ARG_2, Arg.ARG_1,
                                           Arg.ZERO, Arg.ARG_2), EnumSet.of(Kind.Unmodified_String, Kind.Modified_String)),

                                   //alias: strchr(alias, b) -> str.find(b, alias)
                                   new FunctionRefactoring(Function.STRCHR, Function.FIND, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet.of(
                                           Kind.Modified_Alias)),

                                   //alias: strstr(alias, b) -> str.find(b, alias)
                                   new FunctionRefactoring(Function.STRSTR, Function.FIND, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet.of(
                                           Kind.Modified_Alias)),

                                   //alias: strpbrk(alias, b) -> str.find(b, alias)
                                   new FunctionRefactoring(Function.STRPBRK, Function.FIND_FIRST_OF, new ArgMapping(Arg.ARG_1, Arg.OFF_0), EnumSet.of(
                                           Kind.Modified_Alias)),

                                   //atof(str) -> stod(str)
                                   new FunctionRefactoring(Function.ATOF, Function.STOD, new ArgMapping(Arg.ARG_0), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //atoi(str) -> stoi(str)
                                   new FunctionRefactoring(Function.ATOI, Function.STOI, new ArgMapping(Arg.ARG_0), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //atol(str) -> stol(str)
                                   new FunctionRefactoring(Function.ATOL, Function.STOL, new ArgMapping(Arg.ARG_0), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //atoll(str) -> stoll(str)
                                   new FunctionRefactoring(Function.ATOLL, Function.STOLL, new ArgMapping(Arg.ARG_0), EnumSet.of(
                                           Kind.Unmodified_String)),

                                   //free(str) -> <gets removed>
                                   new RemoveStatementRefactoring(Function.FREE, EnumSet.of(Kind.Unmodified_String)),

                                   //various -> see NullRefactoring class
                                   new NullRefactoring(EnumSet.allOf(Kind.class)),

                                   //various -> see DefaultRefactoring class
                                   new DefaultRefactoring(EnumSet.allOf(Kind.class)) };
    }
}
