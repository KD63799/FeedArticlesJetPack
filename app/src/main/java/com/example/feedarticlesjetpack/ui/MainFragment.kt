package com.example.feedarticlesjetpack.ui

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feedarticlesjetpack.util.navController
import com.example.feedarticlesjetpack.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ArticleAdapter
    private val mainViewModel: MainFragmentViewModel by viewModels()

    private var favoritesFilterEnabled = false

    private var currentCategory: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Vous pouvez récupérer d'autres arguments si nécessaire avec Safe Args ici.
        arguments?.let {
            it.getInt("artieID")
        }

        binding.listAdapter.layoutManager = LinearLayoutManager(requireContext())

        adapter = ArticleAdapter(
            onItemClick = { article ->
                // Utiliser la fonction isOwner() du ViewModel pour décider de la navigation
                if (mainViewModel.isOwner(article)) {
                    val navDir =
                        MainFragmentDirections.actionMainFragmentToEditArticleFragment(article)
                    navController.navigate(navDir)
                } else {
                    val navDir =
                        MainFragmentDirections.actionMainFragmentToDetailsArticleFragment(article)
                    navController.navigate(navDir)
                }
            },
            onFavoriteClick = {
                // Gestion du clic sur l'icône favorite (filtrage dans MainFragment)
            }
        )

        binding.listAdapter.adapter = adapter

        // Observer la liste des articles et soumettre la liste à l'adapter.
        mainViewModel.articlesLiveData.observe(viewLifecycleOwner) { articles ->
            adapter.submitList(articles)
        }

        // Observer les messages utilisateur
        mainViewModel.userMessageLiveData.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // Observer la navigation
        mainViewModel.navigationDestination.observe(viewLifecycleOwner) { destination ->
            destination?.let { navController.navigate(it) }
        }

        binding.ivAddArticle.setOnClickListener {
            mainViewModel.navigateToAddArticle()
        }

        binding.btnMainFav.setOnClickListener {
            // Toggle du filtre favoris
            favoritesFilterEnabled = !favoritesFilterEnabled
            mainViewModel.setFavoritesFilter(favoritesFilterEnabled)
            // Met à jour l'icône en fonction de l'état
            if (favoritesFilterEnabled) {
                binding.btnMainFav.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                binding.btnMainFav.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        binding.ivMainLogOff.setOnClickListener {
            mainViewModel.logOff()
        }

        // Configuration des radio buttons pour le filtre par catégorie.
        binding.radioMainAll.setOnClickListener {
            if (currentCategory != null) {
                currentCategory = null
                mainViewModel.setCategoryFilter(null)
                updateRadioButtonsUI(binding.radioMainAll)
            }
        }
        binding.radioMainSport.setOnClickListener {
            currentCategory = if (currentCategory == 1) null else 1
            mainViewModel.setCategoryFilter(currentCategory)
            updateRadioButtonsUI(binding.radioMainSport)
        }
        binding.radioMainManga.setOnClickListener {
            currentCategory = if (currentCategory == 2) null else 2
            mainViewModel.setCategoryFilter(currentCategory)
            updateRadioButtonsUI(binding.radioMainManga)
        }
        binding.radioMainVarious.setOnClickListener {
            currentCategory = if (currentCategory == 3) null else 3
            mainViewModel.setCategoryFilter(currentCategory)
            updateRadioButtonsUI(binding.radioMainVarious)
        }

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.loadArticles()
            binding.swipeRefresh.isRefreshing = false
        }

    }

    /**
     * Met à jour l'apparence des radio buttons pour refléter le filtre actif.
     */
    private fun updateRadioButtonsUI(selected: RadioButton) {
        binding.radioMainAll.isChecked = false
        binding.radioMainSport.isChecked = false
        binding.radioMainManga.isChecked = false
        binding.radioMainVarious.isChecked = false

        // Si aucun filtre n'est appliqué, cochez "All"
        if (currentCategory == null) {
            binding.radioMainAll.isChecked = true
        } else {
            selected.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
