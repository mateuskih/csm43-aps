package br.edu.utfpr.aps.bd

import android.content.Context
import androidx.room.Room
import br.edu.utfpr.aps.RegisterFragment
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.bd.dao.UsuarioDao

object DatabaseClient {

    @Volatile
    private var instance: AppDatabase? = null

    private fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "quiz_db.db"
            )
                .allowMainThreadQueries()
                .build().also { instance = it }
        }
    }

    fun getPerguntaDao(context: Context): PerguntaDao {
        return getDatabase(context).perguntaDao()
    }

    fun getUsuarioDao(context: Context): UsuarioDao {
        return getDatabase(context).usuarioDao()
    }
}
