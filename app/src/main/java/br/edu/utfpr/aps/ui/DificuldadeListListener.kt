package br.edu.utfpr.aps.ui

import br.edu.utfpr.aps.entidades.Dificuldade

interface DificuldadeListListener {
    fun getDificuldade(dificuldade: Dificuldade)
}