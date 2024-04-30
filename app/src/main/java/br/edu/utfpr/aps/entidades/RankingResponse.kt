package br.edu.utfpr.aps.entidades

data class RankingResponse (
    var sucesso: Boolean,
    var ranking: List<Usuario>,
    var jogadoresSemPontos: List<String>
)