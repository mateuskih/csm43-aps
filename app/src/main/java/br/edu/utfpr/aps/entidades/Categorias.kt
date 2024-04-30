package br.edu.utfpr.aps.entidades

import com.google.gson.annotations.SerializedName

data class Categorias (
    @SerializedName("trivia_categories")
    var categoria: List<Categoria>
)