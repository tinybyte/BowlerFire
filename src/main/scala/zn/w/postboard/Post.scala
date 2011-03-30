package zn.w.postboard

import collection.mutable.MutableList

case class Post(postId: Long, title: String, content: String)

object Post {
  var allPosts = new MutableList[Post]
  allPosts += Post(1L, "first Post", "first post content")

  def findAll = allPosts.toList
}