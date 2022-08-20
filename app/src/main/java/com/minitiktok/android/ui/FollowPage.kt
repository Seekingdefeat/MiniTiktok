package com.minitiktok.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.minitiktok.android.R

//TODO:
//Replace the class TempUserClass with a "model class"
//Then get the user information and send it to RecyclerView adapter


class FollowPage : AppCompatActivity() {
    private lateinit var listTab : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_page)

        //TODO: : change this to viewBinding
        val mTabLayout : TabLayout = findViewById(R.id.follow_n_follower_layout)
        val mViewPager2 : ViewPager2 = findViewById(R.id.follow_n_follower_viewer)

        //Test data
        //See Hi~ to The King of LiTang and his Little pony perl
        //Don't forget to try RELX5
        val mUser = TempUserClass("丁真","中国-理塘",R.drawable.test_avatar_dz,true)
        val users = arrayListOf<TempUserClass>(mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser,mUser)


        //init
        //This array controls the tab numbers
        listTab = arrayListOf("关注","粉丝")

        mViewPager2.adapter = FollowerRecyclerAdapter(this,
            listTab, arrayListOf(users,users))

        //link viewPage with tabLayout
        val mediator = TabLayoutMediator(mTabLayout,mViewPager2, TabLayoutMediator.TabConfigurationStrategy{
            tab, position ->
            val mTextView = TextView(this)
            mTextView.text = listTab[position]
            mTextView.textAlignment=View.TEXT_ALIGNMENT_CENTER
            tab.customView = mTextView
        })
        mediator.attach()

    }

    //TODO: replace this user class with a true model class
    class TempUserClass(var userName: String,var address : String,var avatarFile: Int,val isFollowed: Boolean){
    }



    //ViewHolder, contains recyclerViews
    inner class RecyclerViewHolder(view:View) : RecyclerView.ViewHolder(view){
        // Init viewHolder here

        //Notice: cause in here the view didn't belong to the current activity
        //so the items must be init like this
        val title : TextView  = view.findViewById(R.id.top_tag)
        val recyclerView : RecyclerView = view.findViewById(R.id.user_recycler_view)
    }

    //Page view's adapter
    //To generate RecyclerViews in pageView
    inner class FollowerRecyclerAdapter(private val mContext : Context,
                                        private val titleList : ArrayList<String>,
                                        private val userDataList:ArrayList<ArrayList<TempUserClass>>)
        : RecyclerView.Adapter<RecyclerViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_follow_n_follower,parent,false)
            return RecyclerViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            val recycler = holder.recyclerView
            recycler.adapter = UserAdapter(mContext, userDataList[position])
            recycler.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false)
            holder.title.text = titleList[position] +"人数: " +userDataList[position].size
        }

        override fun getItemCount(): Int {
            return titleList.size
        }

    }



    //Adapter for recyclerView in PageViewer2
    class UserAdapter(private val context: Context, private val Users: java.util.ArrayList<TempUserClass>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
        inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view){
            // Init viewHolder here
            val AvatarView: ImageView = view.findViewById<ImageView>(R.id.avatar)
            val UserNameTextView: TextView = view.findViewById<TextView>(R.id.user_name)
            val UserAddressTextView: TextView = view.findViewById<TextView>(R.id.address)
            val FollowButton : Button = view.findViewById(R.id.follow_btn)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item,parent,false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //val stream = ByteArrayOutputStream()
            val config = BitmapFactory.Options()
            //To get better performance
            config.apply {
                inPreferredConfig = Bitmap.Config.RGB_565
                inSampleSize = 15
            }

            val tmpBitMap = BitmapFactory.decodeResource(context.resources,Users[position].avatarFile,config)

            //tmpBitMap.compress(Bitmap.CompressFormat.JPEG, 20,stream)
            //stream.close()
            holder.AvatarView.setImageBitmap(tmpBitMap)
            holder.UserNameTextView.text=Users[position].userName
            holder.UserAddressTextView.text = Users[position].address
            holder.FollowButton.apply {
                when(Users[position].isFollowed) {
                    true -> {
                        text = "已关注"
                    }
                    false -> {
                        setHintTextColor(Color.BLACK)
                    }
                }
            }
            holder.itemView.setOnClickListener{
                //Click to see user's detail page
                //TODO
            }
        }
        override fun getItemCount(): Int {
            return Users.size
        }
    }

}