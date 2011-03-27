import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  //val templemoreRepo = "templemore sbt repo" at "http://templemore.co.uk/repo"
  //val cucumberPlugin = "templemore" % "cucumber-sbt-plugin" % "0.5.0"
	lazy val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"

}