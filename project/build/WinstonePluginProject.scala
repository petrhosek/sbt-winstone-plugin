import sbt._

class WinstonePluginProject(info: ProjectInfo) extends PluginProject(info) {
  override def managedStyle = ManagedStyle.Maven
  lazy val publishTo = Resolver.file("GitHub", new java.io.File("../petrh.github.com/m2/")) 
}
