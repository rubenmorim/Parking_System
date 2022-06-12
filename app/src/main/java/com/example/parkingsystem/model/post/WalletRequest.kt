package com.example.parkingsystem.model.post

import android.text.Editable

data class WalletRequest(
    val idUtilizador: Long,
    val pagamento: String,
)