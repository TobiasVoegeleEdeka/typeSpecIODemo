$javaPath = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
$env:JAVA_HOME = $javaPath
$env:Path = "$javaPath\bin;" + $env:Path
echo "Switched to Java 21"
java -version
