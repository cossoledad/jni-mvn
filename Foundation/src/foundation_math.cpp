#include "foundation_math.hpp"
#include "cloud_logger_bridge.hpp"

#include <string>

namespace foundation {

int FoundationMath::add(int a, int b) const {
    int result = a + b;

    auto* logger = CloudLoggerRegistry::get();
    if (logger != nullptr) {
        logger->upload("foundation-math", "add(" + std::to_string(a) + "," + std::to_string(b) + ")=" + std::to_string(result));
    }

    return result;
}

} // namespace foundation
