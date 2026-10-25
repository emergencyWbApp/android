package `in`.emergencyWbApp.essentials.ui.blocks

import com.google.gson.Gson
import com.google.gson.JsonElement

object BlockParser {
    private val gson = Gson()

    fun parseAll(elements: List<JsonElement>): List<Block> {
        return elements.mapNotNull { parseSingle(it) }
    }

    fun parseJsonString(jsonString: String?): List<Block> {
        if (jsonString.isNullOrBlank()) return emptyList()
        return try {
            val array = gson.fromJson(jsonString, com.google.gson.JsonArray::class.java)
            parseAll(array.toList())
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseSingle(el: JsonElement): Block? = try {
        val obj = el.asJsonObject
        when (obj.get("type")?.asString) {
            "radial_orbit"      -> gson.fromJson(obj, Block.RadialOrbit::class.java)
            "card_grid_section" -> gson.fromJson(obj, Block.CardGridSection::class.java)
            "hero_call"         -> gson.fromJson(obj, Block.HeroCall::class.java)
            "location_card"     -> gson.fromJson(obj, Block.LocationCard::class.java)
            "section_header"    -> gson.fromJson(obj, Block.SectionHeader::class.java)
            "action_grid"       -> gson.fromJson(obj, Block.ActionGrid::class.java)
            "banner"            -> gson.fromJson(obj, Block.Banner::class.java)
            else                -> Block.Unknown(obj.get("type")?.asString ?: "unknown")
        }
    } catch (e: Exception) {
        null
    }
}