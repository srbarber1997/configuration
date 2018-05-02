package org.bitbucket.srbarber1997.configuration;

import com.google.common.annotations.Beta;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.bitbucket.srbarber1997.configuration.logger.ConfigLogger;
import org.bitbucket.srbarber1997.configuration.serialise.SelfDeserializable;
import org.bitbucket.srbarber1997.configuration.serialise.SelfSerializable;
import org.bitbucket.srbarber1997.configuration.util.Scrambler;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class that is used to load configs into a project. Allows for saving
 * variable between each runtime. Persists data after the jre exits. Useful
 * for preferences or user settings. Similar to a simple database,
 * much easier to implement and with no relationships.
 * @see Configuration
 * @see ConfigurationModel
 * @see ConfigLoader#load()
 * @see ConfigLoader#reload()
 * @see ConfigLoader#save()
 * @author srbarber1997
 */
public class ConfigLoader {

    /**
     * Boolean to mark if the config loader has been loaded
     */
    private static boolean loaded = false;

    /**
     * Configuration service used to scramble a json string
     */
    private static final Scrambler service = new Scrambler("config loader");

    /**
     * Maps a field to it's resource name. This allows the loader
     * to know which file needs to be loaded into each field
     */
    private static Map<Field, Configuration> data;

    /**
     * Maps the name of a resource to the stored instances of the object.
     * Each {@link Configuration#name()} has a corresponding object
     * Where config1.name().equals(config2.name()) you can assume
     * their objects are the same instance
     */
    private static Map<Configuration, Object> configs;

    /**
     * Set of methods that are used to configure the serializer,
     * should be filtered to get the desired type
     */
    private static Set<Method> configureMethods;

    /**
     * Directory where the config files are stored in json format
     */
    private static File directory = new File("configs/");

    private static ConfigLogger logger = new ConfigLogger();

    /**
     * Public method used to load the configs. The method
     * runs all the steps required to load or create configs.
     * Adds a shutdown hook to save all configs when the jre
     * exits. This prevents lost of data, however is not 100%
     * guaranteed {@link Runtime#addShutdownHook(Thread)}
     * @see ConfigLoader#setup()
     * @see ConfigLoader#configure()
     * @see ConfigLoader#distribute()
     */
    public static void load(boolean log) {
        if (hasLoaded())
            return;

        logger.setLog(log);
        logger.log("---------------------------------");
        logger.log(" Loading configurations...       ");
        logger.log("---------------------------------");
        setup();
        configure();
        distribute();
        logger.log("---------------------------------");
        logger.log(" Finished loading configurations ");
        logger.log("---------------------------------");

        loaded = true;
        Runtime.getRuntime().addShutdownHook(
            new Thread(ConfigLoader::save, "Config Loader Shutdown Hook")
        );
    }

    public static void load() {
        load(true);
    }

    /**
     * Public method to reload the configs. This will not rescan the
     * classpath to find new {@link Configuration} or {@link ConfigurationModel}.
     * This simply reads the configs from the
     * hard disk again and reassigns the fields with the correct instance.
     * Unsaved changes will be overwritten
     */
    public static void reload() {
        if (!hasLoaded())
            throw new RuntimeException("Cannot reload configurations before you have loaded it first");

        logger.log("-----------------------------------");
        logger.log(" Reloading configurations...       ");
        logger.log("-----------------------------------");
        configure();
        distribute();
        logger.log("-----------------------------------");
        logger.log(" Finished reloading configurations ");
        logger.log("-----------------------------------");
    }

