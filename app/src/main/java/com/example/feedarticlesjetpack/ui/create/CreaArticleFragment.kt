package com.example.feedarticlesjetpack.ui.create

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feedarticlesjetpack.R
import com.example.feedarticlesjetpack.util.navController
import com.example.feedarticlesjetpack.databinding.FragmentCreaArticleBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreaArticleFragment : Fragment() {
    private var _binding: FragmentCreaArticleBinding? = null
    private val binding get() = _binding!!

    private val creaViewModel: CreaViewModel by viewModels()

    private var selectedCategory: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreaArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCreaImgUrl.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val imageUrl = s?.toString()?.trim() ?: ""
                if (imageUrl.isNotEmpty()) {
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.feedarticles_logo)
                        .into(binding.ivEditPreview)
                } else {
                    binding.ivEditPreview.setImageResource(R.drawable.feedarticles_logo)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.radioBtnSport.setOnClickListener {
            selectedCategory = 1
            updateRadioButtonsUI(binding.radioBtnSport)
        }
        binding.radioBtnManga.setOnClickListener {
            selectedCategory = 2
            updateRadioButtonsUI(binding.radioBtnManga)
        }
        binding.radioBtnVarious.setOnClickListener {
            selectedCategory = 3
            updateRadioButtonsUI(binding.radioBtnVarious)
        }

        binding.btnSave.setOnClickListener {
            val title = binding.tvCreaTitleArticle.text.toString().trim()
            val content = binding.tvCreaContent.text.toString().trim()
            val imageUrl = binding.tvCreaImgUrl.text.toString().trim()

            if (title.isEmpty() || content.isEmpty() || imageUrl.isEmpty() || selectedCategory == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.please_fill_all_fields_and_choose_a_category), Toast.LENGTH_SHORT).show()
            } else {
                creaViewModel.addArticle(title, content, imageUrl, selectedCategory!!)
            }
        }

        creaViewModel.userMessageLiveData.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        creaViewModel.navigationDestination.observe(viewLifecycleOwner) { destinationId ->
            destinationId?.let { navController.navigate(it) }
        }
    }

    private fun updateRadioButtonsUI(selected: RadioButton) {
        binding.radioBtnSport.isChecked = false
        binding.radioBtnManga.isChecked = false
        binding.radioBtnVarious.isChecked = false

        selected.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
