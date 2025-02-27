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

    // Variable pour stocker la catégorie sélectionnée (1: Sport, 2: Manga, 3: Various)
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

        // Mettre à jour l'aperçu de l'image dès que l'URL change
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

        // Configurer les radio buttons pour la sélection de catégorie
        binding.radioEditSport.setOnClickListener {
            selectedCategory = 1
            updateRadioButtonsUI(binding.radioEditSport)
        }
        binding.radioEditManga.setOnClickListener {
            selectedCategory = 2
            updateRadioButtonsUI(binding.radioEditManga)
        }
        binding.radioEditVarious.setOnClickListener {
            selectedCategory = 3
            updateRadioButtonsUI(binding.radioEditVarious)
        }

        // Bouton de soumission
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

        // Observer les messages utilisateur
        creaViewModel.userMessageLiveData.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // Observer la navigation
        creaViewModel.navigationDestination.observe(viewLifecycleOwner) { destinationId ->
            destinationId?.let { navController.navigate(it) }
        }
    }

    /**
     * Met à jour l'apparence des radio buttons.
     * Le radio button sélectionné sera coché et les autres décochés.
     */
    private fun updateRadioButtonsUI(selected: RadioButton) {
        binding.radioEditSport.isChecked = false
        binding.radioEditManga.isChecked = false
        binding.radioEditVarious.isChecked = false

        selected.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
