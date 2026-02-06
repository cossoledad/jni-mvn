#include "foundation_math.hpp"
#include "cloud_logger_bridge.hpp"

#include <string>

namespace foundation {

int FoundationMath::add(int a, int b) const {
    int result = a + b;

    cloudlogger::CloudLoggerRegistry::upload(
        "foundation-math",
        "add(" + std::to_string(a) + "," + std::to_string(b) + ")=" + std::to_string(result)
    );

    return result;
}

} // namespace foundation
