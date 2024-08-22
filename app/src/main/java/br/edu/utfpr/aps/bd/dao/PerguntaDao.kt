package br.edu.utfpr.aps.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.utfpr.aps.entidades.Question


@Dao
interface PerguntaDao {
    @Insert
    fun inserir(pergunta: Question): Long

    @Query("SELECT * FROM perguntas")
    fun buscaTodas(): List<Question>

    @Query("SELECT * FROM perguntas WHERE id = :id LIMIT 1")
    fun buscaPergunta(id: Int): Question

    @Query("SELECT * FROM perguntas WHERE category = :category")
    fun buscaPerguntaPorCategoria(category: String): Question

    @Query("UPDATE perguntas SET category = :category, type = :type, difficulty = :difficulty, question = :question, correctAnswer = :correctAnswer, incorrectAnswers = :incorrectAnswers WHERE id = :id")
    fun alterarPergunta(id: Int, category: String, type: String, difficulty: String, question: String, correctAnswer: String, incorrectAnswers: List<String>): Int

    @Delete
    fun apagar(pergunta: Question)
}