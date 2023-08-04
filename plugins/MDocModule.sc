import mill._
import mill.scalalib._
import mill.modules.Jvm
import os.Path

// adapted from https://github.com/atooni/mill-mdoc
trait MDocModule extends ScalaModule {

  def scalaMdocVersion: T[String] = T("2.3.7")

  def scalaMdocDep: T[Dep] = T(ivy"org.scalameta::mdoc:${scalaMdocVersion()}")

  def watchedMDocsDestination: T[Option[Path]] = T(None)

  override def ivyDeps = T {
    super.ivyDeps() ++ Agg(scalaMdocDep())
  }

  def mdocClasspath: T[Agg[PathRef]] = T {
    transitiveLocalClasspath() ++
      resources() ++
      localClasspath() ++
      unmanagedClasspath() ++ runClasspath()
  }

  // where do the mdoc sources live ?
  def mdocSources = T.sources { super.millSourcePath }

  def mdoc: T[PathRef] = T {

    val mdocCP = mdocClasspath().map(_.path)
    val mdocCPArg = mdocCP.iterator.mkString(":")

    val dir = T.dest.toIO.getAbsolutePath

    val dirParams = mdocSources()
      .map(pr => Seq(s"--in", pr.path.toIO.getAbsolutePath, "--out", dir))
      .iterator
      .flatten
      .toSeq

    val cpArgs = Seq("--classpath", mdocCPArg)

    mill.util.Jvm.runLocal("mdoc.Main", mdocCP, cpArgs ++ dirParams)

    PathRef(T.dest)
  }

}
