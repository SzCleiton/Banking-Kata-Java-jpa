# Banking Kata - Java

## Overview

This project illustrates TDD & Clean Architecture implementation in Java, showing the Use Case Driven Development
Approach.

This demo was created for the purposes of meetup series on TDD & Clean Architecture.

## Business Requirements

We implement a Banking system with the following use cases:

- Open account
- Withdraw funds
- Deposit funds
- View account

We also have authentication with Keycloak - a realm with client_id and with client_credentials flow enabled.

## Architecture

The overall architecture is split into: 
- Application Core (`core`) layer contains the business logic. 
- Adapter (`adapter`) layer contains the infrastructural concerns.

The Application Core(`core`) is composed of: 
- Ports (`ports`), representing an interface to the Application Core, isolating it from infrastructural concerns. There are both Driver (`driver`) ports, representing use cases; and Driven (`driven`) ports, representing gateways.
- Internals (`internal`), representing the internal implementation of the Application Core, more specifically, the implementation of the Driver (`driver`) ports. This implementation can be anything. Here we illustrate the implementation with Clean Architecture (`cleanarch`) approach and the CRUD (`crud`) approach, but other approaches are possible too. The use case tests are independent of the implementation approach; the internal implementation is thus swappable.

The Adapter (`adapter`) layer is composed of driver adapters and driven adapters.
- Driver adapters: REST API Adapter (`adapter-restapi-*`)
- Driven adapters: Persistence Adapters (`adapter-persistence-*`), Random Number Generation Adapters (`adapter-generation-*`), Time Adapters (`adapter-time-*`), Third Party Integration Adapters (`adapter-thirdparty-*`), and Messaging Adapters (`adapter-messaging-*`).

The application can be executed via `startup`.

## Tests

- Core Layer: Unit Tests targeting the Driver Ports.
- Adapter Layer: Integration Tests targeting the Driven Ports. In cases of integrating with third-party systems or microservices, we use Contract Testing.

As can be seen below, you can separately run these fast-running and slow-running tests.

## Prerequisites

- OpenJDK 17
- Docker

## Running Gradle

Make sure that Gradle works:

```
./gradlew
```

## Environment Setup

Remove old images/volumes:

```shell
docker-compose down -v
```

Apply the environment variables (Windows):

```shell
. .\env\env.ps1
```

Apply the environment variables (for Linux/Mac):

```shell
source ./env/env.sh
```

Apply the environment variable in the ide, in case if it needs to run the application from ide UI button
For intellij, there is a plugin EnvFile can help to run the env file.

```shell
#chmod 777 ./env/env.intellij.ui
Then select the Run->Edit configuration which will then provide the option to import and run the file.
```


For Mac only, you need to build a custom Keycloak image to enable Keycloak to work on Mac M1.
This is due to a reported Mac-specific issue https://github.com/docker/for-mac/issues/5310.
For any other OS, please skip this step, because this issue is Mac-specific:

```shell
cd startup
chmod +x ./src/main/resources/keycloak/build-keycloak-image-m1.zsh
./src/main/resources/keycloak/build-keycloak-image-m1.zsh 16.0.0
```

Run docker:

```shell
docker-compose up -d
```

## verify if rabbitmq admin console is accessible and the docker image is running without issues
```shell
http://localhost:15672
user: admin
pass: admin


```

## Running Unit Tests

Run unit tests for `core`:

```
./gradlew coreTest
```

## Running Integration Tests

As a prerequisite, please see *Environment Setup*.

To run all the adapter integration tests:

```shell
./gradlew adapterTest
```

Note: Currently, this fails locally due to open issues with Flyway & MongoDB( #111 and #114).

## Running System Tests

As a prerequisite, please see *Environment Setup*.

To run the whole system tests:

```shell
./gradlew systemTest
```

Note: Currently, this fails locally due to open issues with Flyway & MongoDB( #111 and #114).

## Running all Tests

As a prerequisite, please see *Environment Setup*.

In the above instructions, we ran the tests separately.
You can run them all:

```shell
./gradlew test
```

Note: Currently, this fails locally due to open issues with Flyway & MongoDB( #111 and #114).

## Running Code Coverage 

Run code coverage (executes Jacoco):

```shell
./gradlew codeCoverage
```

To run it for specific project:
```shell
./gradlew core:codeCoverage
```

## Running Mutation Testing

Run mutation testing (executes pitest):

```shell
./gradlew mutationTest
```

<!--- TODO: Add pitest report aggregation after issue is resolved 
See issue #80 Pitest report aggregation not working --->

## Viewing Reports

See the `build\reports` directory for the generated reports for test results, code coverage and mutation testing.

Reports:

- build\reports\tests
- build\reports\jacoco
- build\reports\pitest

## Running Spring Boot

To manually run the app.

```shell
./gradlew runApp
```

Then `CTRL+C` to terminate the app.

<!--- TODO: VC: Building also the entire app, maybe running with fakes? --->

## Optional Notes

The following are for additional reading, you do not need to execute these, but you can if you wish.

Environment variables are located inside the `env` folder. You can optionally choose to edit them.

You can choose to run the tests via IntelliJ UI. 

In the case of integration tests (for `adapters`) you'd have to specify environment variables before you run the tests.
To do that, you can copy the text from the file `env/env.intellij.ui` into the `Environment variables` section into `Tests in 'banking-kata.adapters'` configuration.

To run Docker with the environment file:

```shell
docker-compose --env-file=env/.env.local up -d
```

You can run the integration tests individually, e.g. if you modified a module (in this way you avoid waiting for all of them to finish):

```shell
./gradlew adapter-persistence-jpa:test
```

For code coverage, the underlying call is:

```shell
./gradlew jacocoTestReport
```

For mutation testing, the underlying call is:

```shell
./gradlew pitest
```

For flyway operation from CLI:
There will be flyway tasks available in the gradle as the plugin is applied to adapter-persistence-jpa.
Tasks can be executed by the following way -

```shell
./gradlew adapter-persistence-jpa:flywayInfo  //will provide the schema file under flywayInfo
./gradlew adapter-persistence-jpa:flywayClean //will clear up the schema from database
./gradlew adapter-persistence-jpa:flywayMigrate //will apply the db schema migration into database
```
