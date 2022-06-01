#!/bin/bash
rm -rf ../turbo-build/out-win
rm ../turbo-build/turbo-rocket-ultra-win.zip

java -jar ../turbo-build/packr-all-4.0.0.jar \
     --platform windows64 \
     --jdk ../turbo-build/openjdk-18.0.1.1_windows-x64_bin.zip \
     --useZgcIfSupportedOs \
     --executable TurboRocketUltra \
     --classpath ./lwjgl3/build/libs/turbo-rocket-ultra-0.0.1.jar \
     --mainclass core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G XstartOnFirstThread \
     --resources assets/* \
     --output ../turbo-build/out-win

7z a ../turbo-build/zips/win/turbo-rocket-ultra-win.zip ../turbo-build/out-win/*

butler push ../turbo-build/zips/win lavaeater/turbo-rocket-ultra:win
