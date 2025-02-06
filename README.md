# Projet de Programmation

## Java Chess

### Prerequisites
The following are required to run the project:
- Java 17
- Maven

#### How to switch to Java 17 ?
- run `update-java-alternatives --list` to see all available java versions:
  - Java 17 is available: run `sudo update-java-alternatives --set /path/to/java/version` to set your current java version
  - Java 17 is not available: run `sudo apt install openjdk-17-jdk` and re-run previous commands to set the correct java version

### Installation
Clone the repository
```bash
git clone git@gitlab.emi.u-bordeaux.fr:pdp-2025/chess-2.git
cd chess-2/
```
### Build the project
- `cd chess/` to enter the project folder
- `mvn clean install` to install dependence and run tests

### Run the tests
- `cd chess/` to enter the project folder
- `mvn clean test` to install dependence and run tests

### Run the Application
- `cd chess/` to enter the project folder
- `mvn javafx:run -Djavafx.args="args"` to run the app with given args
- To know all available arguments you can run `mvn javafx:run -Djavafx.args="-h"`

Ce dépôt contient trois répertoires importants:

- `<project_name>/`: Code source du projet.

- `reports/preliminary`: Code LaTeX du rapport préliminaire.
- `reports/final`: Code LaTeX du rapport final.
