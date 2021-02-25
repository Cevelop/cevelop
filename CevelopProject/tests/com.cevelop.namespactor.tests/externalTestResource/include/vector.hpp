#ifndef VETOR_HPP_
#define VETOR_HPP_

namespace std {
inline namespace __1 {
	template <typename T>
	struct vector {
		typedef T* iterator;
	};
	struct string{
		typedef char *iterator;
	};
}}
#endif /* VETOR_HPP_ */
