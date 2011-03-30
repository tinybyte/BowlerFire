package zn.w.postboard

import org.bowlerframework.view.squery.{Component, MarkupContainer}

/**
 * Created by IntelliJ IDEA.
 * User: Zane
 * Date: 3/30/11
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */

class PostPage (comp:MarkupContainer) extends Component {

  $(".tabs-container").contents = <p>TEST POST PAGE</p>

}