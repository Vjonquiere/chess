# Projet de Programmation M1 - Java Chess

# Setup

### Clone the repository
```bash
git clone git@gitlab.emi.u-bordeaux.fr:pdp-2025/chess-2.git
cd chess-2/
```

### For contributors
Contributors should setup the pre-commit hook by running
```bash
chmod +x setup-hooks.sh
./setup-hooks.sh
```

# Installation
## 1. Docker

### Prerequisites
The following are required to run the project using docker:
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
- run `update-java-alternatives --list` to see all available java versions:
  - Java 17 is available: run `sudo update-java-alternatives --set /path/to/java/version` to set your current java version
  - Java 17 is not available: run `sudo apt install openjdk-17-jdk` and re-run previous commands to set the correct java version

### Clone the repository
```bash
git clone git@gitlab.emi.u-bordeaux.fr:pdp-2025/chess-2.git
cd chess-2/
```
### Build the project
- `cd chess/` to enter the project folder
- `mvn clean install` to install dependence and run tests

## Usage

### Run the tests
From the local `chess` or Docker `app` folder :
- `mvn clean test` to install dependencies and run tests

### Run the Application
From the local `chess` or Docker `app` folder :
- `mvn javafx:run -Djavafx.args="args"` to run the app with given args
- To print all available arguments you can run `mvn javafx:run -Djavafx.args="-h"`
