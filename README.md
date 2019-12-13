# Logviewer
Yet another log viewer. You know what it does.

### Features
- Multiple filter panes with both simple contains and regex filtering
- GOTO line on filter pane click
- Tab view for multiple opened files
- Configurable number of thread to poll the file changes

The number is configurable by setting the JVM property `logviewer.threading.number`. If gradle is used add the `-D` to
add the property like `gradle run -Dlogviewer.threading.number=5`

#### Roadmap
- Structured log: Split and extract each log line into columns for easy access
- Sort lines

#### Nice to have
- Change thread model to coroutines with kotlin flows for notification
- Use GraalVm for [native image support](https://gluonhq.com/gluon-substrate-and-graalvm-native-image-with-javafx-support/)

### Requirements
- Java 11
- Gradle 5.6 (optional if gradle wrapper is used)

### Run the application

Because OpenJfx has a lot of operating system dependencies, see [here](https://openjfx.io/openjfx-docs), it's 
impractical to create an uber jar or installation.

To run the app simply clone the repository and run `./gradlew run` on the root folder. You must run this command 
with `Java11` in your path. 
