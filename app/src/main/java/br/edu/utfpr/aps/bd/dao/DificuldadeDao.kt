package br.edu.utfpr.aps.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Dificuldade
import br.edu.utfpr.aps.entidades.Usuario
import java.util.*


@Dao
interface DificuldadeDao {
    @Insert
    fun inserir(dificuldade: Dificuldade)

    @Query("SELECT * FROM dificuldades")
    fun buscarDificuldades(): List<Dificuldade>

    @Query("SELECT * FROM dificuldades WHERE name = :nome LIMIT 1")
    fun buscarDificuldade(nome: String): Dificuldade

    @Query("UPDATE dificuldades SET name = :nome WHERE id = :id")
    fun alterarDificuldade(id: Int, nome: String): Int

    @Delete
    fun apagar(dificuldade: Dificuldade): Int
}