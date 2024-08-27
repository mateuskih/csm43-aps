package br.edu.utfpr.aps.entidades

import android.content.Context
import android.widget.Toast

class Validator(private val context: Context) {

    /**
     * Valida os inputs e realiza verificações adicionais.
     * @param inputs Lista de pares (input, mensagem de erro para input vazio).
     * @param additionalChecks Lista de funções de validação adicionais. Se não for fornecida, será uma lista vazia.
     * @param errorMessages Lista de mensagens de erro para as verificações adicionais. Se não for fornecida, será uma lista vazia.
     * @return Retorna true se todos os testes passarem, caso contrário, false.
     */
    fun validate(
        inputs: List<Pair<String, String>>,
        additionalChecks: List<() -> Boolean> = emptyList(),
        errorMessages: List<String> = emptyList()
    ): Boolean {
        for ((input, message) in inputs) {
            if (input.isEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        for ((index, check) in additionalChecks.withIndex()) {
            if (!check()) {
                val errorMessage = errorMessages.getOrElse(index) { "Erro de validação" }
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }
}
