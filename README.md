# Configuration

A configuration library for Java applications. Including support for SpringBoot apps.
Easily define configs and load them into any class.

The use of Configuration is similar to a database, except it is much more lightweight. This **should not be used instead of a database**.  
The package provides a way of retaining data between runtime executions. If you save a value to a variable during runtime, the next time you run the jar it's not there

### Basic Use
First, define your configuration model class  
It can be done inside another class but it is not advised
```java
@ConfigurationModel
public class Config {
    private int number;
    // ...
    
    public int getNumber() {
        return number;
    }
    // ...
}
```
Then, add your config instance variable to your class of
choice, specify the name of the config. Naming the
configuration, uniquely identifies it (behind the scenes
it's the file that is generated, containing a json
representation of the object). So "config" would be
"{configDirectory}/config.config"
```java
public class App {
    @Configuration(name = "config")
    private static Config config;
}
```
Make sure to use the load() method on the ConfigLoader class
to instantiate the ```@Configuration``` variables for you
```java
public class App {
    public static void main(String[] args) {
        // Run the loader to instantiate instance variables
        ConfigLoader.load();
        // This only has to be done once
        
        // Then you can access the variable...
        System.out.println(config.getNumber());
    }
}
```
The ConfigLoader.load() method does not just load the
variables in the class you run it in. You can run the
method anywhere and all variables annotated with
```@Configuration``` will be loaded or created, if they
do not already exist.  
  
Save the changes you've made using this method, again,
like with the load() method this works for the entire
classpath. This method is not needed. A Shutdown Hook
is registered so a configs are saved on shutdown
```java
public class App {
    public static void main(String[] args) {
        ConfigLoader.load();
        // Change config variable
        ConfigLoader.save();
    }
}
```
### Advanced Use
#### Modifiers
Add modifiers to the configuration, they will be applied
to it when loading and saving.  
For example, make the config not readable
```java
public class App {
    @Configuration(name = "config", readable = false)
    private static Config config;
}
```
#### Default Configurations
You can specify a default configuration for a model.
This will be used if there isn't one saved. This will
overwrite the default values specified in the class.
```java
@ConfigurationModel(defaultResource = "myConfigDefault.config")
public class MyConfig {
    
}
```
The path given will look for a file within the resources root.
The contents of the default resource file should be a json
representation of the configuration that will be loaded. The
default resource must be "readable"

#### Configuring the Serializer
If you require a specific way of serialising and de-serialising
an object you will have to tell the loader how. In order to do this
you will have to configure the Gson Serializer. For example, you may
want the specific model to be printed as pretty json.
```java
@ConfigurationModel
public class MyConfig {
    @ConfigureSerializer
    public static void configure(GsonBuilder gsonBuilder) {
        gsonBuilder.setPrettyPrinting();
    }
}
```
Each model must configure the serializer in order to protect
multiple models wanting different ways of serializing the
same type of object.

#### Adding Descriptions
If your Config Model requires editing via the config directory.
In other words, the user editing the .config json file. Then
it might be useful to have a description of what values are
acceptable.

### Spring Boot
This library can be used in conjunction with Spring Boot. The additional module
provides a PostConstruct method to automatically load the configs on startup.
```gradle
compile 'org.bitbucket.srbarber1997:configuration-spring:+'
```
```xml
<dependency>
    <groupId>org.bitbucket.srbarber1997</groupId>
    <artifactid>configuration-spring</artifactid>
</dependency>
```
#### Configure
When using Spring Boot you can configure how the
Config Loader loads. Add the options to the
application.properties or application.yml
```properties
configuration.auto-load=false
configuration.log-output=false
```
```yaml
configuration:
    auto-load: false
    log-output: false
```
Both options default to true if they are not present.