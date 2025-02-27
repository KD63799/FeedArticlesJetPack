package com.example.feedarticlesjetpack.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.feedarticlesjetpack.R
import com.example.feedarticlesjetpack.util.navController
import com.example.feedarticlesjetpack.databinding.FragmentDetailsArticleBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsArticleFragment : Fragment() {
    private var _binding: FragmentDetailsArticleBinding? = null
    private val binding get() = _binding!!

    private val args: DetailsArticleFragmentArgs by navArgs()
    private val detailsViewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailsViewModel.setArticle(args.articleBdl)

        with(binding) {
            detailsViewModel.article.observe(viewLifecycleOwner) { article ->

                tvDetailsTitleArticle.text = article.titre
                tvArticleContent.text = article.descriptif
                val formatedDate = detailsViewModel.convertFormatDate(article.created_at)

                tvDetailsDate.text = "${getString(R.string.created_at)} ${formatedDate}"

                // Mise à jour de l'aperçu d'image
                Picasso.get()
                    .load(article.url_image)
                    .placeholder(R.drawable.feedarticles_logo)
                    .into(ivDetailsArticle)

                // Mise à jour du texte de catégorie en appelant getCategoryName()
                tvDetailsCategory.text = detailsViewModel.getCategoryName()
            }

            btnDetailsBack.setOnClickListener {
                navController.navigate(R.id.action_detailsArticleFragment_to_mainFragment)
            }
            ivDetailsFavorite.setOnClickListener {
                detailsViewModel.toggleFavorite()
            }

            detailsViewModel.article.observe(viewLifecycleOwner) { article ->
                if (article.is_fav == 1) {
                    binding.ivDetailsFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    ivDetailsFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
