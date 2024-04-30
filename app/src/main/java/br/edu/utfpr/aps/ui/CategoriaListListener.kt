package br.edu.utfpr.aps.ui

import br.edu.utfpr.aps.entidades.Categoria

interface CategoriaListListener {
    fun getCategoria(categoria: Categoria)
}