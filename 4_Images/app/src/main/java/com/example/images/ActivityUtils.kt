package com.example.images

import android.app.AlertDialog
import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

fun Context.showOkDialog(title_id: Int, message_id: Int, onClickOkExtra: () -> Unit = {}) {
    val builder = AlertDialog.Builder(this)
        .apply {
            setTitle(title_id)
            setMessage(message_id)
            setCancelable(false)
            setNegativeButton("OK") { dialog, _ ->
                dialog.cancel()
                onClickOkExtra()
            }
        }
    val alert = builder.create()
    alert.show()
}

@Parcelize
enum class State : Parcelable {
    WAIT,
    SHOW,
    FAIL
}