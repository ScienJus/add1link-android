package com.scienjus.add1link.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class WebviewViewModel : ViewModel() {

    val url = MutableLiveData<String>()

}