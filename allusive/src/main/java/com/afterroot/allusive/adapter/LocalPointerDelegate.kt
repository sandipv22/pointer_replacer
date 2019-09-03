/*
 * Copyright (C) 2016-2019 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afterroot.allusive.adapter

import android.content.Context
import android.os.Environment
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.allusive.GlideApp
import com.afterroot.allusive.R
import com.afterroot.allusive.adapter.callback.ItemSelectedCallback
import com.afterroot.allusive.model.IPointer
import com.afterroot.allusive.model.RoomPointer
import com.afterroot.allusive.utils.getDrawableExt
import com.afterroot.allusive.utils.getMinPointerSize
import com.afterroot.allusive.utils.inflate
import kotlinx.android.synthetic.main.item_pointer_repo.view.*

class LocalPointerDelegate(val callbacks: ItemSelectedCallback) : TypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = PointerVH(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: IPointer) {
        holder as PointerVH
        holder.bind(item as RoomPointer)
    }


    inner class PointerVH(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_pointer_repo)) {
        val context: Context = parent.context

        fun bind(pointer: RoomPointer) {
            val path =
                "${Environment.getExternalStorageDirectory()}${context.getString(R.string.pointer_folder_path)}${pointer.file_name}"
            itemView.info_pointer_pack_name.text = pointer.pointer_name
            itemView.info_username.text =
                String.format(context.getString(R.string.str_format_uploaded_by), pointer.uploader_name)
            itemView.info_pointer_image.apply {
                GlideApp.with(context).load(path)
                    .override(context.getMinPointerSize(), context.getMinPointerSize())
                    .into(this)
                background = context.getDrawableExt(R.drawable.transparent_grid)
            }

            with(super.itemView) {
                tag = pointer
                setOnClickListener {
                    callbacks.onClick(adapterPosition, itemView)
                }
                setOnLongClickListener {
                    callbacks.onLongClick(adapterPosition)
                    return@setOnLongClickListener true
                }
            }
        }
    }

}