%module(directors="1") foundation

%{
#include "cloud_logger_bridge.hpp"
#include "foundation_math.hpp"
%}

%include <std_string.i>

%feature("director") foundation::CloudLogger;

%include "cloud_logger_bridge.hpp"
%include "foundation_math.hpp"
