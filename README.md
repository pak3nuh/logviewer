# Logviewer
Yet another log viewer. You know what it does.

### Features
- Multiple filter panes with both simple contains and regex filtering
- GOTO line on filter pane click
- Tab view for multiple opened files
- Configurable number of thread to poll the file changes

### Configurations

There are some system properties that can be set in order to configure the application.
If gradle is used add the `-D` to add the property like `gradle run -Dlogviewer.threading.number=5`

- `logviewer.threading.leg.level`- Configures the logging level. Available values are
0 Trace
1 Debug
2 Info
3 Error
- `logviewer.threading.number` - The number of threads available to process file notifications

#### Roadmap
- Structured log: Split and extract each log line into columns for easy access
- Sort lines

#### Nice to have
- Change thread model to coroutines with kotlin flows for notification

### Requirements
- Java 11
- Gradle 5.6 (optional if gradle wrapper is used)

### Run the application


Because OpenJfx has a lot of operating system dependencies, see [here](https://openjfx.io/openjfx-docs), it's 
impractical to create an uber jar or installation.

To run the app simply clone the repository and run `./gradlew run` on the root folder. You must run this command 
with `Java11` in your path. 
