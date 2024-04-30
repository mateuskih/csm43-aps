package br.edu.utfpr.aps.entidades

data class QuestionsResponse (
    var response_code: Int,
    var results: List<Question>
)