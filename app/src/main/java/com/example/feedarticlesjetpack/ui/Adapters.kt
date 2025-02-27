package com.example.feedarticlesjetpack.ui

import com.example.feedarticlesjetpack.databinding.RvItemBinding
import com.squareup.picasso.Picasso
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.feedarticlesjetpack.DtosResponse.ArticleDto
import com.example.feedarticlesjetpack.R
import java.util.Locale

class ArticleAdapter(
    private val onItemClick: (ArticleDto) -> Unit,
    private val onFavoriteClick: (ArticleDto) -> Unit
) : ListAdapter<ArticleDto, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    inner class ArticleViewHolder(private val binding: RvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleDto) {
            binding.tvRvItemTitle.text = article.titre
            binding.tvRvContent.text = article.descriptif

            val formatedDate = convertFormatDate(article.created_at)

            binding.tvRvItemDate.text = formatedDate

            if (article.url_image.isEmpty()) {
                binding.ivRvItem.setImageResource(R.drawable.feedarticles_logo)
            } else {
                Picasso.get()
                    .load(article.url_image)
                    .placeholder(R.drawable.feedarticles_logo)
                    .into(binding.ivRvItem)
            }

            if (article.is_fav == 1) {
                binding.ivItemFavorite.setImageResource(android.R.drawable.star_big_on)
            } else {
                binding.ivItemFavorite.setImageResource(android.R.drawable.btn_star_big_off)
            }

            val bgColor = when (article.categorie) {
                1 -> ContextCompat.getColor(binding.root.context, R.color.sport_color)
                2 -> ContextCompat.getColor(binding.root.context, R.color.manga_color)
                3 -> ContextCompat.getColor(binding.root.context, R.color.divers_color)
                else -> Color.WHITE
            }
            binding.root.setBackgroundColor(bgColor)

            binding.root.setOnClickListener { onItemClick(article) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<ArticleDto>() {
    override fun areItemsTheSame(oldItem: ArticleDto, newItem: ArticleDto): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ArticleDto, newItem: ArticleDto): Boolean =
        oldItem == newItem
}

fun convertFormatDate(date: String): String {
    val dateInputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateOutputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    val objectDatefromdateStr = dateInputFormat.parse(date)
    return dateOutputFormat.format(objectDatefromdateStr) ?: "not a date"
}
