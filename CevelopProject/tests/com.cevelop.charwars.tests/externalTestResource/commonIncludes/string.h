#define NULL 0

namespace std{
	class string {
	public:
		string() {}
		string(const char *str) {}
		string(const string &str) {}
		string(const char *str, size_type count) {}
		const char *c_str() const { return NULL; }
		string& operator=(const char *str) { return *this };
		string& operator=(const string& str) { return *this; };
		string& operator+=(const char *str) { return *this; }
		string& operator+=(const string& str) { return *this; }
	};

	template<class _CharT>
	string operator+(const string& __x, const _CharT* __y) {
		return __x;
	}

	template<class _CharT>
	string operator+(const _CharT* __x, const string& __y) {
		return __y;
	}

	string operator+(const string& __x, const string& __y) {
		return __x;
	}
	
	bool operator==(const string &lhs, const string &rhs) { return false; }
	bool operator==(const string &lhs, const char *rhs) { return false; }
	bool operator==(const char *lhs, const string &rhs) { return false; }
	bool operator!=(const string &lhs, const string &rhs) { return false; }
	bool operator!=(const string &lhs, const char *rhs) { return false; }
	bool operator!=(const char *lhs, const string &rhs) { return false; }
	bool operator<(const string &lhs, const string &rhs) { return false; }
	bool operator<(const string &lhs, const char *rhs) { return false; }
	bool operator<(const char *lhs, const string &rhs) { return false; }
	bool operator<=(const string &lhs, const string &rhs) { return false; }
	bool operator<=(const string &lhs, const char *rhs) { return false; }
	bool operator<=(const char *lhs, const string &rhs) { return false; }
	bool operator>(const string &lhs, const string &rhs) { return false; }
	bool operator>(const string &lhs, const char *rhs) { return false; }
	bool operator>(const char *lhs, const string &rhs) { return false; }
	bool operator>=(const string &lhs, const string &rhs) { return false; }
	bool operator>=(const string &lhs, const char *rhs) { return false; }
	bool operator>=(const char *lhs, const string &rhs) { return false; }
}