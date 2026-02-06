#ifndef CLOUD_LOGGER_BRIDGE_HPP
#define CLOUD_LOGGER_BRIDGE_HPP

#include <string>

namespace foundation {

class CloudLogger {
public:
    virtual ~CloudLogger() = default;
    virtual void upload(const std::string& category, const std::string& message) = 0;
};

class CloudLoggerRegistry {
public:
    static void registerLogger(CloudLogger* logger) {
        instance = logger;
    }

    static CloudLogger* get() {
        return instance;
    }

private:
    inline static CloudLogger* instance = nullptr;
};

} // namespace foundation

#endif // CLOUD_LOGGER_BRIDGE_HPP
