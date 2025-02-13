# Projet de Programmation M1 - Java Chess

# Setup

### Clone the repository
```bash
git clone git@gitlab.emi.u-bordeaux.fr:pdp-2025/chess-2.git
cd chess-2/
```

### For contributors
Contributors should set up the pre-commit hook by running
```bash
chmod +x setup-hooks.sh
./setup-hooks.sh
```

# Installation
## 1. Docker

### Prerequisites
The following are required to run the project using Docker:
- Docker

### Build the project
- `cd chess/` to enter the project folder
- `sudo docker build -t chess .` to package the project inside a container named "chess"

### Enter the container
From the `chess` folder :
- `sudo docker run --rm -it chess bash` to enter the project container

You can now use the commands as specified in **Usage**.

## 2. Local
### Prerequisites
The following are required to run the project:
- Java 17
- Maven

#### How to switch to Java 17 ?
- Run `update-java-alternatives --list` to see all available Java versions:
  - Java 17 is available: Run `sudo update-java-alternatives --set /path/to/java/version` to set your current Java version.
  - Java 17 is not available: Run `sudo apt install openjdk-17-jdk` and re-run previous commands to set the correct Java version.

### Build the project
- `cd chess/` to enter the project folder
- `mvn clean install` to install dependencies and run tests

# Usage

### Run the tests
From the local `chess` or Docker `app` folder :
- `mvn clean test` to install dependencies and run tests

### Run the Application
From the local `chess` or Docker `app` folder :
- `mvn javafx:run -Djavafx.args="args"` to run the app with given args
- To print all available arguments, you can run `mvn javafx:run -Djavafx.args="-h"`

### Generate a coverage report
From the local `chess` or Docker `app` folder :
- `mvn clean test` to install dependencies and run tests
- `mvn jacoco:report` to generate the report

The reports will be available under `target/site/jacoco`

# About

### Developers
- Mathilde Chollon
- Iwen Jomaa
- Denis Demirci
- Valentin Jonquière
- Jonathan Landry

### Languages
Currently available in:
- English

Targeted languages:
- French

### Compatibility
The project has been tested successfully under:
- Ubuntu 24.04 (Noble Numbat)
- Debian 10 (Buster)
- Alpine 3.21.2