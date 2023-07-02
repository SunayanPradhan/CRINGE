package com.sunayanpradhan.cringe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentHomeBinding.bind(view)

        val adapter= ViewPagerAdapter(requireContext() as AppCompatActivity)

        binding.homeViewPager.adapter= adapter

        TabLayoutMediator(binding.homeTabLayout, binding.homeViewPager) { tab, position ->

            when(position){

                0-> tab.text= "FOLLOWING"

                1-> tab.text= "RECOMMENDED"

            }


        }.attach()



    }

    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return 2 // Change this to the number of tabs
        }

        override fun createFragment(position: Int): Fragment {
            // Return the corresponding fragment for each tab position
            return when (position) {
                0 -> FollowFeedFragment()
                1 -> RecommendedFeedFragment()
                // Add more cases for additional fragments
                else -> throw IllegalArgumentException("Invalid tab position: $position")
            }
        }
    }




}