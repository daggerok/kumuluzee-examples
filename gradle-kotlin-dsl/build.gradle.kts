import java.nio.charset.StandardCharsets

plugins {
  idea
  java
  id("com.github.ben-manes.versions") version "0.22.0"
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

repositories {
  mavenCentral()
}

tasks.withType(Wrapper::class.java) {
  val gradleWrapperVersion: String by project
  gradleVersion = gradleWrapperVersion
  distributionType = Wrapper.DistributionType.BIN
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
}
//tag::content[]
val kumuluzeeLoader by configurations.creating {
  extendsFrom(configurations["runtime"])
  setTransitive(false)
}
//end::content[]
val kumuluzeeVersion: String by project
val arquillianVersion: String by project
dependencyManagement {
  imports {
    mavenBom("com.kumuluz.ee:kumuluzee-bom:$kumuluzeeVersion")
    mavenBom("org.jboss.arquillian:arquillian-bom:$arquillianVersion")
  }
}
//tag::content[]
dependencies {
  //end::content[]
  val kumuluzeeLogsVersion: String by project
  val kumuluzeeRestVersion: String by project
  val kumuluzeeCorsVersion: String by project
  val junitVersion: String by project
  val kumuluzeeArquillianContainerVersion: String by project
  //tag::content[]
  kumuluzeeLoader("com.kumuluz.ee:kumuluzee-loader:$kumuluzeeVersion")
  //end::content[]

  implementation("com.kumuluz.ee:kumuluzee-microProfile-2.1")
  implementation("com.kumuluz.ee.logs:kumuluzee-logs-jul:$kumuluzeeLogsVersion")
  implementation("com.kumuluz.ee.rest:kumuluzee-rest-core:$kumuluzeeRestVersion")
  implementation("com.kumuluz.ee.cors:kumuluzee-cors:$kumuluzeeCorsVersion")

  testImplementation("com.kumuluz.ee.testing:kumuluzee-arquillian-container:$kumuluzeeArquillianContainerVersion")
  testImplementation("org.jboss.arquillian.junit:arquillian-junit-container")
  testImplementation("junit:junit:$junitVersion")
  //tag::content[]
  // ...
}

tasks {
  //end::content[]
  test {
    useJUnitPlatform()
  }
  //tag::content[]
  register("kumuluzLoader", Copy::class.java) {
    group = "KumuluzEE"
    description = "Add KumuluzEE boot loader"

    from(zipTree(configurations["kumuluzeeLoader"].singleFile).matching {
      include("**/*.class")
      include("**/*.properties")
    })
    into("$buildDir/classes/java/main")
    outputs.upToDateWhen { false }

    doLast {
      val filename = "$buildDir/classes/java/main/META-INF/kumuluzee"
      file(filename).mkdirs()
      file("$filename/boot-loader.properties")
          .writeText("main-class=com.kumuluz.ee.EeApplication\n", StandardCharsets.UTF_8)
    }
  }

  register("kumuluzJar", Jar::class.java) {
    group = "KumuluzEE"
    description = "Build KumuluzEE uber (fat) jar"

    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
      attributes("Main-Class" to "com.kumuluz.ee.loader.EeBootLoader")
    }

    // deps
    val libs = configurations.runtimeClasspath
    libs.get().forEach { println("add dependency: ${it.name}") }
    from(libs.get()) {
      into("lib")
    }

    // app
    val sourcesMain = sourceSets.main
    sourcesMain.get().allSource.forEach { println("add app: ${it.name}") }
    from(sourcesMain.get().output)

    shouldRunAfter("kumuluzLoader")
    dependsOn("kumuluzLoader")
  }
}

defaultTasks("kumuluzJar")
//end::content[]
