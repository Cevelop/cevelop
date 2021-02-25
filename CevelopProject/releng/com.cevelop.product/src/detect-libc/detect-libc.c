#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
	size_t n = confstr(_CS_GNU_LIBC_VERSION,NULL,(size_t) 0);

	char* glibcVersionBuf = (char*) malloc(n);

	if (glibcVersionBuf == NULL) {
		return -1;
	}

	confstr(_CS_GNU_LIBC_VERSION, glibcVersionBuf, n);

	char* startOfversion = strchr(glibcVersionBuf, '2');

	if(startOfversion == NULL) {
		return 1;
	}

	float version = atof(startOfversion);

	if(version > 2.13) {
		return 0;
	} else {
		return 1;
	}
}
