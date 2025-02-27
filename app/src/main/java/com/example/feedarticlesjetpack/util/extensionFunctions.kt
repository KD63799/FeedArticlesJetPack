package com.example.feedarticlesjetpack.util

import android.content.Context
import android.widget.Toast
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

val Fragment.navController get() = findNavController()

