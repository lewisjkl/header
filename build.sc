import $ivy.`com.lihaoyi::mill-contrib-bloop:`
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.1.4`
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.0`
import io.github.davidgregory084.TpolecatModule
import de.tobiasroeser.mill.vcs.version.VcsVersion
import mill._
import mill.modules.Jvm
import mill.scalalib._
import mill.scalalib.publish._

trait BaseModule extends Module {
  def millSourcePath: os.Path = {
    val originalRelativePath = super.millSourcePath.relativeTo(os.pwd)
    os.pwd / "modules" / originalRelativePath
  }
}

trait BaseScalaModule
    extends TpolecatModule
    with BaseModule
    with mill.scalalib.bsp.ScalaMetalsSupport
    with PublishModule {
  def scalaVersion = T.input("2.13.8")
  def semanticDbVersion = T.input("4.4.34")

  def segmentsName = millModuleSegments.parts.mkString("-")

  def artifactName = {
    s"header-$segmentsName"
  }

  def publishVersion = VcsVersion.vcsState().format().dropWhile(_ == "v")

  def pomSettings = PomSettings(
    description = "Header Automation & Linting",
    organization = "com.lewisjkl",
    url = "https://github.com/lewisjkl/header",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl(Some("https://github.com/lewisjkl/header")),
    developers =
      Seq(Developer(id = "lewisjkl", name = "Jeff Lewis", url = "lewisjkl.com"))
  )
}

object Dependencies {
  val osLib = ivy"com.lihaoyi::os-lib:0.8.1"
  val mill = Agg(
    ivy"com.lihaoyi::mill-main:0.10.4",
    ivy"com.lihaoyi::mill-main-api:0.10.4"
  )
}

object core extends BaseScalaModule {
  def ivyDeps = Agg(
    Dependencies.osLib
  )
}

object `mill-plugin` extends BaseScalaModule {
  def ivyDeps = Dependencies.mill
  def moduleDeps = List(core)

  override def artifactSuffix: T[String] =
    s"_mill0.10_${artifactScalaVersion()}"
}
