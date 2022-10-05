# JUGtoberFest 2022 – Going big with Kotlin

## Requirements
To play with this project you will need:
- [_JDK_ 17](https://adoptium.net/en-GB/temurin/releases/?version=17) – for _Java_ pipeline,
- [_Python_ 3.8](https://www.python.org/downloads/release/python-380/) – for _Python_ pipeline,
- [_Docker_](https://docs.docker.com/desktop/install/mac-install/) – for containerisation and running locally,
- [_Google Cloud CLI_](https://cloud.google.com/sdk/docs/install-sdk) – for authentication in _Google Cloud Platform_.

## Build
To build this project simply use _Gradle_ and run the following command:
```shell
./gradlew build
```
This will build the _Java_ project, as well as _Docker_ images.

## Run
To run the pipeline you should first ensure the project is correctly set up:
1. Adjust the environment variables located in [`.env` file](.env),
2. Adjust the target file path in [`Pipeline`](src/main/kotlin/pl/allegro/tech/jugtoberfest2022/Pipeline.kt),
3. Adjust the _Dataflow_ configuration in [`Configuration`](src/main/kotlin/pl/allegro/tech/jugtoberfest2022/Configuration.kt)
   (optional if running locally using `PortableRunner`).

If everything is set up properly you can use _Gradle_ and run the following command:
```shell
./gradlew runPipeline
```
This will start up the _Docker_ containers and run the job locally or on _Google Cloud Dataflow_ – depending on what you've configured.
