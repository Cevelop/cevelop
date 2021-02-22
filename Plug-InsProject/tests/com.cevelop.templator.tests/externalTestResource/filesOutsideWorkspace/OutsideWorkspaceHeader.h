#ifndef OUTSIDEWORKSPACEHEADER_H_
#define OUTSIDEWORKSPACEHEADER_H_

template<typename T, typename F>
void extern_inner(T first, F second) {
}

template<typename T>
T extern_outer(T first, int i) {
	extern_inner(first, i);
	return first;
}
#endif /* OUTSIDEWORKSPACEHEADER_H_ */
