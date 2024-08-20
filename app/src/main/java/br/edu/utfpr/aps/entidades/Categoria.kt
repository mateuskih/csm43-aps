package br.edu.utfpr.aps.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria (
    var name: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
