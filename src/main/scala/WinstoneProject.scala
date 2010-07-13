import sbt._

trait WinstoneProject extends DefaultWebProject {
  def embeddedOutputPath = outputPath / embeddedJarName
  def embeddedTemporaryPath = outputPath / "embedded"
  def embeddedJarName = artifactID + "-embedded-" + version + ".jar"

  def embeddedPaths(tempDir: Path) = {
    val jars = descendents(configurationPath(pack) ##, "*.jar")
    for (jar <- jars.get) FileUtilities.unzip(jar, tempDir, log).left.foreach(error)
    FileUtilities.copyFile(warPath, tempDir / "embedded.war", log)
    val base = (Path.lazyPathFinder(tempDir :: Nil) ##)
    (descendents(base, "*") --- (base / "META-INF" ** "*")).get
  }

  lazy val embed = embedTask(embeddedTemporaryPath) dependsOn(`package`)
  def embedTask(tempDir: Path) = packageTask(Path.lazyPathFinder(embeddedPaths(tempDir)), embeddedOutputPath, packageOptions)

  val pack = config("pack") hide
  val winstone = "net.sourceforge.winstone" % "winstone" % "0.9.10" % "pack"

  override def mainClass = Some("winstone.Launcher")
}
