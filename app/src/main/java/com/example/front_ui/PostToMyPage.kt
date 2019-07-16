package com.example.front_ui

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
import com.example.front_ui.DataModel.PostingInfo
import com.example.front_ui.DataModel.SerializableStoreInfo
import com.example.front_ui.DataModel.StoreInfo
import com.example.front_ui.Utils.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.my_page.*
import kotlinx.android.synthetic.main.polar_style.view.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.toast

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
         * 팔로우 클릭시 내 서브컬렉션 팔로잉 추가, 상대방 팔로워 추가
         * **/
        //본인 마이페이지일 경우엔 팔로우 버튼 숨긷
        if(postingInfo.writerId.equals(FirebaseAuth.getInstance().uid.toString())){
            followToggle.visibility = View.GONE
        }
        else{
            //디비에 이미 팔로우한 사람의 경우엔 토글 모양을 미리 바꿔줌.
            followToggle.visibility = View.INVISIBLE//바뀌는 과정 안보여주게 하려고
            FirebaseFirestore.getInstance().collection("사용자")
                    .document(FirebaseAuth.getInstance().uid!!)
                    .collection("팔로잉")
                    .document(postingInfo.writerId)
                    .get().addOnSuccessListener {
                        Log.d(TAG, "파이어스토어 '사용자 -> 팔로잉'에 접근합니다.")
                        if (it["팔로우 여부"] == true) {
                            followToggle?.setChecked(true)
                            Log.d(TAG, "팔로우 버튼 -> 팔로잉 버튼")
                        } else {
                            followToggle?.setChecked(false)
                            Log.d(TAG, "팔로우 버튼 변경 X")
                        }
                        followToggle.visibility = View.VISIBLE
                    }
            //팔로우 버튼 on/off에 따른 이벤트 바로 반영
            followToggle.onCheckedChange { buttonView, isChecked ->
                val userToFollowing = FirebaseFirestore.getInstance()
                        .collection("사용자")
                        .document(FirebaseAuth.getInstance().uid!!)
                        .collection("팔로잉")
                        .document(postingInfo.writerId)
                val userToFollower = FirebaseFirestore.getInstance()
                        .collection("사용자")
                        .document(postingInfo.writerId)
                        .collection("팔로워")
                        .document(FirebaseAuth.getInstance().uid!!)

                if(isChecked){
                    Log.d(TAG, "팔로우 버튼 클릭됨")
                    //본인 사용자 서브 콜렉션에 팔로잉 추가
                    userToFollowing
                            .set(mapOf("팔로우 여부" to true)).addOnSuccessListener {
                                Log.d(TAG, "팔로우 버튼을 눌러서 '사용자->팔로잉' 디비에 업로드 완료")
                            }
                    //상대방 사용자 서브 콜렉션에 팔로워 추가
                    userToFollower
                            .set(mapOf("팔로워 여부" to true)).addOnSuccessListener {
                                Log.d(TAG, "팔로우 버튼을 눌러서 '사용자->팔로워' 디비에 업로드 완료")
                            }
                }
                else{
                    Log.d(TAG, "팔로우 버튼 클릭 해제됨")
                    userToFollowing.delete().addOnSuccessListener {
                        Log.d(TAG, "팔로우 버튼 해제해서 '사용자->팔로잉' 디비 삭제 완료")
                    }
                    userToFollower.delete().addOnSuccessListener {
                        Log.d(TAG, "팔로우 버튼 해제해서 '사용자->팔로워' 디비 삭제 완료")
                    }
                }
            }
        }
    }
}

class PostToMyPageAdapter(val context : Context,
                          val writerId : String,
                          val textview : TextView) : BaseAdapter(){

    private var list = arrayListOf<PostingInfo>()
    private val storage : FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val TAG = "TAGPostToMyPageAdapter"
    private val db : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

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
//            val bundle = Bundle()
//            bundle.putSerializable("postingInfo", singleItem)
//            intent.putExtras(bundle)
//            context.startActivity(intent)

            db.collection("가게")
                    .document(singleItem.storeId)
                    .addSnapshotListener { snapshot, exception ->
                        if(exception != null) return@addSnapshotListener
                        if(snapshot != null){
                            val storeInfo = snapshot?.toObject(StoreInfo::class.java)
                            val serializableStoreInfo = SerializableStoreInfo(storeInfo)

                            intent.putExtra("postingInfo", singleItem)
                            intent.putExtra("storeInfo", serializableStoreInfo)
                            context.startActivity(intent)
                        }
                    }
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