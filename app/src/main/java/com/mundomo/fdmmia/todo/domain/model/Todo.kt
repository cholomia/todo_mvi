package com.mundomo.fdmmia.todo.domain.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Todo(

    @field:[SerializedName("id") PrimaryKey] var id: Long = 0L,
    @SerializedName("userId") var userId: Long = 0L,
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null
) : RealmObject()