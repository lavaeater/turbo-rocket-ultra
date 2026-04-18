package spritesheet

data class LpcRecolor(
    val material: String,
    val palettes: List<String>
)

data class LpcCredit(
    val file: String,
    val authors: List<String>,
    val licenses: List<String>,
    val urls: List<String>,
    val notes: String = ""
)

/**
 * Parsed representation of a single `sheet_definitions/**/*.json` file.
 *
 * [variants] maps gender/body-type keys ("male", "female", "muscular", …) to the
 * sprite folder path relative to `lpc/spritesheets/`.
 *
 * [categoryPath] is the directory hierarchy derived from the JSON file's path inside
 * `sheet_definitions/`, e.g. `["head", "eyebrows"]` for
 * `sheet_definitions/head/eyebrows/eyebrows_thick.json`.
 */
data class LpcSheetDef(
    val name: String,
    val priority: Int,
    val variants: Map<String, String>,
    val animations: List<String>,
    val recolors: List<LpcRecolor>,
    val credits: List<LpcCredit>,
    val categoryPath: List<String>
)
