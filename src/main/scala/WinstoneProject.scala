import sbt._

trait WinstoneProject extends DefaultWebProject {
  def assemblyOutputPath = outputPath / assemblyJarName
  def assemblyTemporaryPath = outputPath / "assembly"
  def assemblyJarName = artifactID + "-assembly-" + version + ".jar"

  def assemblyPaths(tempDir: Path) = {
    val jars = descendents(configurationPath(pack) ##, "*.jar")
    for (jar <- jars.get) FileUtilities.unzip(jar, tempDir, log).left.foreach(error)
    FileUtilities.copyFile(warPath, tempDir / "embedded.war", log)
    val base = (Path.lazyPathFinder(tempDir :: Nil) ##)
    (descendents(base, "*") --- (base / "META-INF" ** "*")).get
  }

  lazy val assembly = assemblyTask(assemblyTemporaryPath) dependsOn(`package`)
  def assemblyTask(tempDir: Path) = packageTask(Path.lazyPathFinder(assemblyPaths(tempDir)), assemblyOutputPath, packageOptions)

  val pack = config("pack") hide
  val winstone = "net.sourceforge.winstone" % "winstone" % "0.9.10" % "pack"

  override def mainClass = Some("winstone.Launcher")
}
