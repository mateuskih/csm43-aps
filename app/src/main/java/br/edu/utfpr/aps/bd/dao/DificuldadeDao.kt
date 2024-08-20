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
    fun buscarCategorias(): List<Dificuldade>

    @Query("SELECT * FROM dificuldades WHERE id = :id LIMIT 1")
    fun buscarCategoria(id: Int): Dificuldade

    @Delete
    fun apagar(dificuldade: Dificuldade)
}