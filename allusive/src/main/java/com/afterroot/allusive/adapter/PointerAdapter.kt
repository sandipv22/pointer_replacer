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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.afterroot.allusive.GlideApp
import com.afterroot.allusive.R
import com.afterroot.allusive.utils.getMinPointerSize
import java.util.*

/**GridView Image Adapter. */
class PointerAdapter(private val mContext: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    val _tag = "PointerAdapter"

    companion object {
        var itemList = ArrayList<String>()
    }

    fun clear() {
        itemList.clear()
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): String {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val holder: ViewHolder
        var view: View? = convertView

        if (view == null) {
            view = inflater.inflate(R.layout.item_pointer_grid, parent, false)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.grid_item_image)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        var size = mContext.getMinPointerSize()
        if (size <= 0) {
            size = 66
        }

        GlideApp.with(mContext)
            .load(itemList[position])
            .override(size)
            .into(holder.imageView!!)

        return view
    }

    inner class ViewHolder {
        internal var imageView: ImageView? = null
    }
}