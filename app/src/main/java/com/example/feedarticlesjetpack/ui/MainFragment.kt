package com.example.feedarticlesjetpack.ui

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feedarticlesjetpack.R
import com.example.feedarticlesjetpack.util.navController
import com.example.feedarticlesjetpack.ViewModels.MainFragmentViewModel
import com.example.feedarticlesjetpack.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ArticleAdapter
    private val mainViewModel: MainFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listAdapter.layoutManager = LinearLayoutManager(requireContext())

        adapter = ArticleAdapter(
            onItemClick = { article ->
                if (mainViewModel.isOwner(article)) {
                    val navDir =
                        MainFragmentDirections.actionMainFragmentToEditArticleFragment(article)
                    navController.navigate(navDir)
                } else {
                    val navDir =
                        MainFragmentDirections.actionMainFragmentToDetailsArticleFragment(article)
                    findNavController().navigate(navDir)
                }
            },
            onFavoriteClick = {
                // Ici, dans MainFragment, le clic sur l'étoile ne bascule pas le favori
                // Il sert à filtrer uniquement les articles favoris.
            }
        )
        binding.listAdapter.adapter = adapter

        mainViewModel.articlesLiveData.observe(viewLifecycleOwner) { articles ->
            adapter.submitList(articles)
            binding.swipeRefresh.isRefreshing = false
        }

        mainViewModel.userMessageLiveData.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.navigationDestination.observe(viewLifecycleOwner) { destination ->
            destination?.let { navController.navigate(it) }
        }

        binding.ivAddArticle.setOnClickListener {
            mainViewModel.navigateToAddArticle()
        }

        binding.btnMainFav.setOnClickListener {
            val newState = !binding.btnMainFav.isSelected
            mainViewModel.setFavoritesFilter(newState)
            if (newState) {
                binding.btnMainFav.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                binding.btnMainFav.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        binding.ivMainLogOff.setOnClickListener {
            mainViewModel.logOff()
        }

        // Configuration des radio buttons pour le filtre par catégorie
        binding.radioMainAll.setOnClickListener {
            mainViewModel.setSelectedCategory(null)
            updateRadioButtonsUI(null)
        }
        binding.radioMainSport.setOnClickListener {
            mainViewModel.setSelectedCategory(1)
            updateRadioButtonsUI(1)
        }
        binding.radioMainManga.setOnClickListener {
            mainViewModel.setSelectedCategory(2)
            updateRadioButtonsUI(2)
        }
        binding.radioMainVarious.setOnClickListener {
            mainViewModel.setSelectedCategory(3)
            updateRadioButtonsUI(3)
        }

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.loadArticles()
        }
    }

    /**
     * Met à jour l'apparence des radio buttons selon la catégorie sélectionnée.
     * @param selectedCategory La catégorie sélectionnée (null signifie "All")
     */
    private fun updateRadioButtonsUI(selectedCategory: Int?) {
        binding.radioMainAll.isChecked = selectedCategory == null
        binding.radioMainSport.isChecked = selectedCategory == 1
        binding.radioMainManga.isChecked = selectedCategory == 2
        binding.radioMainVarious.isChecked = selectedCategory == 3
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
