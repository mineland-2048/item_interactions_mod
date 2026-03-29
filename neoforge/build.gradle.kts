plugins {
    `multiloader-loader`
    id("net.neoforged.moddev") version "2.0.141"
//    kotlin("jvm") version "2.2.0"
//    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
//    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22"
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

neoForge {
    enable {
        version = commonMod.dep("neoforge")
    }
}

dependencies {
    compileOnly("maven.modrinth:tiny-item-animations:${commonMod.dep("tia")}")
    compileOnly("maven.modrinth:flow:${commonMod.dep("flow")}")
}

neoForge {
    runs {
        register("client") {
            client()
            ideName = "NeoForge Client (${project.path})"
        }
        register("server") {
            server()
            ideName = "NeoForge Server (${project.path})"
        }
    }

    parchment {
        commonMod.depOrNull("parchment")?.let {
            mappingsVersion = it
            minecraftVersion = commonMod.mc
        }
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}