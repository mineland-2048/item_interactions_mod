plugins {
    `multiloader-loader`
    id("fabric-loom") version "1.14-SNAPSHOT"
//    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
//    kotlin("jvm") version "2.2.0"
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

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")

    mappings(loom.layered {
        officialMojangMappings()
        commonMod.depOrNull("parchment")?.let { parchmentVersion ->
            parchment("org.parchmentmc.data:parchment-${commonMod.mc}:$parchmentVersion@zip")
        }
    })

    modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}+${commonMod.mc}")
    modCompileOnly("maven.modrinth:tiny-item-animations:${commonMod.dep("tia")}")
    modCompileOnly("maven.modrinth:flow:${commonMod.dep("flow")}")

    // Required dependencies
    modCompileOnly("com.terraformersmc:modmenu:${commonMod.dep("modmenu")}")

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