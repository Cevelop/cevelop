/*
 * gslrefactor.h
 *
 */

#ifndef GSLREFACTOR_H
#define GSLREFACTOR_H

#include "gsl.h"

namespace gsl
{

//
// GSL.borrower: non ownership of pointers
//
template <class T>
using borrower = T;


//
// GSL.nullable
//
template <class T>
using nullable = T;



}

#endif // GSLREFACTOR_H
