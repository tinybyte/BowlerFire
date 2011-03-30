package zn.w.postboard
import org.bowlerframework.model.ParameterMapper

import org.bowlerframework.view.Renderable

import org.bowlerframework.model.Validations

import org.bowlerframework.controller.Controller
import org.bowlerframework.examples.{Widget, NewWidgetForm}
import org.bowlerframework.view.squery.MarkupContainer

class PostController extends Controller with ParameterMapper with Validations with Renderable {
	def renderPosts = { 
		val posts = Post.findAll
		if(posts.size == 0) render
		else render(posts)
	}
	get("/posts")((request, response) => {renderWith(new PostPage(new MarkupContainer {}), "test page")})
  //get("/posts")((request, response) => {renderWith(new PostPage(new MarkupContainer {<div class="tabs-container"></div>}), "test page")})
}
