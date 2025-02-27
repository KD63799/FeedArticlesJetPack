package com.example.feedarticlesjetpack.ui.edit

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.feedarticlesjetpack.R
import com.example.feedarticlesjetpack.util.navController
import com.example.feedarticlesjetpack.databinding.FragmentEditArticleBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditArticleFragment : Fragment() {
    private var _binding: FragmentEditArticleBinding? = null
    private val binding get() = _binding!!

    private val editViewModel: EditViewModel by viewModels()
    private val args: EditArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentEditArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editViewModel.setArticle(args.articleBdl)

        editViewModel.article.observe(viewLifecycleOwner) { article ->
            binding.tvEditTitleArticle.setText(article.titre)
            binding.tvEditContent.setText(article.descriptif)
            binding.tvEditImgUrl.setText(article.url_image)
            Picasso.get()
                .load(article.url_image)
                .placeholder(R.drawable.feedarticles_logo)
                .into(binding.ivEditPreview)
        }

        editViewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            updateRadioButtonsUI(category)
        }

        binding.radioEditSport.setOnClickListener {
            editViewModel.setSelectedCategory(1)
        }
        binding.radioBtnManga.setOnClickListener {
            editViewModel.setSelectedCategory(2)
        }
        binding.radioBtnVarious.setOnClickListener {
            editViewModel.setSelectedCategory(3)
        }

        binding.btnEditUpdate.setOnClickListener {
            val updatedTitle = binding.tvEditTitleArticle.text.toString().trim()
            val updatedContent = binding.tvEditContent.text.toString().trim()
            val updatedImgUrl = binding.tvEditImgUrl.text.toString().trim()
            editViewModel.updateArticle(updatedTitle, updatedContent, updatedImgUrl)
        }

        binding.btnEditDelete.setOnClickListener {
            editViewModel.deleteArticle()
            navController.navigate(R.id.action_editArticleFragment_to_mainFragment)
        }

        editViewModel.navigationDestination.observe(viewLifecycleOwner) { destinationId ->
            destinationId?.let { navController.navigate(it) }
        }

        editViewModel.messageLiveData.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun updateRadioButtonsUI(selectedCategory: Int?) {
        binding.radioEditSport.isChecked = selectedCategory == 1
        binding.radioBtnManga.isChecked = selectedCategory == 2
        binding.radioBtnVarious.isChecked = selectedCategory == 3
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
