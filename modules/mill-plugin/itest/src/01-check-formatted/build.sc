import $exec.plugins

import mill._
import mill.define.Command
import header._

object sut extends HeaderModule {
  def license: HeaderLicense = HeaderLicense.Apache2("2022", "lewisjkl")
}

object nodir extends HeaderModule {
  def license: HeaderLicense = HeaderLicense.Apache2("2022", "lewisjkl")
}

final case class FlatFile(path: os.Path, contents: String)

def flatFiles(p: os.Path): IndexedSeq[FlatFile] = {
  os.walk(p)
    .collect {
      case file if os.isFile(file) && file.toString.endsWith(".scala") =>
        val contents = os.read(file)
        FlatFile(file, contents)
    }
    .sortBy(_.path)
}

def fail(): Command[Unit] = T.command {
  sut.headerCheck()()
  ()
}

private val expectedHeader = """|/* Copyright 2022 lewisjkl
                                | *
                                | * Licensed under the Apache License, Version 2.0 (the "License");
                                | * you may not use this file except in compliance with the License.
                                | * You may obtain a copy of the License at
                                | *
                                | *     http://www.apache.org/licenses/LICENSE-2.0
                                | *
                                | * Unless required by applicable law or agreed to in writing, software
                                | * distributed under the License is distributed on an "AS IS" BASIS,
                                | * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                                | * See the License for the specific language governing permissions and
                                | * limitations under the License.
                                | */
                                |""".stripMargin

private def checkFlatFiles() = {
  val allFiles = flatFiles(super.millSourcePath)
  val withoutHeader = allFiles.filterNot(_.contents.startsWith(expectedHeader))
  if (withoutHeader.nonEmpty) {
    sys.error(s"Some files did not have header! $withoutHeader")
  }
}

def verify(): Command[Unit] = T.command {
  val expectedFailedPaths = List(
    os.rel / "test.scala",
    os.rel / "nested" / "badHeader.scala",
    os.rel / "nested" / "noHeader.scala"
  )
  val createResult = sut.headerCreate()()
  if (createResult.sorted != expectedFailedPaths)
    sys.error(
      s"Expected createResult '$expectedFailedPaths' but was '${createResult}'"
    )
  checkFlatFiles()
  sut.headerCheck()() // now should be fine
  nodir.headerCheck()()
  ()
}
