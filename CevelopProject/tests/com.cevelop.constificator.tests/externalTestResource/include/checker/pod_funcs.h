#ifndef __POD_FUNCS_H
#define __POD_FUNCS_H

// C3 / C26
void func_ref_const(int const &) { }
void func_ref_nonconst(int &) { }

// C5 / C28
void func_const_ptr_const(int const * const) { }
void func_nonconst_ptr_const(int const *) { }
void func_const_ptr_nonconst(int * const) { }
void func_nonconst_ptr_nonconst(int *) { }

// C7 & C18 & C30
void func_ref_const_ptr_const(int const * const &) { }
void func_ref_const_ptr_nonconst(int * const &) { }

// C18
void func_ref_nonconst_ptr_nonconst(int * const &) { }
void func_ref_nonconst_ptr_const(int const * &) { }

// C20
void func_nonconst_ptr_nonconst_ptr_const(char const * *) { }
void func_nonconst_ptr_const_ptr_const(char const * const *) { }
void func_const_ptr_nonconst_ptr_const(char const * * const) { }
void func_const_ptr_const_ptr_const(char const * const * const) { }
void func_const_ptr_const_ptr_nonconst(char * const * const) { }

// C22
void func_ref_const_ptr_nonconst_ptr_const(char const * * const &) { }
void func_ref_const_ptr_const_ptr_const(char const * const * const &) { }

#endif
