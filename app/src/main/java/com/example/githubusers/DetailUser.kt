package com.example.githubusers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.example.githubusers.databinding.ActivityDetailUserBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUser : AppCompatActivity() {

    private lateinit var binding : ActivityDetailUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail User"
        val username = intent.getStringExtra(EXTRA_USERNAME)
        getUserData(username!!)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter
        sectionsPagerAdapter.username = username
        TabLayoutMediator(binding.tabs, binding.viewPager){ tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

    }

    private fun getUserData(username: String){
        showsLoading(true)
        val client = ApiConfig.getApiService().getDetailUser(username)
        client.enqueue(object : Callback<UserGithub>{
            override fun onResponse(call: Call<UserGithub>, response: Response<UserGithub>) {
                showsLoading(false)
                val responseBody = response.body()
                if (responseBody != null){
                    setUserData(responseBody)
                }
            }

            override fun onFailure(call: Call<UserGithub>, t: Throwable) {
                showsLoading(false)
                Toast.makeText(this@DetailUser, "Something Wrong : /n ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setUserData(user : UserGithub?){
        with(binding){
            tvUsername.text = user?.username
            tvFullname.text = user?.fullname ?: "-"
            tvRepository.text = getString(R.string.repository, user?.repository ?: "-")
            tvCompany.text = user?.company ?: "-"
            tvLocation.text = user?.location ?: "-"

            Glide.with(this@DetailUser).load(user?.avatar).into(imgProfile)
        }
    }

    private fun showsLoading(isLoading : Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object{
        const val EXTRA_USERNAME = "extra_username"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_follower,
            R.string.tab_following
        )
    }

}