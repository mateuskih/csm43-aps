package br.edu.utfpr.aps.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.entidades.Converters
import br.edu.utfpr.aps.entidades.Question

@Database(entities = arrayOf(Question::class), version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun perguntaDao(): PerguntaDao
}
