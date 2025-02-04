import $file.plugins.MDocModule
import MDocModule.{MDocModule => MDoc}
import $ivy.`com.lihaoyi::mill-contrib-bloop:`
import $ivy.`io.chris-kipp::mill-ci-release::0.3.0`
import $ivy.`io.github.davidgregory084::mill-tpolecat::0.3.5`
import $ivy.`de.tototec::de.tobiasroeser.mill.integrationtest::0.7.1`
import de.tobiasroeser.mill.integrationtest._
import io.github.davidgregory084.TpolecatModule
import io.kipp.mill.ci.release.CiReleaseModule
import mill.scalalib.scalafmt.ScalafmtModule
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
    with ScalafmtModule
    with CiReleaseModule {
  def scalaVersion = T.input("2.13.16")

  def segmentsName = millModuleSegments.parts.mkString("-")

  def artifactName = {
    s"header-$segmentsName"
  }

  def pomSettings = PomSettings(
    description = "Header Automation & Linting",
    organization = "com.lewisjkl",
    url = "https://github.com/lewisjkl/header",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl(Some("https://github.com/lewisjkl/header")),
    developers =
      Seq(Developer(id = "lewisjkl", name = "Jeff Lewis", url = "lewisjkl.com"))
  )

  // Copied from lefou's plugins to keep compatible with older java versions
  override def javacOptions = {
    (if (scala.util.Properties.isJavaAtLeast(9)) Seq("--release", "8")
     else Seq("-source", "1.8", "-target", "1.8")) ++
      Seq("-encoding", "UTF-8", "-deprecation")
  }
}

object Dependencies {
  val osLib = ivy"com.lihaoyi::os-lib:0.11.3"
  val mill = Agg(
    ivy"com.lihaoyi::mill-main:0.12.7",
    ivy"com.lihaoyi::mill-main-api:0.12.7"
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
    s"_mill0.11_${artifactScalaVersion()}"

  object itest extends MillIntegrationTestModule {
    def millTestVersion = "0.11.13"
    def pluginsUnderTest = Seq(`mill-plugin`)
  }
}

object readme extends BaseScalaModule with MDoc {
  override def mdocSources = T.sources { T.workspace / "README.md" }
  override def moduleDeps = List(`mill-plugin`, core)
}
