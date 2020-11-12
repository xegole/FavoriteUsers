package com.bigthinkapps.sw.test.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bigthinkapps.sw.test.R
import com.bigthinkapps.sw.test.databinding.EmptyStateFragmentBinding

class EmptyStateFragment : Fragment(), LifecycleObserver {
    private lateinit var binding: EmptyStateFragmentBinding
    private lateinit var arguments: EmptyStateFragmentArgs

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreated(){
        activity?.lifecycle?.removeObserver(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.empty_state_fragment, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
        getArguments()?.let {
            arguments = EmptyStateFragmentArgs.fromBundle(it)
            binding.imageResource = if(arguments.image == 0) R.drawable.ic_contacts else arguments.image
            binding.textResource = if (arguments.text == 0) R.string.no_contacts_selected else arguments.text
        }
    }
}