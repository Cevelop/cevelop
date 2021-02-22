#ifndef __ARRAY_CLS_H
#define __ARRAY_CLS_H

struct cls
  {
  explicit cls(int number) :
    m_number{number}
    {
    }

  void operator++(int) const
    {
    }

  void operator++() const
    {
    }

  void operator--(int)
    {
    }

  void operator--()
    {
    }

  void inspect() const
    {
    }

  void modify()
    {
    }

  int m_number{};

  int const m_fixed{};
  };

#endif
