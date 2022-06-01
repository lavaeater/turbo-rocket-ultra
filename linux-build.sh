#!/bin/bash
rm -rf ../turbo-build/out-linux
rm ../turbo-build/zips/linux/turbo-rocket-ultra-linux.zip

java -jar ../turbo-build/packr-all-4.0.0.jar \
     --platform linux64 \
     --jdk /home/tommie/.sdkman/candidates/java/current \
     --useZgcIfSupportedOs \
     --executable TurboRocketUltra \
     --classpath ./lwjgl3/build/libs/turbo-rocket-ultra-0.0.1.jar \
     --mainclass core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G XstartOnFirstThread \
     --resources assets/* \
     --output ../turbo-build/out-linux

7z a ../turbo-build/zips/linux/turbo-rocket-ultra-linux.zip ../turbo-build/out-linux/*

butler push ../turbo-build/zips/linux lavaeater/turbo-rocket-ultra:linux
