package com.mundomo.fdmmia.todo.presenter.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mundomo.fdmmia.todo.domain.model.Todo

object TodoDiffCallback : DiffUtil.ItemCallback<Todo>() {

    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean = oldItem == newItem

}
