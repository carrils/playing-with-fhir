Various examples of FHIR interactions using the HAPI FHIR Library
=============================================================
Overview
---------

The examples and the framework of this repository comprise my personal notes and experiments using the [HAPI FHIR Library](https://hapifhir.io/hapi-fhir/).

As such there will be some instances of code that are not particularly useful to those seeking examples of HAPI FHIR. The majority of the source code however is commented to explain in greater detail what is happening and how it might be useful.

Examples
---------
All examples can be found in the [org.mitre.phfic](src/main/java/org/mitre/phfic) folder.

These include, among other things, examples of:
* Creating FHIR patient records
* Encoding a patient to FHIR
* Converting a FHIR patient resource to JSON or XML
* Creating a Generic (Fluent) Client
* Searching a given FHIR server with that client
* Validating a resource with a validator

Acknowledgments
------------------
This repository was created by studying and borrowing examples/code from various sources:
* [HAPI FHIR Documentation](https://hapifhir.io/hapi-fhir/docs/)
* [FHIR Drills](https://fhir-drills.github.io/index.html)
* [FirelyTeam/fhirstarters](https://github.com/FirelyTeam/fhirstarters)
