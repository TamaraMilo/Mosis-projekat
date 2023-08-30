package mosis.project.travelreport.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mosis.project.travelreport.api.repos.CommentRepository
import mosis.project.travelreport.data.CommentData

class CommentViewModel: ViewModel() {

    private val commentRepository = CommentRepository()
    val comments: MutableLiveData<List<CommentData>> = MutableLiveData()

    fun addComment(comment: CommentData, callback: (String?)->Unit) {
        commentRepository.addComment(comment) {
            if(it == null) {
                val existing = comments.value?.toMutableList()
                existing?.add(comment)
                existing?.let {itList->
                    comments.value = itList
                    callback(it)
                }
            } else {
                callback.invoke(it)
            }
        }
    }

    fun getAllCommentForLocation(locationId:String) {
        commentRepository.getAllCommentForLocation(locationId) {
            it?.let{itList->
                comments.value = itList
            }
        }
    }

}