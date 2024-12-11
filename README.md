# mandrel-microbenchmarks
Benchmarks for the Mandrel performance improvement initiative.

Navigate to either directory:
- `polymorphism-investigation` (benchmarks for the investigating the effect of polymorphism on inlining) or 
- `benchmarks` (general benchmarksfor Quarkus focus areas) 

### Basic benchmarking:
Build 

`mvn clean package -DskipTests`

 Run benchmark

`java -jar target/benchmarks.jar -f 1 -i 1 -wi 1 -tu us`

### Profile branches and cycles:
Build

`mvn clean package -DskipTests -Dquarkus.package.jar.decompiler.enabled=true -Dquarkus.native.debug.enabled -Dquarkus.native.additional-build-args=-H:-DeleteLocalSymbols` 

Run benchmarks

`java -jar target/benchmarks.jar  -f 1 -i 1 -wi 1 -r 1 -w 1 -tu ns -prof perfnorm:events=branches,instructions,cycles`

### Profile hot methods with assembly:
Build 


`mvn package -Ddebug=true -DbuildArgs=-H:-DeleteLocalSymbols,-H:+SourceLevelDebug,-H:+TrackNodeSourcePosition,-H:+DebugCodeInfoUseSourceMappings`

Run benchmarks

` $JAVA_HOME/bin/java  -jar target/benchmarks.jar -f 1 -i 1 -wi 1 -tu us -prof org.mendrugo.fibula.DwarfPerfAsmProfiler:events=cycles:P`

Annotate assembly of hottset single method

`perf annotate -i <benchmark name .perfbin>`

Show report for all hottest methods (you can further annotate specific methods with 'a' once in report mode).

`perf report -i <benchmark name .perfbin>`

