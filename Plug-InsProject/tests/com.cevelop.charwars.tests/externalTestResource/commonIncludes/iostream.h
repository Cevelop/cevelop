#define NULL 0

namespace std{
	template <class _CharT, class _Traits>
	class basic_ostream {
	};

	template<class _Traits>
	basic_ostream<char, _Traits>& operator<<(basic_ostream<char, _Traits>& __os, const char* __str)
	{
		return __os;
	}

	template<class _CharT, class _Traits>
	basic_ostream<_CharT, _Traits>& operator<<(basic_ostream<_CharT, _Traits>& __os, const string& __str)
	{
		return __os;
	}

	template <class _CharT, class _Traits = char >
	class basic_ostream;

	typedef basic_ostream<char> ostream;
	extern ostream cout;
	ostream cout;
}