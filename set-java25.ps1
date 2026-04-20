$javaPath = "C:\Program Files\Java\jdk-25"
$env:JAVA_HOME = $javaPath
$env:Path = "$javaPath\bin;" + $env:Path
echo "Switched to Java 25"
java -version
