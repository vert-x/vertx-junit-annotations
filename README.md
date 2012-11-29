# vert.x JUnit Annotations

A JUnit Test Runner for vert.x and some utility annotations for launching integration tests.

## Setup

### Maven coordinates

The vertx-junit-annotations JAR is in Maven Central at the following coordinates (in Gradle form).

    org.vert-x:vertx-junit-annotations:1.3.0.final

## Configuration

### Starting the vert.x node

Once you've added the JAR to your integration test classpath, you should annotate each test class with the @RunWith annotation, with the vert.x JUnit Class Runner implementation: VertxJUnit4ClassRunner.class.

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    public class SimpleVerticleTest {
      ...
    }

To start a clustered vert.x node, set the port and hostname attributes of the @VertxConfiguration annotation. (The default value for 'hostname' is 'localhost'.)

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration(hostname="vert.x.org", port=10000)
    public class SimpleVerticleTest {
      ...
    }

### Test class implementations

Implement VertxAware in your test class and VertxJUnit4ClassRunner will provide the same Vertx instance that is being used to start verticles and modules found in annotations:

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    public class SimpleVerticleTest implements VertxAware {

      private Vertx vertx;

      @Override
      public void setVertx(Vertx vertx) {
        this.vertx = vertx;
      }          
    }

Implement VertxManagerAware to get the current VerticleManager:

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    public class SimpleVerticleTest implements VertxManagerAware  {

      private VerticleManager manager;

      @Override
      public void setVerticleManager(VerticleManager manager) {
        this.manager = manager;
      }
    }

Finally, extend VertxTestBase which implements both interfaces described above and some other utility methods:

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    public class SimpleVerticleTest extends VertxTestBase  {
      ...
    }

## Launching

### Testing a verticles

You can annotate a class:

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    @TestVerticle(main="test-verticle.js")
    public class SimpleVerticleTest {
      ...
    }

or you can annotate a method:

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    public class SimpleVerticleTest {

      @Test
      @TestVerticle(main="test-verticle.js")
      public void doSomeTests() {}
    }

If you annotate a class, the verticle starts before any other method is executed and is automatically stopped when the all of the tests in the class have finished.

A verticle started through an method annotation starts before the method is executed and is automatically stopped afterwards.

### Testing a Module

Just like the @TestVerticle annotation, @TestModule can be placed on the class:

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    @TestModule(name="vertx.example-v1.0")
    public class SimpleVerticleTest {
      ...
    }

and it can also annotate a method:

    @RunWith(VertxJUnit4ClassRunner.class)
    @VertxConfiguration
    public class SimpleVerticleTest {

      @Test
      @TestModule(name="vertx.example-v1.0")
      public void doSomeTests() {}
    }

## Usage

See the projects own unit tests for many more examples!
