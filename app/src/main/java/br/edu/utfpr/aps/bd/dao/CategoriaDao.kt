package br.edu.utfpr.aps.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Usuario
import java.util.*


@Dao
interface CategoriaDao {
    @Insert
    fun inserir(categoria: Categoria)

    @Query("SELECT * FROM categorias")
    fun buscarCategorias(): List<Categoria>

    @Query("SELECT * FROM categorias WHERE id = :id LIMIT 1")
    fun buscarCategoria(id: Int): Categoria

    @Query("SELECT * FROM categorias WHERE name = :nome LIMIT 1")
    fun buscarCategoriaByName(nome: String): Categoria

    @Query("UPDATE categorias SET name = :nome WHERE  id = :id")
    fun alterarCategoria(id: Int, nome: String): Int

    @Delete
    fun apagar(categoria: Categoria): Int
}