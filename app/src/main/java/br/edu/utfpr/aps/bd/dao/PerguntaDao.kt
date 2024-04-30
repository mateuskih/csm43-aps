package br.edu.utfpr.aps.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.utfpr.aps.entidades.Question


@Dao
interface PerguntaDao {
    @Query("SELECT * FROM perguntas")
    fun buscaTodas(): List<Question>

    @Query("SELECT * FROM perguntas WHERE id = :id LIMIT 1")
    fun buscaPergunta(id: Int): Question

    @Insert
    fun inserir(pergunta: Question)

    @Delete
    fun apagar(pergunta: Question)


}