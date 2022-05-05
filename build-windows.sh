#!/bin/bash
rm -rf out-linux

java -jar ../packr-all-4.0.0.jar \
     --platform windows64 \
     --jdk /home/tommie/.sdkman/candidates/java/current \
     --useZgcIfSupportedOs \
     --executable TurboRocketUltra \
     --classpath /home/tommie/projects/games/turbo-rocket-ultra/lwjgl3/build/libs/turbo-rocket-ultra-0.0.1.jar \
     --mainclass core.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx1G \
     --resources assets/* \
     --output out-linux

./out-linux/TurboRocketUltra
