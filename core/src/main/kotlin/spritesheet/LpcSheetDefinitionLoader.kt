package spritesheet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader

/**
 * Reads every non-`meta_*.json` file under [sheetDefinitionsPath] and converts them into
 * a tree of [LpcSpriteSheetCategoryDefinition]s whose leaf [LpcSpriteSheetDefinition]
 * entries point at per-animation PNG files under [spriteSheetsPath].
 *
 * The category hierarchy mirrors the directory structure of `sheet_definitions/`.
 * Only the two top levels (e.g. "head" → "eyebrows") are surfaced; deeper nesting is
 * flattened into the nearest ancestor category.
 *
 * Both paths are resolved via [Gdx.files.local], so they are relative to the game's
 * working directory (`assets/` when running via `./gradlew lwjgl3:run`).
 *
 * Default paths assume the `lpc` Git submodule sits one level above `assets/`:
 *   `../lpc/sheet_definitions/`  and  `../lpc/spritesheets/`
 */
object LpcSheetDefinitionLoader {

    private val KNOWN_VARIANTS = setOf("male", "female", "muscular", "teen", "pregnant", "child")

    fun load(
        sheetDefinitionsPath: String = "../lpc/sheet_definitions",
        spriteSheetsPath: String = "../lpc/spritesheets"
    ): List<LpcSpriteSheetCategoryDefinition> {

        val defs = loadAllDefs(sheetDefinitionsPath)
        return buildCategories(defs, spriteSheetsPath)
    }

    // -------------------------------------------------------------------------
    // JSON loading
    // -------------------------------------------------------------------------

    private fun loadAllDefs(basePath: String): List<LpcSheetDef> {
        val result = mutableListOf<LpcSheetDef>()
        loadRecursive(basePath, basePath, result)
        return result
    }

    private fun loadRecursive(
        basePath: String,
        currentPath: String,
        result: MutableList<LpcSheetDef>
    ) {
        val handle = Gdx.files.local(currentPath)
        if (!handle.exists()) return

        for (child in handle.list()) {
            when {
                child.isDirectory -> loadRecursive(basePath, child.path(), result)
                child.extension() == "json" && !child.name().startsWith("meta_") -> {
                    parseDef(basePath, child.path())?.let { result += it }
                }
            }
        }
    }

