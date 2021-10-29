package com.example.githubusers

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubusers.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu,menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchGithubUser(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    private fun searchGithubUser(username : String){
        showsLoading(true)
        val client = ApiConfig.getApiService().searchUser(username)
        client.enqueue(object : Callback<GithubResponse>{
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                showsLoading(false)
                val responseBody = response.body()
                val listUsers = ArrayList<UserGithub>()
                if (responseBody != null){
                    for (user in responseBody.listUser){
                        listUsers.add(user)
                    }
                    binding.rvGithubuser.layoutManager = LinearLayoutManager(this@MainActivity)
                    val userAdapter = UserAdapter(listUsers)
                    binding.rvGithubuser.adapter = userAdapter
                    userAdapter.setOnItemClickCallback(object: UserAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: UserGithub) {
                            val intent = Intent(this@MainActivity, DetailUser::class.java)
                            intent.putExtra(DetailUser.EXTRA_USERNAME, data.username)
                            startActivity(intent)
                        }
                    })
                }else{
                    Toast.makeText(this@MainActivity,"User Not Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                showsLoading(false)
                Toast.makeText(this@MainActivity,"Something Wrong :/n ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showsLoading(isLoading : Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}