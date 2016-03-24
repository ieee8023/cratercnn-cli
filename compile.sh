mkdir -p classes

javac -J-Xms256m -J-Xmx256m -cp `sh getclasspath.sh lib`:. -d classes `find src -type f -name "*.java"` `find JSON -type f -name "*.java"`

