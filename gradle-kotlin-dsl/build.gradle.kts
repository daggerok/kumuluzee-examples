plugins {
  idea
  java
  id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

repositories {
  mavenCentral()
}

tasks.withType(Wrapper::class.java) {
  distributionType = Wrapper.DistributionType.BIN
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
}

val kumuluzeeLoader by configurations.creating {
  extendsFrom(configurations["runtime"])
  setTransitive(false)
}

val kumuluzeeVersion: String by project
val arquillianVersion: String by project
dependencyManagement {
  imports {
    mavenBom("com.kumuluz.ee:kumuluzee-bom:$kumuluzeeVersion")
    mavenBom("org.jboss.arquillian:arquillian-bom:$arquillianVersion")
  }
}

dependencies {
  val kumuluzeeLogsVersion: String by project
  val kumuluzeeRestVersion: String by project
  val kumuluzeeCorsVersion: String by project
  val junitVersion: String by project
  val kumuluzeeArquillianContainerVersion: String by project

  kumuluzeeLoader("com.kumuluz.ee:kumuluzee-loader:$kumuluzeeVersion")

  implementation("com.kumuluz.ee:kumuluzee-microProfile-2.1")
  implementation("com.kumuluz.ee.logs:kumuluzee-logs-jul:$kumuluzeeLogsVersion")
  implementation("com.kumuluz.ee.rest:kumuluzee-rest-core:$kumuluzeeRestVersion")
  implementation("com.kumuluz.ee.cors:kumuluzee-cors:$kumuluzeeCorsVersion")

  testImplementation("com.kumuluz.ee.testing:kumuluzee-arquillian-container:$kumuluzeeArquillianContainerVersion")
  testImplementation("org.jboss.arquillian.junit:arquillian-junit-container")
  testImplementation("junit:junit:$junitVersion")
}

tasks {
  test {
    useJUnitPlatform()
  }

  register("kumuluzLoader", Copy::class.java) {
    group = "KumuluzEE"
    description = "Repackage KumuluzEE loader"
    from(zipTree(configurations["kumuluzeeLoader"].singleFile).matching { include("**/*.class") })
    into("$buildDir/classes/java/main")
    outputs.upToDateWhen { false }
  }

  register("kumuluzProperties") {
    group = "KumuluzEE"
    description = "Add KumuluzEE boot-loader.properties file"
    file("$buildDir/classes/java/main/META-INF/kumuluzee").mkdirs()
    file("$buildDir/classes/java/main/META-INF/kumuluzee/boot-loader.properties")
        .writeText("main-class=com.kumuluz.ee.EeApplication")
  }

  register("kumuluzJar", Jar::class.java) {
    group = "KumuluzEE"
    description = "Build KumuluzEE uber (fat) jar"
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
      attributes("Main-Class" to "com.kumuluz.ee.loader.EeBootLoader")
    }
    into("lib") {
      from(configurations.runtimeClasspath.get()
          .onEach { println("add dependency: ${it.name}") })
    }
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add app: ${it.name}") }
    from(sourcesMain.output)
    shouldRunAfter("kumuluzLoader", "kumuluzProperties")
    dependsOn("kumuluzLoader", "kumuluzProperties")
    finalizedBy("build")
  }
}

defaultTasks("kumuluzJar")
