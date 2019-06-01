package com.example.front_ui.KotlinCode

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.front_ui.DataModel.PostingInfo
import com.example.front_ui.DishView
import com.example.front_ui.Interface.MyPageDataPass
import com.example.front_ui.R
import com.example.front_ui.Utils.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.my_page.*
import kotlinx.android.synthetic.main.polar_style.view.*
import org.jetbrains.anko.toast
import org.w3c.dom.Document

class PostToMyPage : AppCompatActivity() {

    private lateinit var postingInfo : PostingInfo
    private val TAG = "TAGPostToMyPage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_page)

        /**
         * DishView 에서 온 postingInfo 를 받음
         * **/
        val getIntent  = this.intent
        val bundle = getIntent.extras
        postingInfo = bundle.getSerializable("allPostingInfo") as PostingInfo
        Log.d(TAG, "DishView.java 로부터 넘어온 postingInfo를 받았습니다. = 포스팅의 아이디는 ${postingInfo.postingId}")
        toast("안녕 ${postingInfo.writerId}")

        /**
         * 프로필 사진 설정 + 작성자 쓴 글 불러오기
         * **/
        //Glide용 이미지 로딩
        val circularProgressDrawable = CircularProgressDrawable(applicationContext)
        circularProgressDrawable.setStrokeCap(Paint.Cap.ROUND)
        circularProgressDrawable.setCenterRadius(10f)
        circularProgressDrawable.setBackgroundColor(R.color.colorMainSearch)
        circularProgressDrawable.start()
        //작성자 프로필 이미지 붙이기
        val userImage = getIntent.getStringExtra("writerImage")
        if(userImage != null){
            GlideApp.with(this)
                    .load(userImage)
                    .placeholder(circularProgressDrawable)
                    .into(circleImage)
        }
        else{
            GlideApp.with(this)
                    .load(R.drawable.basic_user_image)
                    .placeholder(circularProgressDrawable)
                    .into(circleImage)
        }
        //작성자가 쓴 글 불러오기
        val writerId = postingInfo.writerId
        val adapter = PostToMyPageAdapter(this, writerId, textNumOfData)
        gridview.adapter = adapter
        /**
         * 팔로우 기능
         * **/
        //본인 계정 마이페이지 갈 경우 팔로우버튼 숨김
        if(FirebaseAuth.getInstance().uid == postingInfo.writerId){
            follow_btn_myPage.visibility = View.GONE
        }
        //팔로우 버튼 누를시
        follow_btn_myPage
    }
}
class PostToMyPageAdapter(val context : Context,
                          val writerId : String,
                          val textview : TextView) : BaseAdapter(){

    private var list = arrayListOf<PostingInfo>()
    private val storage : FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val TAG = "TAGPostToMyPageAdapter"

    init {
        getDataFromFirestore(writerId){
            textview.text = it.toString()
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.polar_style, parent, false)
        val itemImage = view.imagefood
        val singleItem = list[position]
        val storageRef = storage.getReferenceFromUrl(singleItem.imagePathInStorage)
        GlideApp.with(context)
                .load(storageRef)
                .into(itemImage)

        //클릭시 이벤트
        view.setOnClickListener {
            val intent = Intent(context, DishView::class.java)
            val bundle = Bundle()
            bundle.putSerializable("postingInfo", singleItem)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
    fun getDataFromFirestore(writerId: String, onComplete:(Int)->Unit){
        Log.d(TAG, "getDataFromFirestore함수 실행")
        val firestore = FirebaseFirestore.getInstance()
        if(writerId == null){
            Log.d(TAG, "받은 writerId가 없습니다.null입니다.")
        }
        firestore.collection("포스팅")
                .whereEqualTo("writerId", writerId)
//                .orderBy("postingTime", Query.Direction.ASCENDING)
//                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    if(firebaseFirestoreException != null) Log.d(TAG, "파이어스토어 예외 발생")
//                    if(querySnapshot != null){
//                        Log.d(TAG, "포스팅에 witerId(${writerId}에 해당하는 querySnapshpot 존재)")
//                        var numOfData = 0
//                        for(dc in querySnapshot.documentChanges){
//                            when(dc.type){
//                                DocumentChange.Type.ADDED -> {
//                                    numOfData+=1
//                                    val postingData = dc.document.toObject(PostingInfo::class.java)
//                                    list.add(postingData)
//                                    notifyDataSetChanged()
//                                }
//                            }
//                        }
//                        mCallback?.setNumberOfData(numOfData)
//                    }
//                    else{
//                        Log.d(TAG, "querySnapshot이 null입니다.")
//                    }
//                }
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d(TAG, document.id + " => " + document.data)
                            //store 정보를 가져오고, id를 따로 저장한다.
                            val postingInfo = document.toObject(PostingInfo::class.java)
                            //해당 가게 정보의 post데이터를 가져온다.
                            list.add(postingInfo)
                            notifyDataSetChanged()
                        }
                        onComplete(task.result.size())
                        Log.d(TAG, "getPostingData size : " + task.result.size())
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
    }
}