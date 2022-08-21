package com.minitiktok.android.ui.userdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.minitiktok.android.R
import com.minitiktok.android.databinding.FragmentUserDetailBinding

class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userDetailViewModel =
            ViewModelProvider(this).get(UserDetailViewModel::class.java)

        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userDetailViewModel.nickname.observe(viewLifecycleOwner) {
            binding.nickname.text = it
        }
        userDetailViewModel.like_total.observe(viewLifecycleOwner) {
            binding.like.text = getString(R.string.user_detail_likes, it.toString())
        }
        userDetailViewModel.fans_total.observe(viewLifecycleOwner) {
            binding.fans.text = getString(R.string.user_detail_fans, it.toString())
        }
        userDetailViewModel.following_number.observe(viewLifecycleOwner) {
            binding.following.text = getString(R.string.user_detail_following, it.toString())
        }
        userDetailViewModel.douyin_id.observe(viewLifecycleOwner) {
            binding.douyiId.text = getString(R.string.user_detail_douyin_id, it.toString())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}