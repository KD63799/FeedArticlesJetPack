package com.example.feedarticlesjetpack.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.feedarticlesjetpack.R
import com.example.feedarticlesjetpack.util.navController
import com.example.feedarticlesjetpack.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding
        get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnRegister.setOnClickListener {
                registerViewModel.checkRegisterForm(
                    etRegisterLogin.text.toString().trim(),
                    etRegisterPassword.text.toString().trim(),
                    etRegisterComfirmPassword.text.toString().trim()
                )

                etRegisterLogin.text.clear()
                etRegisterPassword.text.clear()
                etRegisterComfirmPassword.text.clear()
            }


            tvRegisterAlreadyaccount.setOnClickListener{
                navController.navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        registerViewModel.userMessageLiveData.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.navigationDestination.observe(viewLifecycleOwner) { destination ->
            destination?.let {
                navController.navigate(it)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}