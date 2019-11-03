# Logviewer
Yet another log viewer. You know what it does.

### Features
- Multiple filter panes with both simple contains and regex filtering
- GOTO line on filter pane click
- Tab view for multiple opened files 

#### Roadmap
- Structured log: Split and extract each log line into columns for easy access
- Sort lines

### Requirements
- Java 11
- Gradle 5.6 (optional if gradle wrapper is used)

### Run the application

Because OpenJfx has a lot of operating system dependencies, see [here](https://openjfx.io/openjfx-docs), it's 
impractical to create an uber jar or installation.

To run the app simply clone the repository and run `./gradlew run` on the root folder. You must run this command 
with `Java11` in your path. 
