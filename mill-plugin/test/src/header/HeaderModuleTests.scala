package header

import mill._
import mill.api.Discover
import mill.testkit.{TestRootModule, UnitTester}
import mill.util.TokenReaders._
import utest._

object HeaderModuleTests extends TestSuite {

  object build extends TestRootModule {
    object sut extends HeaderModule {
      def license: HeaderLicense = HeaderLicense.Apache2("2022", "lewisjkl")
    }
    object nodir extends HeaderModule {
      def license: HeaderLicense = HeaderLicense.Apache2("2022", "lewisjkl")
    }
    lazy val millDiscover = Discover[this.type]
  }

  def tests: Tests = Tests {
    test("headerCreate and headerCheck") {
      val resourceFolder = os.Path(sys.env("MILL_TEST_RESOURCE_DIR"))
      UnitTester(build, resourceFolder / "check-formatted").scoped { eval =>
        val expectedFailedPaths = List(
          os.rel / "test.scala",
          os.rel / "nested" / "badHeader.scala",
          os.rel / "nested" / "noHeader.scala"
        )

        // headerCreate should add headers to files missing them
        val Right(createResult) = eval(build.sut.headerCreate()): @unchecked
        assert(createResult.value.sorted == expectedFailedPaths)

        // after creation, headerCheck should pass
        val Right(checkResult) = eval(build.sut.headerCheck()): @unchecked
        assert(checkResult.value.isEmpty)

        // nodir module has no source directory, should pass
        val Right(nodirResult) = eval(build.nodir.headerCheck()): @unchecked
        assert(nodirResult.value.isEmpty)
      }
    }
  }
}