package com.cevelop.ctylechecker.checker.includes;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;

import com.cevelop.ctylechecker.checker.AbstractStyleChecker;


public abstract class AbstractStandardIncludeChecker extends AbstractStyleChecker {

    protected static TreeMap<String, String> nameToInclude    = new TreeMap<>();
    protected static TreeSet<String>         standardIncludes = new TreeSet<>();
    protected static TreeSet<String>         skippedIncludes  = new TreeSet<>();

    static {
        register("string", "std::string", "std::getline", "std::stoi", "std::stol", "std::stoll", "std::stoul", "std::stoull", "std::stof",
                "std::stod", "std::stold", "std::to_string", "std::to_wstring", "std::string_literals", "std::literals::string_literals",
                "std::__cxx11::string", "std::__cxx11::to_string");
        register("string_view", "std::string_view", "std::u8string_view", "std::u16string_view", "std::u32string_view", "std::wstring_view",
                "std::string_view_literals", "std::literals::string_view_literals");
        register("complex", "std::complex", "std::real", "std::imag", "std::arg", "std::norm", "std::conj", "std::proj", "std::polar",
                "std::complex_literals", "std::literals::complex_literals");
        register("vector", "std::vector");
        register("map", "std::map", "std::multimap");
        register("unordered_map", "std::unordered_map");
        register("set", "std::set", "std::multiset");
        register("unordered_set", "std::unordered_set");
        register("array", "std::array");
        register("deque", "std::deque");
        register("list", "std::list");
        register("stack", "std::stack");
        register("queue", "std::queue", "std::priority_queue");
        register("forward_list", "std::forward_list");
        register("iostream", "std::cout", "std::cin", "std::cerr");
        register("ostream", /* "std::ostream", */ "std::endl", "std::ends", "std::flush"); //needs special treatment
        register("iomanip", "std::resetiosflags", "std::setiosflags", "std::setbase", "std::setbase", "std::setfill", "std::setprecision",
                "std::setw", "std::get_money", "std::put_money", "std::get_time", "std::put_time", "std::quoted");
        register("istream", /* "std::istream", */ "std::ws"); //needs special treatment
        register("ios", "std::make_error_code", "std::make_error_condition", "std::boolalpha", "std::noboolalpha", "std::showbase", "std::noshowbase",
                "std::showpoint", "std::noshowpoint", "std::showpos", "std::noshowpos", "std::skipws", "std::noskipws", "std::uppercase",
                "std::nouppercase", "std::unitbuf", "std::nounitbuf", "std::internal", "std::left", "std::right", "std::dec", "std::hex", "std::oct",
                "std::fixed", "std::scientific", "std::hexfloat", "std::defaultfloat");
        register("iterator", "std::begin", "std::end", "std::istream_iterator", "std::ostream_iterator", "std::istreambuf_iterator",
                "std::ostreambuf_iterator", "std::next", "std::advance", "std::iterator_traits", "std::input_iterator_tag",
                "std::output_iterator_tag", "std::forward_iterator_tag", "std::bidirectional_iterator_tag", "std::random_access_iterator_tag",
                "std::iterator", "std::reverse_iterator", "std::move_iterator", "std::back_insert_iterator", "std::front_insert_iterator",
                "std::insert_iterator", "std::make_reverse_iterator", "std::make_move_iterator", "std::front_inserter", "std::back_inserter",
                "std::inserter", "std::distance", "std::prev", "std::cbegin", "std::cend", "std::rbegin", "std::rend", "std::crbegin", "std::crend");
        register("sstream", "std::stringstream", "std::istringstream", "std::ostringstream", "std::stringbuf", "std::wstringstream",
                "std::wistringstream", "std::wostringstream", "std::wstringbuf");
        register("algorithm", "std::for_each", "std::copy", "std::copy_if", "std::transform", "std::count", "std::count_if", "std::all_of",
                "std::any_of", "std::none_of", "std::mismatch", "std::equal", "std::find", "std::find_if", "std::find_if_not", "std::find_end",
                "std::find_first_of", "std::adjacent_find", "std::search", "std::search_n", "std::copy_n", "std::copy_backward", "std::move_backward",
                "std::fill", "std::fill_n", "std::generate", "std::generate_n", "std::remove", "std::remove_if", "std::remove_copy",
                "std::remove_copy_if", "std::replace", "std::replace_if", "std::replace_copy", "std::replace_copy_if", "std::swap_ranges",
                "std::iter_swap", "std::reverse", "std::reverse_copy", "std::rotate", "std::rotate_copy", "std::random_shuffle", "std::shuffle",
                "std::unique", "std::unique_copy", "std::is_partitioned", "std::partition", "std::partition_copy", "std::stable_partition",
                "std::partition_point", "std::is_sorted", "std::is_sorted_until", "std::sort", "std::partial_sort", "std::partial_sort_copy",
                "std::stable_sort", "std::nth_element", "std::lower_bound", "std::upper_bound", "std::binary_search", "std::equal_range",
                "std::merge", "std::inplace_merge", "std::includes", "std::set_difference", "std::set_intersection", "std::set_symmetric_difference",
                "std::set_union", "std::is_heap", "std::is_heap_until", "std::make_heap", "std::push_heap", "std::pop_heap", "std::sort_heap",
                "std::clamp", "std::max", "std::max_element", "std::min", "std::min_element", "std::minmax", "std::minmax_element",
                "std::lexicographical_compare", "std::is_permutation", "std::next_permutation", "std::prev_permutation");
        register("numeric", "std::iota", "std::accumulate", "std::inner_product", "std::adjacent_difference", "std::partial_sum", "std::gcd",
                "std::lcm", "std::reduce", "std::transform_reduce", "std::inclusive_scan", "std::exclusive_scan", "std::transform_inclusive_scan",
                "std::transform_exclusive_scan");
        register("utility", "std::pair", "std::integer_sequence", "std::index_sequence", "std::make_integer_sequence", "std::make_index_sequence",
                "std::index_sequence_for", "std::exchange", "std::forward", "std::move", "std::move_if_noexcept", "std::declval", "std::make_pair",
                "std::as_const");
        register("tuple", "std::tuple", "std::make_tuple", "std::tie", "std::get");
        register("stdexcept", "std::logic_error", "std::invalid_argument", "std::domain_error", "std::length_error", "std::out_of_range",
                "std::runtime_error", "std::range_error", "std::overflow_error", "std::underflow_error");
        register("exception", "std::exception");
        register("future", "std::promise", "std::packaged_task", "std::future", "std::shared_future", "std::launch", "std::future_status",
                "std::future_error", "std::future_errc", "std::async", "std::future_category");
        register("optional", "std::optional", "std::bad_optional_access", "std::nullopt_t", "std::make_optional");
        register("functional", "std::placeholders::_1", "std::placeholders::_2", "std::placeholders::_3", "std::placeholders::_4",
                "std::placeholders::_5", "std::placeholders::_6", "std::placeholders::_7", "std::placeholders::_8", "std::placeholders::_9",
                "std::function", "std::mem_fn", "std::bad_function_call", "std::is_bind_expression", "std::is_placeholder", "std::reference_wrapper",
                "std::hash", "std::bind", "std::ref", "std::cref", "std::invoke", "std::plus", "std::minus", "std::multiplies", "std::divides",
                "std::modulus", "std::negate", "std::equal_to", "std::not_equal_to", "std::greater", "std::less", "std::greater_equal",
                "std::less_equal", "std::logical_and", "std::logical_or", "std::logical_not", "std::bit_and", "std::bit_or", "std::bit_xor",
                "std::bit_not", "std::not_fn", "std::unary_negate", "std::binary_negate", "std::not1", "std::not2");
        register("regex", "std::syntax_option_type", "std::match_flag_type", "std::error_type", "std::basic_regex", "std::sub_match",
                "std::match_results", "std::regex_iterator", "std::regex_token_iterator", "std::regex_error", "std::regex_traits", "std::regex_match",
                "std::regex_search", "std::regex_replace");
        register("system_error", "std::system_error", "std::error_category", "std::generic_category", "std::system_category", "std::error_condition",
                "std::errc", "std::error_code", "std::is_error_code_enum", "std::is_error_condition_enum", "std::make_error_condition");
        register("typeinfo", "std::type_info", "std::bad_typeid", "std::bad_cast");
        register("memory", "std::unique_ptr", "std::shared_ptr", "std::weak_ptr", "std::auto_ptr", "std::owner_less", "std::enable_shared_from_this",
                "std::bad_weak_ptr", "std::default_delete", "std::allocator", "std::allocator_traits", "std::allocator_arg_t", "std::allocator_arg",
                "std::uses_allocator", "std::pointer_safety", "std::pointer_traits", "std::allocator_arg", "std::uninitialized_copy",
                "std::uninitialized_copy_n", "std::uninitialized_fill", "std::uninitialized_fill_n", "std::raw_storage_iterator",
                "std::get_temporary_buffer", "std::return_temporary_buffer", "std::declare_reachable", "std::undeclare_reachable",
                "std::declare_no_pointers", "std::undeclare_no_pointers", "std::get_pointer_safety", "std::addressof", "std::align",
                "std::make_unique", "std::make_shared", "std::allocate_shared", "std::static_pointer_cast", "std::dynamic_pointer_cast",
                "std::const_pointer_cast", "std::reinterpret_pointer_cast", "std::get_deleter", "std::destroy", "std::destroy_at", "std::destory_n",
                "std::uninitialized_move", "std::uninitialized_move_n", "std::uninitialized_default_construct",
                "std::uninitialized_default_construct_n", "std::uninitialized_value_construct", "std::uninitialized_value_construct_n");
        register("atomic", "std::atomic", "std::atomic_flag", "std::memory_order", "std::atomic_bool", "std::atomic_char", "std::atomic_schar",
                "std::atomic_uchar", "std::atomic_short", "std::atomic_ushort", "std::atomic_int", "std::atomic_uint", "std::atomic_long",
                "std::atomic_ulong", "std::atomic_llong", "std::atomic_ullong", "std::atomic_char16_t", "std::atomic_char32_t", "std::atomic_wchar_t",
                "std::atomic_int_least8_t", "std::atomic_uint_least8_t", "std::atomic_int_least16_t", "std::atomic_uint_least16_t",
                "std::atomic_int_least32_t", "std::atomic_uint_least32_t", "std::atomic_int_least64_t", "std::atomic_uint_least64_t",
                "std::atomic_int_fast8_t", "std::atomic_uint_fast8_t", "std::atomic_int_fast16_t", "std::atomic_uint_fast16_t",
                "std::atomic_int_fast32_t", "std::atomic_uint_fast32_t", "std::atomic_int_fast64_t", "std::atomic_uint_fast64_t",
                "std::atomic_intptr_t", "std::atomic_uintptr_t", "std::atomic_size_t", "std::atomic_ptrdiff_t", "std::atomic_intmax_t",
                "std::atomic_uintmax_t", "std::atomic_is_lock_free", "std::atomic_store", "std::atomic_store_explicit", "std::atomic_load",
                "std::atomic_load_explicit", "std::atomic_exchance", "std::atomic_exchange_explicit", "std::atomic_compare_exchange_weak",
                "std::atomic_compare_exchange_weak_explicit", "std::atomic_compare_exchange_strong", "std::atomic_compare_exchange_strong_explicit",
                "std::atomic_fetch_add", "std::atomic_fetch_add_explicit", "std::atomic_fetch_sub", "std::atomic_fetch_sub_explicit",
                "std::atomic_fetch_and", "std::atomic_fetch_and_explicit", "std::atomic_fetch_or", "std::atomic_fetch_or_explicit",
                "std::atomic_fetch_xor", "std::atomic_fetch_xor_explicit", "std::atomic_flag_test_and_set", "std::atomic_flag_test_and_set_explicit",
                "std::atomic_flag_clear", "std::atomic_flag_clear_explicit", "std::atomic_init", "std::kill_dependency", "std::atomic_thread_fence",
                "std::atomic_signal_fence");
        register("new", "std::get_new_handler", "std::set_new_handler", "std::bad_alloc", "std::bad_array_new_length", "std::nothrow_t",
                "std::new_handler", "std::nothrow");
        register("thread", "std::thread", "std::this_thread", "std::this_thread::yield", "std::this_thread::get_id", "std::this_thread::sleep_for",
                "std::this_thread::sleep_until");
        register("chrono", "std::chrono::duration", "std::chrono::system_clock", "std::chrono::steady_clock", "std::chrono::high_resolution_clock",
                "std::chrono::time_point", "std::chrono::treat_as_floating_point", "std::chrono::duration_values", "std::chrono::nanoseconds",
                "std::chrono::microseconds", "std::chrono::milliseconds", "std::chrono::seconds", "std::chrono::minutes", "std::chrono::hours",
                "std::chrono::time_point_cast", "std::chrono_literals", "std::literals::chrono_literals");
        register("limits", "std::numeric_limits", "std::float_round_style", "std::float_denorm_style");
        register("initializer_list", "std::initializer_list");

        register("cmath", "std::abs", "std::labs", "std::llabs", "std::fabs", "std::fmod", "std::remainder", "std::remquo", "std::fma", "std::fma",
                "std::fmax", "std::fmin", "std::dim", "std::nan", "std::nanf", "std::nanl", "std::exp", "std::exp2", "std::expm1", "std::log",
                "std::log10", "std::log2", "std::log1p", "std::pow", "std::sqrt", "std::cbrt", "std::hypot", "std::sin", "std::cos", "std::tan",
                "std::asin", "std::acos", "std::atan", "std::atan2", "std::sinh", "std::cosh", "std::tanh", "std::asinh", "std::acosh", "std::atanh",
                "std::erf", "std::erfc", "std::tgamma", "std::lgamma", "std::ceil", "std::floor", "std::trunc", "std::round", "std::lround",
                "std::llround", "std::nearbyint", "std::rint", "std::lrint", "std::llrint", "std::frexp", "std::ldexp", "std::modf", "std::scalbn",
                "std::scalbln", "std::ilogb", "std::logb", "std::nextafter", "std::nexttoward", "std::copysign", "std::fpclassify", "std::isfinite",
                "std::isinf", "std::isnormal", "std::signbit", "std::isgreater", "std::isgreaterequal", "std::isless", "std::islessequal",
                "std::islessgreater", "std::isunordered");
        register("cctype", "std::isalnum", "std::isalpha", "std::islower", "std::isupper", "std::isdigit", "std::isxdigit", "std::iscntrl",
                "std::isgraph", "std::isspace", "std::isblank", "std::isprint", "std::ispunct", "std::tolower", "std::toupper", "isalnum", "isalpha",
                "islower", "isupper", "isdigit", "isxdigit", "iscntrl", "isgraph", "isspace", "isblank", "isprint", "ispunct", "tolower", "toupper");
        register("cstdlib", "std::div_t", "std::ldiv_t", "std::lldiv_t", "std::abort", "std::exit", "std::quick_exit", "std::_Exit", "std::atexit",
                "std::at_quick_exit", "std::system", "std::getenv", "std::malloc", "std::calloc", "std::realloc", "std::free", "std::atof",
                "std::atoi", "std::atol", "std::atoll", "std::strtol", "std::strtoll", "std::strtoul", "std::strtoull", "std::strtof", "std::strtod",
                "std::strtold", "std::mblen", "std::mbtowc", "std::wctomb", "std::mbstowcs", "std::wcstombs", "std::rand", "std::srand", "std::div",
                "std::ldiv", "std::lldiv");
        register("cstddef", "std::size_t", "std::ptrdiff_t", "std::max_align_t", "std::nullptr_t", "std::byte");

        standardIncludes.addAll(nameToInclude.values());

        skippedIncludes.add("istream");
        skippedIncludes.add("ostream");
        skippedIncludes.add("iosfwd");
    }

