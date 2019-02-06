package com.mundomo.fdmmia.todo.presenter.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mundomo.fdmmia.todo.R
import com.mundomo.fdmmia.todo.domain.model.Todo
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoViewHolder(
    itemView: View,
    private val onSelectTodo: (Todo) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun newInstance(
            viewGroup: ViewGroup,
            onSelectTodo: (Todo) -> Unit
        ): TodoViewHolder =
            TodoViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.item_todo,
                    viewGroup,
                    false
                ),
                onSelectTodo
            )
    }

    fun bind(todo: Todo) {
        itemView.txt_title.text = todo.title
        itemView.txt_body.text = todo.body
        itemView.setOnClickListener { onSelectTodo(todo) }
    }


}
