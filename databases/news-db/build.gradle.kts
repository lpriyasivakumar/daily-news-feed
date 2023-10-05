plugins {
    id("org.flywaydb.flyway") version "8.5.7"
}

repositories {
    mavenCentral()
}

val flywayMigration by configurations.creating

dependencies {
    flywayMigration("org.postgresql:postgresql:42.3.3")
}

flyway {
    configurations = arrayOf("flywayMigration")
}

tasks.register<org.flywaydb.gradle.task.FlywayMigrateTask>("devMigrate") {
    url = "jdbc:postgresql://localhost:5555/news_dev?user=news-reader&password=news-reader"
}

tasks.register<org.flywaydb.gradle.task.FlywayCleanTask>("devClean") {
    url = "jdbc:postgresql://localhost:5555/news_dev?user=news-reader&password=news-reader"
}

tasks.register<org.flywaydb.gradle.task.FlywayMigrateTask>("testMigrate") {
    url = "jdbc:postgresql://localhost:5555/news_test?user=news-reader&password=news-reader"
}

tasks.register<org.flywaydb.gradle.task.FlywayCleanTask>("testClean") {
    url = "jdbc:postgresql://localhost:5555/news_test?user=news-reader&password=news-reader"
}
