package org.bitbucket.srbarber1997.configuration;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import com.google.gson.Gson;
import org.bitbucket.srbarber1997.configuration.ConfigLoader;
import org.bitbucket.srbarber1997.configuration.Configuration;
import org.bitbucket.srbarber1997.configuration.models.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.*;

public class ConfigLoaderTest {
    private static File directory = new File("configs/");

    // Test private static
    @Configuration(name = "test1")
    private static TestConfig config1;

    // Test package-private static
    @Configuration(name = "test2")
    static TestConfig config2;

    // Test public static
    @Configuration(name = "test2")
    public static TestConfig config3;

    @Configuration(name = "init")
    public static TestConfigWithInitMethod configWithInit;

    @Configuration(name = "testWithDefault")
    public static TestConfigWithDefaultResource configWithDefault;

    @BeforeClass
    public static void beforeAll() throws IOException {
        if (directory.exists() && directory.isDirectory())
            MoreFiles.deleteRecursively(directory.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);

        ConfigLoader.load();
    }

    @Before
    public void before() {
        config1 = null;
        config2 = null;
        config3 = null;
    }

    @Test
    public void testLoadsConfigurationsFromModelToFields() {
        assertNull(config1);
        assertNull(config2);
        assertNull(config3);

        ConfigLoader.reload();

        assertNotNull(config1);
        assertNotNull(config2);
        assertNotNull(config3);
    }

    @Test
    public void testLoadedConfigsAreSingletons() {
        ConfigLoader.reload();

        assertNotSame(config1, config2);
        assertNotSame(config1, config3);
        assertSame(config2, config3);
    }

    @Configuration(name = "apply properties", readable = false)
    private static TestConfig propertyTestConfig;

    @Test
    public void testApplyProperties() throws NoSuchFieldException {
        ConfigLoader.reload();
        Configuration configuration = getClass().getDeclaredField("propertyTestConfig")
                .getAnnotation(Configuration.class);

        String json = new Gson().toJson(propertyTestConfig);
        String outbound = ConfigLoader.applyOutboundProperties(configuration, json);
        String inbound = ConfigLoader.applyInboundProperties(configuration, outbound);

        assertNotSame(json, inbound);
        assertEquals(json, inbound);
    }

    @Test
    public void testSaving() {
        ConfigLoader.reload();

        assertEquals(0, config1.getNum());

        config1.setNum(5);

        ConfigLoader.save();
        ConfigLoader.reload();

        assertEquals(5, config1.getNum());
    }

    @Test
    public void testInitMethodCall() {
        configWithInit.setNumber(9);
        ConfigLoader.save();
        ConfigLoader.reload();
        assertEquals(10, configWithInit.getNumber());

        TestConfigWithInitMethod.setThrowable(new Throwable());

        boolean thrown = false;
        try {
            ConfigLoader.reload();
        } catch (RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown);

        TestConfigWithInitMethod.setThrowable(null);
    }

    @Test
    public void testDefaultResource() {
        ConfigLoader.reload();

        assertEquals(1, configWithDefault.num);
    }

    @Configuration(name = "myDir/config1")
    private static TestConfig dirConfig1;

    @Configuration(name = "myDir/config2")
    private static TestConfig dirConfig2;

    @Configuration(name = "myDir/config3")
    private static TestConfig dirConfig3;

    @Configuration(name = "dir1/dir2/")
    private static TestConfig dirWithDirConfig;

    @Test
    public void testDirectoryConfigs() {
        ConfigLoader.reload();

        File directoryWithConfigs = new File(directory.getAbsolutePath() + "/myDir");
        assertTrue(directoryWithConfigs.isDirectory());
        assertEquals(3, Objects.requireNonNull(directoryWithConfigs.listFiles()).length);

        File slashAtEnd = new File(directory.getAbsolutePath() + "/dir1/dir2/.config");
        assertTrue(slashAtEnd.exists());
    }

    @Configuration(name = "serializer test")
    private static SerialiserConfig serialiserConfig;

    @Test
    public void testSerializerIsCalled() {
        ConfigLoader.save();
        assertTrue(serialiserConfig.called);

        serialiserConfig.called = false;
        ConfigLoader.reload();
        assertTrue(serialiserConfig.called);
    }

    @Configuration(name = "gson configure test")
    private static GsonSerialiserConfig gsonSerialiserConfig;

    @Test
    public void testGsonSerializerIsCalled() {
        ConfigLoader.save();
        assertTrue(gsonSerialiserConfig.called);

        gsonSerialiserConfig.called = false;
        ConfigLoader.reload();
        assertTrue(gsonSerialiserConfig.called);
    }

    @Test
    public void testGsonSerializerCreatesActualObject() {
        ConfigLoader.reload();

        assertEquals(GsonSerialiserConfig.class, gsonSerialiserConfig.getClass());
    }
}
