# MCI

[![Build Status](https://travis-ci.com/Binarios/Mci.svg?branch=master)](https://travis-ci.com/Binarios/Mci)
[![Code Grade](https://www.code-inspector.com/project/145/status/svg)](https://www.code-inspector.com/project/145/status/svg) 


MCI is a web app based on Java that generates automatically games using an Ontology.

### Tech
* [Opend Jdk 12]
* [Spring]
* [Apache Jena]
* [Openllet]

### Installation
MCI requires [Opend Jdk 12] .

Git clone the project:
```sh
git clone https://github.com/Binarios/Mci.git
```
Also clone the dependent repository:
```sh
git clone https://github.com/Binarios/MciOntology.git
```
The directory should look like this:
```bash
|-parent
|-------/Mci
|-------/MciOntology
```

Once cloned you can run the following command at the root of the project:

```sh
mvn clean install
```

which will install all the dependencies, build the project, trigger the tests and install on the local repo

### Todos

 - Write MORE Tests

License
----

[Apache License 2.0]

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [Opend Jdk 12]: <https://openjdk.java.net/projects/jdk/12/>
   [Spring]: <https://spring.io/projects/spring-framework>
   [Apache Jena]: <https://jena.apache.org/index.html>
   [Openllet]: <https://github.com/Galigator/openllet>
   [Apache License 2.0]: <https://github.com/Binarios/Mci/blob/master/LICENSE>
