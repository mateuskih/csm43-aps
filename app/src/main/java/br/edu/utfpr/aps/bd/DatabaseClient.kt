package br.edu.utfpr.aps.bd

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.edu.utfpr.aps.RegisterFragment
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.bd.dao.DificuldadeDao
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
                .fallbackToDestructiveMigration()
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

    fun getCategoriaDao(context: Context): CategoriaDao {
        return getDatabase(context).categoriaDao()
    }

    fun getDificuldadeDao(context: Context): DificuldadeDao {
        return getDatabase(context).dificuldadeDao()
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE usuarios ADD COLUMN foto ByteArray DEFAULT null")
        }
    }
}
