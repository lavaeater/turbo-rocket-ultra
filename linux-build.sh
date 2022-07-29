#!/bin/bash
rm -rf ../turbo-build/out-linux

java -jar ../turbo-build/packr-all-4.0.0.jar \
     --platform linux64 \
     --jdk ../turbo-build/linux.tar.gz \
     --useZgcIfSupportedOs \
     --executable TurboRocketUltra \
     --classpath ./lwjgl3/build/libs/TurboRocketUltra-0.0.1.jar \
     --mainclass core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G XstartOnFirstThread \
     --resources assets/* \
     --output ../turbo-build/out-linux

butler push ../turbo-build/out-linux lavaeater/turbo-rocket-ultra:linux
