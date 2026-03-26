allprojects {
    group = providers.gradleProperty("POM_GROUP_ID").getOrElse("com.itswin11")
    version = providers.gradleProperty("VERSION_NAME").getOrElse("0.1.0-SNAPSHOT")
}

