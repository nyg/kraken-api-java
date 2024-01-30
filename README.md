# A Kraken API Client in Java

[![Maven Central](https://img.shields.io/maven-central/v/dev.andstuff.kraken/kraken-api)](https://central.sonatype.com/artifact/dev.andstuff.kraken/kraken-api)

Query the [Kraken API][1] in Java.

### Examples

Run the examples with:

```shell
# input your API keys in api-keys.properties
cp examples/src/main/resources/api-keys.properties.example \
   examples/src/main/resources/api-keys.properties
 
# build project
mvn clean install

# run Examples class
mvn -q -pl examples exec:java -Dexec.mainClass=dev.andstuff.kraken.example.Examples
```

[1]: https://docs.kraken.com/rest/
