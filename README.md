# jni-mvn demo

## 目标（完全解耦）

- `CloudLogger` 是独立库：
  - Java 侧通过 Maven 引入（实现与自动注册都在库内）。
  - Native 侧有自己的动态库（`libcloudlogger_bridge.so` + `libcloudlogger_jni.so`），由库自己加载。
- `Foundation` 是业务 C++ 库：
  - 只 include `cloud_logger_bridge.hpp` 头文件并调用接口。
  - 不持有日志实现，不加载日志库动态库。
- `Backend`：
  - Java 端只引入 `cloud-logger` 依赖。
  - 业务库 `foundation_jni` 由 Backend 自己加载。

## 模块说明

### 1) CloudLogger 独立库

- Native Bridge 头文件：
  - `JniMvnLib/CloudLogger/native/include/cloud_logger_bridge.hpp`
- Native 实现：
  - `JniMvnLib/CloudLogger/native/src/cloud_logger_bridge.cpp`
  - `JniMvnLib/CloudLogger/native/swig/cloud_logger.i`
  - `JniMvnLib/CloudLogger/native/CMakeLists.txt`
- Java 实现与自动初始化：
  - `JniMvnLib/CloudLogger/src/main/java/com/example/cloudlogger/DefaultCloudLogger.java`
  - `JniMvnLib/CloudLogger/src/main/java/com/example/cloudlogger/CloudLoggerNativeLoader.java`
  - `JniMvnLib/CloudLogger/src/main/java/com/example/cloudlogger/CloudLoggerApplicationContextInitializer.java`

### 2) Foundation（只消费头文件）

- `Foundation/src/foundation_math.cpp` 只调用：
  - `cloudlogger::CloudLoggerRegistry::upload(...)`
- `Foundation/CMakeLists.txt` 强制要求外部参数：
  - `CLOUD_LOGGER_INCLUDE_DIR`
  - `CLOUD_LOGGER_LIB_DIR`

### 3) Backend

- `Backend/pom.xml` 依赖 `com.example:cloud-logger`
- `Backend/src/main/java/com/example/backend/MathController.java` 负责加载 `foundation_jni`

## 构建顺序

1. 安装 CloudLogger Maven 库（会自动触发 native CMake 构建）

```bash
mvn -pl JniMvnLib/CloudLogger -DskipTests install
```

产物会放到：
- `JniMvnLib/CloudLogger/src/main/resources/native/lib/`
- `JniMvnLib/CloudLogger/target/generated-sources/cloudlogger-swig/`

2. （可选）从 Maven jar 提取头文件/动态库供 Foundation 使用

```bash
./scripts/extract-cloudlogger-headers.sh
```

3. 构建 Foundation（仅引用 CloudLogger 的 include + lib）

```bash
cmake -S Foundation -B Foundation/build \
  -DCLOUD_LOGGER_INCLUDE_DIR=$(pwd)/Foundation/.deps/cloud-logger/native/include \
  -DCLOUD_LOGGER_LIB_DIR=$(pwd)/Foundation/.deps/cloud-logger/native/lib
cmake --build Foundation/build -j
```

4. 启动 Backend

```bash
export FOUNDATION_JNI_LIB=$(pwd)/Foundation/build/lib/libfoundation_jni.so
export CLOUD_LOGGER_NATIVE_DIR=$(pwd)/Foundation/.deps/cloud-logger/native/lib
mvn -pl Backend spring-boot:run
```

## 接口验证

```bash
curl "http://localhost:8080/api/math/add?a=3&b=5"
```

返回：

```json
{"a":3,"b":5,"result":8}
```

并会看到 `CloudLogger` 的日志输出，说明 C++ 已通过独立日志库回调到 Java 实现。
