package com.ziemapp.johnzieman.mystorybook.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "story")
data class Story(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var date: Date = Date(),
    var title: String = "",
    var description: String = ""
    ){
    val photoFileName
        get() = "IMG_$id.jpg"
}