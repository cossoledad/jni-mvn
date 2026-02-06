rm -rf ~/.m2/repository/com/example/cloud-logger/
mvn -pl JniMvnLib/CloudLogger -DskipTests install
./scripts/extract-cloudlogger-headers.sh
rm -rf ./Foundation/build/ ./Foundation/generated/
cmake -S Foundation -B Foundation/build \
  -DCLOUD_LOGGER_INCLUDE_DIR=$(pwd)/Foundation/.deps/cloud-logger/native/include \
  -DCLOUD_LOGGER_LIB_DIR=$(pwd)/Foundation/.deps/cloud-logger/native/lib
cmake --build Foundation/build -j
rm -rf ./Backend/target/

export FOUNDATION_JNI_LIB=$(pwd)/Foundation/build/lib/libfoundation_jni.so
export CLOUD_LOGGER_NATIVE_DIR=$(pwd)/Foundation/.deps/cloud-logger/native/lib