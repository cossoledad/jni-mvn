cmake_minimum_required(VERSION 3.20)
project(Foundation LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(SWIG REQUIRED)
include(UseSWIG)
find_package(JNI REQUIRED)

set(FOUNDATION_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/include")
set(CLOUD_LOGGER_INCLUDE_DIR "" CACHE PATH "Directory containing cloud_logger_bridge.hpp from cloud-logger Maven artifact")
if(NOT CLOUD_LOGGER_INCLUDE_DIR)
    message(FATAL_ERROR "CLOUD_LOGGER_INCLUDE_DIR is required. Point it to extracted cloud-logger native/include directory.")
endif()
if(NOT EXISTS "${CLOUD_LOGGER_INCLUDE_DIR}/cloud_logger_bridge.hpp")
    message(FATAL_ERROR "cloud_logger_bridge.hpp not found under CLOUD_LOGGER_INCLUDE_DIR=${CLOUD_LOGGER_INCLUDE_DIR}")
endif()
set(SWIG_OUT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/generated/java")
file(MAKE_DIRECTORY "${SWIG_OUT_DIR}")

set_source_files_properties(${CMAKE_CURRENT_SOURCE_DIR}/swig/foundation.i PROPERTIES CPLUSPLUS ON)
set(CMAKE_SWIG_OUTDIR ${SWIG_OUT_DIR})
set(CMAKE_SWIG_FLAGS
    -package com.example.foundation
    -I${FOUNDATION_INCLUDE_DIR}
    -I${CLOUD_LOGGER_INCLUDE_DIR}
    -directors
)

swig_add_library(foundation_jni
    TYPE SHARED
    LANGUAGE java
    SOURCES
      ${CMAKE_CURRENT_SOURCE_DIR}/swig/foundation.i
      ${CMAKE_CURRENT_SOURCE_DIR}/src/foundation_math.cpp
)

swig_link_libraries(foundation_jni ${JNI_LIBRARIES})

target_include_directories(${SWIG_MODULE_foundation_jni_REAL_NAME}
    PRIVATE
      ${FOUNDATION_INCLUDE_DIR}
      ${JNI_INCLUDE_DIRS}
      ${CLOUD_LOGGER_INCLUDE_DIR}
)

set_target_properties(${SWIG_MODULE_foundation_jni_REAL_NAME} PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/build/lib
)

add_custom_target(print_foundation_outputs ALL
    COMMAND ${CMAKE_COMMAND} -E echo "Native lib: ${CMAKE_CURRENT_SOURCE_DIR}/build/lib/libfoundation_jni.so"
    COMMAND ${CMAKE_COMMAND} -E echo "SWIG Java: ${SWIG_OUT_DIR}"
)
add_dependencies(print_foundation_outputs ${SWIG_MODULE_foundation_jni_REAL_NAME})
