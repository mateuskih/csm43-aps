package br.edu.utfpr.aps.entidades

data class LoginResponse (
    var sucesso: Boolean,
    var mensagem: String,
    var pontuacao: Int
)