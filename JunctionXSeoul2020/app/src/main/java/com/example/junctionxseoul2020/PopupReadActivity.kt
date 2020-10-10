package com.example.junctionxseoul2020

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.junctionxseoul2020.adapter.CommentListAdapater
import com.example.junctionxseoul2020.data.Post
import kotlinx.android.synthetic.main.activity_popup_read.*
import java.util.*

class PopupReadActivity : FragmentActivity() {

    lateinit var post: Post
    lateinit var commentListView: RecyclerView
    lateinit var adapter: CommentListAdapater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lpWindow: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.6f
        window.attributes = lpWindow

        setContentView(R.layout.activity_popup_read)

        commentListView = findViewById(R.id.commentListView)
        commentListView.layoutManager = LinearLayoutManager(applicationContext)

        post = intent.getSerializableExtra("post") as Post
        storyTextView.text = post.story
        uploadTime.text = post.uploadTime
        if (post.comments == null) {
            post.comments = ArrayList<String>()
        }
//        comments = post.comments!!

//        storyTextView.text = intent.getStringExtra("story")!!
//        uploadTime.text = intent.getStringExtra("uploadTime") + " 작성"
//        comments = intent.getStringArrayListExtra("comments")

        adapter = CommentListAdapater(post.comments!!)
        commentListView.adapter = adapter

        checkCommentNum()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 댓글 입력창을 닫은 경우
        if (requestCode == 1010) {
            if (data != null) {
                if (data.getBooleanExtra("inputOK", false)) {
                    val comment: String = data.getStringExtra("comment")
                    // 덧글을 새롭게 추가해야함
                    if (post.comments == null) {
                        post.comments = ArrayList<String>()
                    }
                    post.comments!!.add(comment)
                    adapter.notifyDataSetChanged()
                    checkCommentNum()
                    // 덧글 DB에 반영해야하고, MainActivtity에 있는 PostManager 내부의 posts에도 반영시켜야 함

                }
            }
        }

    }

    fun checkCommentNum() {
        if (post.comments!!.isEmpty()) {
            commentListView.visibility = View.GONE
            noComment.visibility = View.VISIBLE
        } else {
            commentListView.visibility = View.VISIBLE
            noComment.visibility = View.GONE
        }
    }

    fun onCloseBtnClicked(view: View) {
        val intent: Intent = Intent()
        intent.putExtra("post", post)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun onStoryTextViewClicked(view: View) {
        val textView: TextView = view as TextView

        if (textView.layout != null) {
            // ellipsize 여부 확인
            if (textView.layout.getEllipsisCount(textView.lineCount - 1) > 0) {
                textView.maxLines = 1000
                textView.ellipsize = null
            } else {
                textView.maxLines = 2
                textView.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }

    fun onAddCommentClicked(view: View) {
        val intent: Intent = Intent(this, PopupCommentWriteActivity::class.java)
        startActivityForResult(intent, 1010)
    }
}