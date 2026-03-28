plugins {
    `multiloader-loader`
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.14"
}

stonecutter {

}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")

    implementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
    api("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}+${commonMod.mc}")
    compileOnly("maven.modrinth:tiny-item-animations:${commonMod.dep("tia")}")
    compileOnly("maven.modrinth:flow:${commonMod.dep("flow")}")

    // Required dependencies
    compileOnly("com.terraformersmc:modmenu:${commonMod.dep("modmenu")}")

}

loom {
    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
        }
        getByName("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
        }
    }
}