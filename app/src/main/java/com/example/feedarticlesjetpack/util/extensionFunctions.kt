package com.example.feedarticlesjetpack.util

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

val Fragment.navController get() = findNavController()