    /**
     * Public method to save the configs to the hard disk. This should be
     * called when changes have been made. The shutdown hook should work
     * to save configs automatically but may not work occasionally
     * @see ConfigLoader#load()
     */
    public static void save() {
        configs.forEach((configuration, o) -> {
            try {
                BufferedWriter buff = new BufferedWriter(new FileWriter(
                        directory.toString() + "/" + configuration.name() + ".config"
                ));
                buff.write(applyOutboundProperties(configuration, serialize(o)));
                buff.flush();
                buff.close();
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    /**
     * --- Setup Phase ---
     * Method that sets up the loader to load in config. The method
     * scans the classpath for {@link Configuration} and {@link ConfigurationModel}
     * using {@link Reflections}. The method ensures any
     * {@link Configuration} field type matches a valid {@link ConfigurationModel}
     * and stores required resource names from {@link Configuration#name()}
     */
    private static void setup() {
        logger.log("Setting up...");
        data = new HashMap<>();

        Reflections.log = null;
        Reflections ref = new Reflections(new ConfigurationBuilder()
            .addUrls(ClasspathHelper.forPackage(""))
            .addScanners(
                new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner(),
                new MethodAnnotationsScanner(),
                new SubTypesScanner()
            )
        );

        // Gets a set of "@ConfigurationModel" annotated classes
        Set<Class<?>> models = ref.getTypesAnnotatedWith(ConfigurationModel.class);
        models.forEach(model -> {
            logger.log(" - Found configuration model: " + model.getName());
            if (model.isMemberClass() || model.isLocalClass())
                logger.log(" - (WARN) It is not advised to have an '@ConfigurationModel' in another class, this should only be used for testing");
        });

        // Gets a set of "@Configuration" annotated fields
        Set<Field> reflectSet = ref.getFieldsAnnotatedWith(Configuration.class);
        reflectSet.stream()
            .filter(field -> models.stream().anyMatch(aClass -> aClass.equals(field.getType())))
            .forEach(field -> {
                data.putIfAbsent(field, field.getAnnotation(Configuration.class));
                logger.log(" - Found configuration field: " + field.getName()
                        + " in " + field.getDeclaringClass().getSimpleName());
            });
        configureMethods = ref.getMethodsAnnotatedWith(ConfigureSerializer.class);
    }

    /**
     * --- Configure Phase ---
     * Method that configures the configs that have been found. The method
     * decides whether to load an existing config or generate a new one.
     */
    private static void configure() {
        logger.log("Configuring...");
        configs = new HashMap<>();

        if (!directory.exists())
            if (directory.mkdirs())
                logger.log(" - Generated config directory, found at: " + directory.getAbsolutePath());
            else
                logger.log(" - Unable to generate a config directory at: " + directory.getAbsolutePath());
        else
            logger.log(" - Using config directory, found at: " + directory.getAbsolutePath());

        data.forEach((field, configuration) -> {
            File resource = new File(directory.getPath() + "/" + configuration.name() + ".config");

            // Find or create resource files
            try {
                if (!resource.exists()) {
                    Files.createParentDirs(resource);
                    if (resource.createNewFile())
                        logger.log(" - Generated configuration file: " + configuration.name() + ".config");
                }
            } catch (IOException e) {
                logger.error(e);
            }

            // Read data from resources, if blank a default instance is generated
            if (!configs.containsKey(configuration)) {
                try {
                    BufferedReader buff = new BufferedReader(new FileReader(resource));
                    String json = buff.lines().reduce(String::concat).orElse(null);
                    buff.close();
                    if (json == null) {
                        String defaultResource = field.getType().getAnnotation(ConfigurationModel.class).defaultResource();
                        if (defaultResource.equals(""))
                            configs.putIfAbsent(
                                    configuration,
                                    createInstance(field.getType())
                            );
                        else
                            configs.putIfAbsent(
                                    configuration,
                                    loadDefault(defaultResource, field.getType())
                            );
                    } else
                        configs.putIfAbsent(
                                configuration,
                                loadInstance(applyInboundProperties(configuration, json), field.getType())
                        );
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        });

        configs.forEach((configuration, o) -> {
            // Run 'init' method if it exists
            try {
                configs.get(configuration).getClass()
                        .getDeclaredMethod("init")
                        .invoke(configs.get(configuration));
                logger.log(" - Initiated resource: " + o.getClass().getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(o.getClass().getSimpleName()
                        + " has an inaccessible 'init' method, try making it public");
            } catch (InvocationTargetException e) {
                e.getTargetException().printStackTrace();
                throw new RuntimeException("The 'init' method of " + o.getClass().getSimpleName() + " threw an exception:\n"
                        + e.getTargetException().getClass().getName() + ": "
                        + e.getTargetException().getMessage());
            } catch (NoSuchMethodException ignored) { }
        });
    }

    /**
     * --- Distribute Phase ---
     * Method that distributes the loaded/generated configs to their relevant
     * fields. Inaccessible fields will be made temporarily accessible to assign
     * the config instance. The initial accessibility will be returned to how it
     * was afterwards
     */
    private static void distribute() {
        logger.log("Distributing...");
        data.forEach((field, configuration) -> {
            try {
                boolean access = field.isAccessible();
                field.setAccessible(true);
                field.set(null, configs.get(configuration));
                field.setAccessible(access);
                logger.log(
                        " - Assigned resource called '" + configuration.name() + "' to field '" +
                        field.getName() + "' in '" + field.getDeclaringClass().getName()
                );
            } catch (NullPointerException e) {
                throw new RuntimeException("'@Configuration' field '" + field.getName() + "' must be static");
            } catch (IllegalAccessException e2) {
                logger.error(e2);
            }
        });
    }

    /**
     * Method that will create an instance of an object based of a class
     * @param objClass to create an object from
     * @param <T> object type to create
     * @return object based of the class given
     */
    private static <T> T createInstance(Class<T> objClass) {
        try {
            T t = objClass.newInstance();
            if (t != null)
                logger.log(" - Generated resource: " + objClass.getName());
            return t;
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create new instance of configuration model:\n\t'" +
                    objClass.getName() + "',\n\tbecause of: '" + e.getClass().getName() + "'"
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create new instance of configuration model '" +
                    objClass.getSimpleName() + "', because:\n" + e.getMessage()
            );
        }
    }

    /**
     * Method that will load an instance of an object
     * from the json string
     * @param json string representation of the object
     * @param objClass class to be created from the json string
     * @param <T> object loaded from the json string
     * @return object based of the json string
     */
    private static <T> T loadInstance(String json, Class<T> objClass) {
        T obj = null;
        try {
            obj = deserialize(json, objClass);
        } catch (Exception ignored) { }
        if (obj == null)
            switch (objClass.getAnnotation(ConfigurationModel.class).onError()) {
                case CREATE_DEFAULT_CONFIGURATION:
                    return loadDefault(objClass.getAnnotation(ConfigurationModel.class).defaultResource(), objClass);
                case THROW_EXCEPTION:
                    throw new RuntimeException("Could not load instance of '" + objClass.getName() + "', found: " + json);
            }

        logger.log(" - Loaded resource: " + objClass.getName());
        return obj;
    }

    private static <T> T loadDefault(String resource, Class<T> objClass) {
        T obj = null;
        if (!resource.equals("")) {
            try {
                InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
                if (resourceStream != null) {
                    BufferedReader buff = new BufferedReader(new InputStreamReader(resourceStream));
                    String json = buff.lines().reduce(String::concat).orElse("");
                    if (!json.equals(""))
                        obj = deserialize(json, objClass);
                }
            } catch (Exception ignored) { }
        }
        if (obj == null)
            switch (objClass.getAnnotation(ConfigurationModel.class).onError()) {
                case CREATE_DEFAULT_CONFIGURATION:
                    return createInstance(objClass);
                case THROW_EXCEPTION:
                    throw new RuntimeException("Could not load default instance of '" + objClass.getName() + "', at resource: " + resource);
            }

        logger.log(" - Loaded default resource: " + objClass.getName());
        return obj;
    }

    /**
     * Method that is run before writing the json to a file. It is used to
     * apply properties to the json string.
     * The method should be reversible by {@link ConfigLoader#applyInboundProperties(Configuration, String)}
     * @param configuration annotation with property data
     * @param json string to be written to a file
     * @return modified json string
     */
    static String applyOutboundProperties(Configuration configuration, String json) {
        if (!configuration.readable())
            json = service.scrambleCharacters(json);

        return json;
    }

    /**
     * Method that is run after reading the json from a file. It is used to remove
     * applied properties from the json string.
     * The method should be reversible by {@link ConfigLoader#applyOutboundProperties(Configuration, String)}
     * @param configuration annotation with property data
     * @param json string to be used to load an object
     * @return modified json string
     */
    static String applyInboundProperties(Configuration configuration, String json) {
        if (!configuration.readable())
            json = service.unscrambleCharacters(json);

        return json;
    }

    /**
     * Method to change the directory that the configs are stored.
     * Use null to reset to default directory location
     * @param directory to store the configs
     */
    @Beta
    public static void chooseDirectoryLocation(File directory) {
        if (loaded)
            throw new RuntimeException("Cannot set the config directory after the loader has already loaded.");

        if (directory == null) // If null, reset to default location
            directory = new File("configs/");

        ConfigLoader.directory = directory;
    }

    /**
     * Method to see if the loader has loaded or not
     * @return boolean on whether the loader has already been loaded
     */
    public static boolean hasLoaded() {
        return loaded;
    }

    /**
     * Method to configure the gson builder. Register a type adapter so
     * when the config loader is loading, the correct gson object is created.
     * It is used to build a gson object used to serialize and deserialize
     * configuration objects. Define a configure method using the annotation
     * @see ConfigureSerializer
     * @see com.google.gson.Gson
     * @see GsonBuilder
     */
    private static GsonBuilder configureSerializer(Class<?> model) {
        final GsonBuilder serializer = new GsonBuilder();
        configureMethods.stream()
                .filter(method -> method.getDeclaringClass().equals(model))
                .forEach(method -> {
                    try {
                        method.invoke(null, serializer);
                    } catch (IllegalAccessException e) {
                        logger.error(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("'@ConfigureSerializer' method through an exception:\n\t" +
                                e.getMessage()
                        );
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException(e.getClass().getName() + ": expected a method:\n" +
                                "\tpublic static void configure(com.google.gson.GsonBuilder gsonBuilder) {\n" +
                                "\t\tgsonBuilder.{someMethod}\n" +
                                "\t}"
                        );
                    } catch (NullPointerException e) {
                        throw new RuntimeException("'@ConfigureSerializer' method '" + method.getName() + "' must be static");
                    }
                });
        return serializer;
    }

    @SuppressWarnings("unchecked")
    private static  <T> T deserialize(String objString, Class<T> objClass) throws Exception {
        if (!SelfDeserializable.class.isAssignableFrom(objClass))
            return new Gson().fromJson(objString, objClass);

        SelfDeserializable obj = (SelfDeserializable) objClass.newInstance();
        return (T) obj.deserialize(objString);
    }

    private static String serialize(Object obj) throws Exception {
        if (!SelfSerializable.class.isAssignableFrom(obj.getClass()))
            return new Gson().toJson(obj);

        return ((SelfSerializable)obj).serialise(obj);
    }
}