    private fun parseDef(basePath: String, filePath: String): LpcSheetDef? {
        return try {
            val text = Gdx.files.local(filePath).readString("UTF-8")
            val root = JsonReader().parse(text) ?: return null

            val name = root.getString("name", null) ?: return null

            // priority: sheet-level field, or fall back to 50
            val priority = root.getInt("priority", 50)

            // Collect variant paths from layer_1 (and optionally layer_2)
            val variants = mutableMapOf<String, String>()
            root.get("layer_1")?.let { layer ->
                for (variantKey in KNOWN_VARIANTS) {
                    layer.getString(variantKey, null)?.let { variants[variantKey] = it }
                }
            }
            // If no known variants found in layer_1, skip this sheet
            if (variants.isEmpty()) return null

            // Animations list
            val animations = mutableListOf<String>()
            root.get("animations")?.forEach { anim -> animations += anim.asString() }

            // Recolors — the JSON field can be an object or an array
            val recolors = mutableListOf<LpcRecolor>()
            root.get("recolors")?.let { rc ->
                when {
                    rc.isObject -> parseRecolor(rc)?.let { recolors += it }
                    rc.isArray -> rc.forEach { item -> parseRecolor(item)?.let { recolors += it } }
                    else -> Unit
                }
            }

            // Category path from the relative file path inside sheet_definitions/
            // e.g. basePath="path/sheet_definitions", filePath="path/sheet_definitions/head/eyebrows/thick.json"
            // → categoryPath = ["head", "eyebrows"]
            val relative = filePath.removePrefix(basePath).trimStart('/')
            val parts = relative.split("/").dropLast(1) // remove the filename
            val categoryPath = parts.filter { it.isNotEmpty() }

            // Credits array
            val credits = mutableListOf<LpcCredit>()
            root.get("credits")?.forEach { c ->
                val authors = mutableListOf<String>()
                c.get("authors")?.forEach { authors += it.asString() }
                val licenses = mutableListOf<String>()
                c.get("licenses")?.forEach { licenses += it.asString() }
                val urls = mutableListOf<String>()
                c.get("urls")?.forEach { urls += it.asString() }
                credits += LpcCredit(
                    file = c.getString("file", ""),
                    authors = authors,
                    licenses = licenses,
                    urls = urls,
                    notes = c.getString("notes", "")
                )
            }

            LpcSheetDef(name, priority, variants, animations, recolors, credits, categoryPath)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseRecolor(node: com.badlogic.gdx.utils.JsonValue): LpcRecolor? {
        val material = node.getString("material", null) ?: return null
        val palettes = mutableListOf<String>()
        node.get("palettes")?.forEach { p -> palettes += p.asString() }
        return LpcRecolor(material, palettes)
    }

    // -------------------------------------------------------------------------
    // Category tree construction
    // -------------------------------------------------------------------------

    private fun buildCategories(
        defs: List<LpcSheetDef>,
        spriteSheetsPath: String
    ): List<LpcSpriteSheetCategoryDefinition> {

        // Group by top-level category (first element of categoryPath)
        val byTop = defs.groupBy { it.categoryPath.firstOrNull() ?: "other" }

        val topCategories = mutableListOf<LpcSpriteSheetCategoryDefinition>()

        for ((topName, topDefs) in byTop.entries.sortedBy { it.key }) {
            // Group by second-level (sub-category), fall back to same name as top
            val bySub = topDefs.groupBy { it.categoryPath.getOrNull(1) }

            val subCategories = mutableListOf<LpcSpriteSheetCategoryDefinition>()

            for ((subName, subDefs) in bySub.entries.sortedBy { it.key }) {
                if (subName == null) {
                    // These defs belong directly to the top-level category — handled below
                    continue
                }
                val subCat = LpcSpriteSheetCategoryDefinition(
                    name = "$topName/$subName",
                    tags = emptySet(),
                    renderPriority = subDefs.minOf { it.priority }
                )
                subDefs.forEach { def ->
                    addSpriteSheets(subCat, def, spriteSheetsPath)
                }
                subCategories += subCat
            }

            // Defs directly in the top directory (no sub-directory)
            val topDirectDefs = bySub[null] ?: emptyList()
            val topPriority = (topDefs + topDirectDefs).minOfOrNull { it.priority } ?: 50
            val topCat = LpcSpriteSheetCategoryDefinition(
                name = topName,
                tags = emptySet(),
                renderPriority = topPriority,
                dependentCategories = subCategories
            )
            topDirectDefs.forEach { def ->
                addSpriteSheets(topCat, def, spriteSheetsPath)
            }
            topCategories += topCat
        }

        return topCategories
    }

    /**
     * For each variant in [def], creates an [LpcSpriteSheetDefinition] pointing at the
     * preview PNG (walk or first available animation) and records the variant folder so
     * [RenderableThing] can load any per-animation PNG at export time.
     */
    private fun addSpriteSheets(
        category: LpcSpriteSheetCategoryDefinition,
        def: LpcSheetDef,
        spriteSheetsPath: String
    ) {
        val previewAnim = when {
            "walk" in def.animations -> "walk"
            def.animations.isNotEmpty() -> def.animations.first()
            else -> "walk"
        }

        for ((variantKey, variantRelPath) in def.variants) {
            val folderPath = "$spriteSheetsPath/${variantRelPath.trimEnd('/')}"
            val pngPath = "$folderPath/$previewAnim.png"
            val isMale = variantKey != "female"
            val isFemale = variantKey == "female"
            category.spriteSheets += LpcSpriteSheetDefinition(
                path = pngPath,
                displayName = def.name,
                explicitIsMale = isMale,
                explicitIsFemale = isFemale,
                variantFolder = folderPath,
                variantKey = variantKey,
                credits = def.credits
            )
        }
    }
}
