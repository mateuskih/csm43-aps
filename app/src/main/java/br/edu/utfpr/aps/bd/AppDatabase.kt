package br.edu.utfpr.aps.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import br.edu.utfpr.aps.entidades.Converters
import br.edu.utfpr.aps.entidades.Question
import br.edu.utfpr.aps.entidades.Usuario

@Database(entities = arrayOf(Usuario::class, Question::class), version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun perguntaDao(): PerguntaDao
    abstract fun usuarioDao(): UsuarioDao
}
