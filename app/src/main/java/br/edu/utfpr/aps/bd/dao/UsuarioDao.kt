package br.edu.utfpr.aps.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.edu.utfpr.aps.entidades.Usuario
import java.util.*


@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun buscarUsuarios(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun buscarUsuario(id: Int): Usuario

    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha")
    fun login(email: String, senha: String): Usuario

    @Insert
    fun inserir(usuario: Usuario)

    @Delete
    fun apagar(usuario: Usuario)

    @Query("UPDATE usuarios SET nome = :nome, email = :email, senha = :senha WHERE id = :id")
    fun atualizarUsuario(id: Int, nome: String, email: String, senha: String): Int

    @Query("UPDATE usuarios SET pontuacao = pontuacao + :pontos, ultimaPartida = :ultimaPartida WHERE email = :email AND senha = :senha")
    fun pontuar(pontos: Int, ultimaPartida: Date, email: String, senha: String): Int

    @Query("SELECT pontuacao FROM usuarios WHERE email = :email AND senha = :senha")
    fun getPontuacao(email: String, senha: String): Int
}