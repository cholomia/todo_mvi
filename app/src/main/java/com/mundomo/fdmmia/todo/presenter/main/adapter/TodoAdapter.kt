package com.mundomo.fdmmia.todo.presenter.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.mundomo.fdmmia.todo.domain.model.Todo

class TodoAdapter(
    private val onSelectTodo: (Todo) -> Unit
) : ListAdapter<Todo, TodoViewHolder>(TodoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder =
        TodoViewHolder.newInstance(
            parent,
            onSelectTodo
        )

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}