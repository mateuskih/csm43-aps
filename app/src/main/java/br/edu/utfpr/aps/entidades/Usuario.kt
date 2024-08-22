package br.edu.utfpr.aps.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "usuarios")
data class Usuario(
    var nome: String,
    var email: String,
    var senha: String,
    var pontuacao: Int,
    var partidasJogadas: Int,
    var ultimaPartida: Date?,
    var admin: Boolean = false,
    var cidade: String,
    var foto: ByteArray
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}