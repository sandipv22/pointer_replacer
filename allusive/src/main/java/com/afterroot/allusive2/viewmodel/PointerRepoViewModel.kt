/*
 * Copyright (C) 2016-2020 Sandip Vaghela
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

package com.afterroot.allusive2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afterroot.allusive2.database.DatabaseFields
import com.google.firebase.firestore.FirebaseFirestore

class PointerRepoViewModel : ViewModel() {
    private var pointerSnapshot = MutableLiveData<ViewModelState>()

    fun getPointerSnapshot(): LiveData<ViewModelState> {
        if (pointerSnapshot.value == null) {
            pointerSnapshot.postValue(ViewModelState.Loading)
            FirebaseFirestore.getInstance().collection(DatabaseFields.COLLECTION_POINTERS)
                .addSnapshotListener { querySnapshot, _ -> //TODO Remove Snapshot listener and replace with query.
                    if (querySnapshot != null) {
                        pointerSnapshot.postValue(ViewModelState.Loaded(querySnapshot))
                    }
                }
        }
        return pointerSnapshot
    }
}