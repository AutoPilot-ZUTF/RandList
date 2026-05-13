#!/bin/zsh

cd "/Users/autopilotzutf/INHN0001 Retake/RandList" || exit

export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH

mvn clean package

rm -rf target/deps target/app-input RandList.app
mkdir -p target/deps target/app-input

mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/deps

cp target/RandList-1.0-SNAPSHOT.jar target/app-input/
cp target/deps/*.jar target/app-input/

jpackage \
  --type app-image \
  --name RandList \
  --input target/app-input \
  --main-jar RandList-1.0-SNAPSHOT.jar \
  --main-class com.xinyang.randlist.Launcher \
  --icon "/Users/autopilotzutf/INHN0001 Retake/RandList/src/main/resources/appIcon.icns"