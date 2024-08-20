package br.edu.utfpr.aps.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dificuldades")
data class Dificuldade (
    var name: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}