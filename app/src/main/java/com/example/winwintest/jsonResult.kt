package com.example.winwintest

data class jsonResult(
    val code: Int,
    val `data`: Data,
    val message: String
)

data class Data(
    val items: List<Item>,
    val totalCount: Int
)

data class Item(
    val tags: List<String>,
    val user: User
)

data class User(
    val imageUrl: String,
    val nickName: String
)