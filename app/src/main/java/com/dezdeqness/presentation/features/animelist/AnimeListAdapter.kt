package com.dezdeqness.presentation.features.animelist

import com.dezdeqness.core.DelegateAdapter
import com.dezdeqness.presentation.models.AdapterItem

class AnimeListAdapter : DelegateAdapter<AdapterItem>(listOf(animeAdapterDelegate()))