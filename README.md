# Java Chess Project

# Setup

### Clone the repository
```bash
git clone git@github.com:Vjonquiere/chess.git
cd chess/
```

### For contributors
1. Contributors should set up the pre-commit hook by running
```bash
chmod +x setup-hooks.sh
./setup-hooks.sh
```
2. Setup development environment

It is possible that your IDE don't find the _BoardLoaderBaseListener_ class. It's because
some of them don't use the `target/` folder for sources.
Here is how to fix:

  - IntelliJ: 
    - Build the project 
    - Go to file -> project structure -> modules
    - Change `target/generated-sources/antlr4` from Excluded to **Sources**

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

## 3. Using release
### Prerequisites
- Java 17

### Run the App
- Download the latest release on the Github page ([here](https://github.com/Vjonquiere/chess/releases))
- Run the jar with `java -jar chess-x.x.x.jar` (x.x.x = version number)

# Usage

### Run the tests
From the local `chess` or Docker `app` folder :
- `mvn clean test` to install dependencies and run tests

The tests are by default ran in headless mode.
You can skip the GUI tests by running :
`mvn clean test -DexcludedGroups=gui`

### Run the Application

#### Using chess binary
From the local `chess` or Docker `app` folder :
- `./chess` followed by the arguments you want to run with. If the project has not been built yet, it will be done without running the tests before execution.

#### Using maven
From the local `chess` or Docker `app` folder :
- `mvn javafx:run -Djavafx.args="args"` to run the app with given args
- To print all available arguments, you can run `mvn javafx:run -Djavafx.args="-h"`

### Generate a coverage report
From the local `chess` or Docker `app` folder :
- `mvn clean test` to install dependencies and run tests
- `mvn jacoco:report` to generate the report

The reports will be available under `target/site/jacoco`

### Learn how to use the app
You can have a small brief by running the program with `-h`, but a complete guide is available in the `chess/README` file. For example, it contains more details on how to play on the different interfaces and explanation on special features (UCI, monitoring, ...).

# Uninstall

A `--clean` option is available with the chess binary. From the local folder, run the `./chess --clean` command to remove all chess sources and resources folder used for themes and settings.

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
- French

### Compatibility
The project has been tested successfully under:
- Ubuntu 24.04 (Noble Numbat)
- Debian 10 (Buster)
- Alpine 3.21.2