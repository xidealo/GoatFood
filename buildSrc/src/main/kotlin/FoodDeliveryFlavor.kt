enum class FoodDeliveryFlavor(
    val key: String,
    val applicationId: String,
) {
    PAPA_KARLO(
        key = "papakarlo",
        applicationId = "com.bunbeuaty.papakarlo"
    ),
    YULIAR(
        key = "yuliar",
        applicationId = "com.bunbeuaty.yuliar"
    ),
    DJAN(
        key = "djan",
        applicationId = "com.bunbeauty.djan"
    ),
    GUSTO_PUB(
        key = "gustopub",
        applicationId = "com.bunbeauty.gustopub"
    ),
    TANDIR_HOUSE(
        key = "tandirhouse",
        applicationId = "com.bunbeauty.tandirhouse"
    ),
    VKUS_KAVKAZA(
        key = "vkuskavkaza",
        applicationId = "com.bunbeauty.vkuskavkaza"
    ),
    EST_POEST(
        key = "estpoest",
        applicationId = "com.bunbeauty.estpoest"
    ),
    LEGENDA(
        key = "legenda",
        applicationId = "com.bunbeauty.legenda"
    ),
    USADBA(
        key = "usadba",
        applicationId = "com.bunbeauty.usadba"
    ),
    EMOJI(
        key = "emoji",
        applicationId = "com.bunbeauty.emoji"
    ),
    VOSTOCHKA(
        key = "vostochka",
        applicationId = "com.bunbeauty.vostochka"
    ),
    TAVERNA(
        key = "taverna",
        applicationId = "com.bunbeauty.taverna"
    ),
    VOLJANE(
        key = "voljane",
        applicationId = "com.bunbeauty.voljane"
    ),
    BEREG(
        key = "bereg",
        applicationId = "com.bunbeauty.bereg"
    ),
    MIMINO(
        key = "mimino",
        applicationId = "com.bunbeauty.mimino"
    ),
    EMOJI_CENTER(
        key = "emojicenter",
        applicationId = "com.bunbeauty.emojicenter"
    );

    val assembleReleaseBundle: String
        get() {
            return "bundle${key.replaceFirstChar { oldChar -> oldChar.uppercase() }}Release"
        }

    val publishReleaseBundle: String
        get() {
            return "publish${key.replaceFirstChar { oldChar -> oldChar.uppercase() }}ReleaseBundle"
        }

    val bootstrapReleaseBundle: String
        get() {
            return "bootstrap${key.replaceFirstChar { oldChar -> oldChar.uppercase() }}ReleaseBundle"
        }
}