    private static void register(String include, String... names) {
        for (String name : names) {
            nameToInclude.put(name, include);
        }
    }

    protected static boolean needsStandardInclude(IASTName name) {
        String qualifiedName = toQualifiedName(name);
        return nameToInclude.containsKey(qualifiedName);
    }

    protected static boolean notInMacroExpansion(IASTName name) {
        IASTNodeLocation[] nodeLocations = name.getNodeLocations();
        return nodeLocations.length == 1 && !(nodeLocations[0] instanceof IASTMacroExpansionLocation);
    }

    protected static boolean isStandardInclude(IASTPreprocessorIncludeStatement include) {
        return standardIncludes.contains(include.getName().toString());
    }

    protected static String extractIncludeName(IASTPreprocessorIncludeStatement includeDirective) {
        return includeDirective.getName().toString();
    }

    protected static boolean isRequired(IASTPreprocessorIncludeStatement include, List<IASTName> allNames) {
        String includeName = extractIncludeName(include);
        if (shouldSkip(includeName)) {
            return true;
        }

        for (IASTName name : allNames) {
            if (nameRequiresInclude(name.toString(), includeName) || nameRequiresInclude(toQualifiedName(name), includeName)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean notPartOfTemplateId(IASTNode node) {
        return node.getPropertyInParent() != ICPPASTTemplateId.TEMPLATE_NAME;
    }

    protected static boolean notPartOfQualifiedName(IASTNode node) {
        return node.getPropertyInParent() != ICPPASTQualifiedName.SEGMENT_NAME;
    }

    private static boolean nameRequiresInclude(String name, String includeName) {
        String requiredInclude = nameToInclude.get(name);
        return includeName.equals(requiredInclude);
    }

    private static boolean shouldSkip(String includeName) {
        return skippedIncludes.contains(includeName);
    }
}
