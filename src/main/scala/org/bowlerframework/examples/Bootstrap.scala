package org.bowlerframework.examples

import org.bowlerframework.view.scalate._
import org.bowlerframework.view.scalate.selectors._
import org.bowlerframework.examples.squeryl._
import com.recursivity.commons.bean.{StringValueTransformer, TransformerRegistry}
import util.matching.Regex
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.squeryl.adapters.H2Adapter
import org.squeryl.{SessionFactory, Session}
import java.io.{StringWriter, PrintWriter}
import org.bowlerframework.squeryl.SquerylController
import org.bowlerframework.squeryl.dao.LongKeyedDao
import org.bowlerframework.persistence.{EntityTransformer, CrudController}
import org.bowlerframework.model.{ModelValidatorBuilder, DefaultModelValidator}
import zn.w.postboard._

/**
 * This class acts as the starting point and bootstrap point for our application
 */
class Bootstrap {
  // parent layout, that uses a LayoutModel to enrich the layout based on request if needed.
  val parentLayout = Layout("default", None, new ParentLayoutModel)

  // this is a childLayout for parentLayout, and has the parent set on it, as shown.
  val composableLayout = Layout("child", Some(parentLayout))

  //You can define which layout to use based on chaining LayoutSelectors, for instance based on URL,
  // User-Agent or other factors.
  TemplateRegistry.appendLayoutSelectors(List(
    new UriLayoutSelector(composableLayout, new Regex("^.*/composable/.*$")),
    new DefaultLayoutSelector(parentLayout)))

  // Register the WidgetTransformer so that we can look up Widgets for pages by ID
  TransformerRegistry.registerTransformer(classOf[Widget], classOf[WidgetTransformer])

  // I think we're ready to start and instantiate our Controllers, first is a simple manually defined controller..
  val controller = new WidgetController
  // This controller is a Squeryl Crud Controller that deals with simple Crud operations on a "Person" entity object
  val peopleController = new CrudController[Person, Long](new SquerylController, new LongKeyedDao[Person](ApplicationSchema.people), "people")
 
  val postController = new PostController
  
  //// SECTION TO SETUP CONNECTION POOLING & DB FOR SQUERYL
  // Setup connection pooling for Squeryl with C3P0
  val cpds = new ComboPooledDataSource
  cpds.setDriverClass("org.h2.Driver")
  cpds.setJdbcUrl("jdbc:h2:mem:test")
  cpds.setUser("sa")
  cpds.setPassword("")

  cpds.setMinPoolSize(5)
  cpds.setAcquireIncrement(1)
  cpds.setMaxPoolSize(10)
  SessionFactory.concreteFactory = Some(() => connection)

  def connection = {
    Session.create(cpds.getConnection, new H2Adapter)
  }

  val session = SessionFactory.newSession
  session.bindToCurrentThread

  // this bit is to create the database schema used by Squeryl if it has not already been created.
  try {
    val writer = new StringWriter
    ApplicationSchema.printDdl(new PrintWriter(writer))
    // println(writer.toString)
    val con = session.connection
    con.createStatement.execute(writer.toString)
  } catch {
    case e: Exception => {
      println("database exists: " + e)
    }
  } finally {
    session.close
    session.unbindFromCurrentThread
  }

  // allow template reload during development - remove these lines in production for better performance
	org.bowlerframework.view.scalate.RenderEngine.getEngine.allowCaching = false
	org.bowlerframework.view.scalate.RenderEngine.getEngine.allowReload = true
}

/**
 * Transforms from single request parameters to an object, for instance in this case from a Widget ID
 * to an Actual Widget, or returns None if a transformation cannot be done.<br/>
 * Must be registered with the TransformerRegistry, as done in the bootstrap above.
 */
class WidgetTransformer extends StringValueTransformer[Widget] {
  def toValue(from: String): Option[Widget] = {
    val widget = Widgets.find(Integer.parseInt(from))
    if (widget == None)
      return None
    else
      return widget
  }
}

