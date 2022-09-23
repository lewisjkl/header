package header

import mill._
import mill.define.Command

trait HeaderModule extends mill.Module {

  def headerRootPath: os.Path = this.millSourcePath

  def includeFileExtensions: List[String] = List("scala")

  def skipFilePredicate: os.Path => Boolean = path => {
    os.isFile(path) && !includeFileExtensions.exists(ext =>
      path.segments.toList.last.endsWith(s".$ext")
    )
  }

  def license: HeaderLicense

  def args: HeaderArgs = HeaderArgs(license, headerRootPath, skipFilePredicate)

  def headerCheck(): Command[List[os.Path]] = T.command {
    val result = Runner.check(args)

    if (result.nonEmpty) {
      System.err.println(s"""|Missing or incorrect headers found in files:
                             |${result.mkString(
                              "    ",
                              "\n    ",
                              ""
                            )}""".stripMargin)
      mill.api.Result
        .Failure("Missing or incorrect headers found in files", Some(result))
    } else {
      System.out.println("All headers up to date.")
      mill.api.Result.Success(List.empty)
    }
  }

  def headerCreate(): Command[List[os.Path]] = T.command {
    val result = Runner.create(args)

    if (result.nonEmpty) {
      System.err.println(s"""|Created headers in files:
                             |${result.mkString(
                              "    ",
                              "\n    ",
                              ""
                            )}""".stripMargin)
    } else {
      System.err.println("All headers up to date.")
    }

    mill.api.Result.Success(result)
  }

}
