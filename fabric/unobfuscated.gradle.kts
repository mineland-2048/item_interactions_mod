plugins {
    `multiloader-loader`
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
//    kotlin("jvm") version "2.2.0"
//    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
//    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

stonecutter {
    replacements.string(current.parsed >= "26.1", "!gui_graphics_extractor") {
        replace("GuiGraphics", "GuiGraphicsExtractor")
    }

    replacements.string(current.parsed >= "26.1", "gui_methods") {
        replace(".drawString", ".text")
        replace(".renderOutline", ".outline")
        replace(".drawCenteredString", ".centeredText")
    }
    replacements.string(current.parsed >= "26.1", "render_extract") {
        replace("render", "extract")
    }
    replacements.string(current.parsed >= "26.1", "render_add") {
        replace("render", "add")
    }

    val loader = property("loader").toString()
    constants.match(
        loader,
        "fabric",   // != "loader" -> false
        "neoforge", // == "loader" -> true
        "forge",    // != "loader" -> false
    )
}

//fletchingTable {
//    mixins.create("main") {
//        mixin("fabric", "fabricOnly.mixins.json")
//    }
//}
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