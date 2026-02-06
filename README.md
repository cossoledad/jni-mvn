# jni-mvn demo

## 你要的最终边界

- `JniMvnLib/CloudLogger`：独立 Maven 日志库（Java 实现 + native 头文件资源）。
- `Foundation`：只拿到 `CloudLogger` 的 C++ 头文件接口，不包含日志实现细节。
- `Backend`：只通过 `pom.xml` 引入 `cloud-logger`，无需自己写 JNI 注册逻辑。

## 当前实现

### 1. 独立日志库（Maven 管理）

- 模块：`JniMvnLib/CloudLogger`
- C++ 头文件接口（打包进 jar 资源）：
  - `JniMvnLib/CloudLogger/src/main/resources/native/include/cloud_logger_bridge.hpp`
- Java 实现与自动装配：
  - `JniMvnLib/CloudLogger/src/main/java/com/example/cloudlogger/DefaultCloudLogger.java`
  - `JniMvnLib/CloudLogger/src/main/java/com/example/cloudlogger/CloudLoggerAutoConfiguration.java`
- 作用：应用只要引入该依赖，启动时自动加载 `foundation_jni` 并把日志实现注册到 C++ `CloudLoggerRegistry`。

### 2. Foundation 只依赖头文件

- `Foundation/src/foundation_math.cpp` 只 include：`cloud_logger_bridge.hpp`
- 不再写 JVM/JNI 管理代码。
- `Foundation/CMakeLists.txt` 强制要求传入：
  - `-DCLOUD_LOGGER_INCLUDE_DIR=...`

### 3. Backend 只保留 Maven 依赖

- `Backend/pom.xml` 只需要依赖：`com.example:cloud-logger`
- 已删除 Backend 中自定义注册器实现类。

## 构建顺序

1. 先构建 Foundation（生成 SWIG Java + JNI so）

```bash
cmake -S Foundation -B Foundation/build -DCLOUD_LOGGER_INCLUDE_DIR=$(pwd)/JniMvnLib/CloudLogger/src/main/resources/native/include/resources/native/include
cmake --build Foundation/build -j
```

2. 安装独立日志库到本地 Maven 仓库（必须先执行，确保初始化器生效）

```bash
mvn -pl JniMvnLib/CloudLogger -DskipTests install
```

3. （可选，更贴近真实“只拿头文件”）从 Maven artifact 提取头文件给 Foundation

```bash
./scripts/extract-cloudlogger-headers.sh
cmake -S Foundation -B Foundation/build -DCLOUD_LOGGER_INCLUDE_DIR=$(pwd)/Foundation/.deps/cloud-logger/native/include
cmake --build Foundation/build -j
```

4. 启动 Backend

```bash
export FOUNDATION_JNI_LIB=$(pwd)/Foundation/build/lib/libfoundation_jni.so
mvn -pl Backend spring-boot:run
```

如果你之前跑过旧版本，建议先清理快照并重装：

```bash
rm -rf ~/.m2/repository/com/example/cloud-logger/1.0-SNAPSHOT
mvn -pl JniMvnLib/CloudLogger -DskipTests install
```

## 接口验证

```bash
curl "http://localhost:8080/api/math/add?a=3&b=5"
```

会返回：

```json
{"a":3,"b":5,"result":8}
```

同时控制台会打印由 `cloud-logger` 库实现的日志上传（示例输出）。
