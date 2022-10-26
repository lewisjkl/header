import $exec.plugins

import mill._
import mill.define.Command
import header._

object sut extends HeaderModule {
  def license: HeaderLicense = HeaderLicense.Apache2("2022", "lewisjkl")
}

def verify(): Command[Unit] = T.command {
  val expectedFailedPaths = List(
    os.rel / "test.scala",
    os.rel / "nested" / "badHeader.scala",
    os.rel / "nested" / "noHeader.scala"
  )
  val result = sut.headerCreate()()
  if (result != expectedFailedPaths)
    sys.error(s"Expected '$expectedFailedPaths' but was '${result}'")
  ()
}
