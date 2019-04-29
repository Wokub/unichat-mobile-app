package com.wk.unichat.Channels

// Class containing channel info with method that allows us to convert channel name into string
class Channel(val name: String, val info: String, val id: String) { override fun toString(): String { return "$name" } }