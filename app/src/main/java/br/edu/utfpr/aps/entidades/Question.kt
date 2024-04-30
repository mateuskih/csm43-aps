package br.edu.utfpr.aps.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "perguntas")
data class Question (
    var category: String,
    var type: String,
    var difficulty: String,
    var question: String,
    @SerializedName("correct_answer")
    var correctAnswer: String,
    @TypeConverters(Converters::class)
    @SerializedName("incorrect_answers")
    var incorrectAnswers: List<String>

){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}