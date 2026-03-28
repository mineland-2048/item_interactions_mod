plugins {
    id("multiloader-common")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.14"
}

stonecutter {

}

fletchingTable {
    j52j.register("main") {
        extension("json", "**/*.json5")
    }
}

loom {
    mixin {
        useLegacyMixinAp = false
    }
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = commonMod.mc)

    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
    compileOnly("maven.modrinth:tiny-item-animations:${commonMod.dep("tia")}")
    compileOnly("maven.modrinth:flow:${commonMod.dep("flow")}")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    afterEvaluate {
        val mainSourceSet = sourceSets.main.get()
        mainSourceSet.java.sourceDirectories.files.forEach {
            add(commonJava.name, it)
        }
        mainSourceSet.resources.sourceDirectories.files.forEach {
            add(commonResources.name, it)
        }
    }
}