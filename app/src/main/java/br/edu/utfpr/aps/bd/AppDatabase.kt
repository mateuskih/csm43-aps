package br.edu.utfpr.aps.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.bd.dao.DificuldadeDao
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import br.edu.utfpr.aps.entidades.*

@Database(entities = arrayOf(Usuario::class, Categoria::class, Dificuldade::class, Question::class), version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun perguntaDao(): PerguntaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun dificuldadeDao(): DificuldadeDao
}
