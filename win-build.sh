#!/bin/bash
rm -rf ../turbo-build/out-win

java -jar ../turbo-build/packr-all-4.0.0.jar \
     --platform windows64 \
     --jdk ../turbo-build/turbo-build/openjdk-19-ea+23_windows-x64_bin.tar.gz \
     --useZgcIfSupportedOs \
     --executable TurboRocketUltra \
     --classpath ./lwjgl3/build/libs/turbo-rocket-ultra-0.0.1.jar \
     --mainclass core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G XstartOnFirstThread \
     --resources assets/* \

     --output ../turbo-build/out-win
