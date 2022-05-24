#!/bin/bash
rm -rf ../turbo-build/out-mac

java -jar ../turbo-build/packr-all-4.0.0.jar \
     --platform mac \
     --jdk ../turbo-build/openjdk-18.0.1.1_macos-x64_bin.tar.gz \
     --useZgcIfSupportedOs \
     --executable TurboRocketUltra \
     --classpath ./lwjgl3/build/libs/turbo-rocket-ultra-0.0.1.jar \
     --mainclass core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G XstartOnFirstThread \
     --resources assets/* \
     --output ../turbo-build/out-mac
