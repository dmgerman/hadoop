//
// deadline_timer.hpp
// ~~~~~~~~~~~~~~~~~~
//
// Copyright (c) 2003-2014 Christopher M. Kohlhoff (chris at kohlhoff dot com)
//
// Distributed under the Boost Software License, Version 1.0. (See accompanying
// file LICENSE_1_0.txt or copy at http://www.boost.org/LICENSE_1_0.txt)
//

#ifndef ASIO_DEADLINE_TIMER_HPP
#define ASIO_DEADLINE_TIMER_HPP

#if defined(_MSC_VER) && (_MSC_VER >= 1200)
# pragma once
#endif // defined(_MSC_VER) && (_MSC_VER >= 1200)

#include "asio/detail/config.hpp"

#if defined(ASIO_HAS_BOOST_DATE_TIME) \
  || defined(ASIO_CPP11_DATE_TIME) \
  || defined(GENERATING_DOCUMENTATION)

#include "asio/detail/socket_types.hpp" // Must come before posix_time.
#include "asio/basic_deadline_timer.hpp"

#if defined(ASIO_HAS_BOOST_DATE_TIME)

#include "asio/detail/push_options.hpp"
#include <boost/date_time/posix_time/posix_time_types.hpp>
#include "asio/detail/pop_options.hpp"

#elif defined(ASIO_CPP11_DATE_TIME)

#include "asio/detail/chrono_time_traits.hpp"
#include "asio/wait_traits.hpp"
#include <chrono>

#endif

namespace asio {

#if defined(ASIO_HAS_BOOST_DATE_TIME)
/// Typedef for the typical usage of timer. Uses a UTC clock.
typedef basic_deadline_timer<boost::posix_time::ptime> deadline_timer;

#elif defined(ASIO_CPP11_DATE_TIME)

typedef basic_deadline_timer<
    std::chrono::system_clock,
    detail::chrono_time_traits<std::chrono::system_clock,
                               wait_traits<std::chrono::system_clock>>>
    deadline_timer;

#endif

} // namespace asio

#endif // defined(ASIO_HAS_BOOST_DATE_TIME)
       // || defined(ASIO_CPP11_DATE_TIME)
       // || defined(GENERATING_DOCUMENTATION)

#endif // ASIO_DEADLINE_TIMER_HPP
