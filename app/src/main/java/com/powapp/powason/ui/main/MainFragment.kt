package com.powapp.powason.ui.main
/*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.powapp.powason.databinding.MainFragmentBinding
import com.powapp.powason.util.DBG

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().title = "Accounts"
        binding = MainFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.loginData.observe(viewLifecycleOwner, Observer {
            val loginUrls = StringBuilder()
            for (login in it) {
                Log.i(DBG, "${login.target} (${login.target_url})")
                loginUrls.append(login.target_url + "\n")
            }
            binding.textView.text = loginUrls
        })
        return binding.root
    }
}
*/