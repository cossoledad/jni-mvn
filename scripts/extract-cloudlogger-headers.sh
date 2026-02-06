#!/usr/bin/env bash
set -euo pipefail

GROUP_PATH="com/example"
ARTIFACT_ID="cloud-logger"
VERSION="1.0-SNAPSHOT"

M2_REPO="${M2_REPO:-$HOME/.m2/repository}"
JAR_PATH="${M2_REPO}/${GROUP_PATH}/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_ID}-${VERSION}.jar"
OUT_DIR="${1:-Foundation/.deps/cloud-logger}"

if [[ ! -f "${JAR_PATH}" ]]; then
  echo "cloud-logger jar not found: ${JAR_PATH}" >&2
  echo "Run: mvn -pl JniMvnLib/CloudLogger -DskipTests install" >&2
  exit 1
fi

mkdir -p "${OUT_DIR}"
rm -rf "${OUT_DIR}/native"

(
  cd "${OUT_DIR}"
  jar xf "${JAR_PATH}" native/include/cloud_logger_bridge.hpp
)

echo "Extracted headers to: ${OUT_DIR}/native/include"
