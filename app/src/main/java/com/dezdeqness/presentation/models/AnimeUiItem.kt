package com.dezdeqness.presentation.models

data class AnimeUiItem(
    val briefInfo: String,
    val kind: String,
    val logoUrl: String,
) : AdapterItem()
