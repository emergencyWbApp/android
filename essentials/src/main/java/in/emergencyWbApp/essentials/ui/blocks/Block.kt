package `in`.emergencyWbApp.essentials.ui.blocks

import com.google.gson.annotations.SerializedName

sealed class Block {

    // ১. অরবিট ডিজাইন (১১২ + ৪টি বাটন)
    data class RadialOrbit(
        @SerializedName("center") val center: CenterItem,
        @SerializedName("orbits") val orbits: List<OrbitItem>
    ) : Block()

    data class CenterItem(
        @SerializedName("title") val title: String,
        @SerializedName("number") val number: String,
        @SerializedName("action") val action: String? = null,
        @SerializedName("color") val color: String? = null,
        @SerializedName("btn_icon") val btnIcon: String? = null
    )

    data class OrbitItem(
        @SerializedName("title") val title: String,
        @SerializedName("color") val color: String? = null,
        @SerializedName("action") val action: String? = null,
        @SerializedName("icon") val icon: String? = null,
        @SerializedName("btn_icon") val btnIcon: String? = null
    )

    // ২. লিস্ট সেকশন (More Helplines, First Aid)
    data class CardGridSection(
        @SerializedName("title") val title: String,
        @SerializedName("items") val items: List<GridSectionItem>
    ) : Block()

    data class GridSectionItem(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String? = null,
        @SerializedName("color") val color: String? = null,
        @SerializedName("action") val action: String? = null,
        @SerializedName("icon") val icon: String? = null,
        @SerializedName("btn_icon") val btnIcon: String? = null
    )

    // ৩. আগের ব্লকগুলো (যদি ভবিষ্যতে লাগে)
    data class HeroCall(
        @SerializedName("number") val number: String,
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String? = null,
        @SerializedName("color") val color: String? = null
    ) : Block()

    data class LocationCard(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("action") val action: String? = null,
        @SerializedName("icon_outline") val iconOutline: String? = null
    ) : Block()

    data class SectionHeader(
        @SerializedName("text") val text: String
    ) : Block()

    data class ActionGrid(
        @SerializedName("columns") val columns: Int = 2,
        @SerializedName("items") val items: List<GridItem>
    ) : Block()

    data class GridItem(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String? = null,
        @SerializedName("color") val color: String? = null,
        @SerializedName("action") val action: String? = null,
        @SerializedName("icon_outline") val iconOutline: String? = null
    )

    data class Banner(
        @SerializedName("text") val text: String,
        @SerializedName("bg_color") val bgColor: String? = null,
        @SerializedName("text_color") val textColor: String? = null,
        @SerializedName("icon_outline") val iconOutline: String? = null
    ) : Block()

    data class Unknown(val type: String) : Block()
}