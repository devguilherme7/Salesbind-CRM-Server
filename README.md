## Pre-requirements

Before running the project, make sure you have the following installed:

- **Java JDK 21**: [Download here](https://www.oracle.com/br/java/technologies/downloads/#java21)
- **Docker** (including Docker Compose): [Download here](https://www.docker.com/products/docker-desktop/)

> Make sure Docker is running before starting the application with Docker Compose

## Running the Application with Docker and Docker Compose (Development Mode)

We use a hybrid approach for development:

- External dependencies (such as the database and other services) run inside Docker containers, ensuring consistency
  across environments.
- The Spring Boot application runs directly on the host machine, enabling hot-reloading in your IDE and faster feedback
  loops.

1. Start the dependent services with Docker Compose: `docker-compose -f docker-compose-development.yml up -d`
2. Run the Spring Boot application: `./gradlew bootRun`.

This approach gives you the stability of containerized services and the agility of local development.

## MailHog for Email Testing

In the development environment, we use [MailHog](https://github.com/mailhog/MailHog) to capture and display emails sent
by the application. This is useful for testing features like user registration, notifications, and other email-based
workflows without sending real emails.

After starting the development environment, you can access the MailHog web interface
at [http://localhost:8025](http://localhost:8025). All emails sent by the application will appear in the MailHog inbox,
where you can view their content, headers, and other details.

## Swagger API Documentation

This projects uses **Swagger / OpenAPI** to provide interactive REST API documentation.

Once the application is running, open your browser and navigate to:
http://localhost:8080/swagger-ui/index.html

## Checkstyle and Spotless

To maintain code quality and a consistent style across the project, we
use [Checkstyle](config/checkstyle/checkstyle.xml) for static analysis and Spotless for automatic formatting.

You can run the checks with the following command:

```shell
./gradlew checkstyleMain checkstyleTest
```

Checkstyle reports are generated in the `build/reports/checkstyle/` directory. To get real-time feedback in your
development environment, it is highly recommended to integrate Checkstyle directly into your IDE. This helps catch
issues early, before you even commit:

- **Intellij IDEA**: Install the [Checkstyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) plugin
- **Eclipse**: Use the [Eclipse Checkstyle Plugin](https://checkstyle.org/eclipse-cs/)

After installing the plugin, configure it to use the project's checkstyle configuration file located at
`config/checkstyle/checkstyle.xml`.

To automatically format all Java source files in the project, run:

```shell
./gradlew spotlessApply
```

It is highly recommended to run this command before committing any changes to ensure your code aligns with the project's
formatting standards.

## Spotbugs

SpotBugs analyzes the compiled bytecode and reports likely bugs, performance issues, or risky constructs.

Run the analysis:

```shell
./gradlew spotbugsMain spotbugsTest
```

HTML reports are generated in the build/reports/spotbugs/ directory. Open the `main.html` and `test.html` files in a web
browser to review any findings.

## Code Coverage Reports (JaCoCo)

Code coverage is generated using JaCoCo. The report is automatically generated after the tests are run. Run the tests to
generate the coverage data:

```shell
./gradlew jacocoTestReport
```

The HTML report can be found at: `build/reports/jacoco/index.html`. Open this file in your web browser to view
the detailed coverage report.
