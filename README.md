# Header

_Utilities for checking and updating headers in files._

A big thanks to [sbt-header](https://github.com/sbt/sbt-header) which this project is based on.

## Mill - Basic Usage

```console
> .mill __.headerCheck
> mill __.headerCreate
```

In `build.sc` file:

```scala
//| mvnDeps: ["com.lewisjkl::header-mill-plugin::0.1.0"] 
import header._
```

#### Apache 2

```scala
object core extends HeaderModule {
  override def license: HeaderLicense = HeaderLicense.Apache2("2023", "lewisjkl")
}
```

#### MIT

```scala
object core extends HeaderModule {
  override def license: HeaderLicense = HeaderLicense.MIT("2023", "lewisjkl")
}
```

#### Custom

```scala
object core extends HeaderModule {
  override def license: HeaderLicense = HeaderLicense.Custom(
  """|The contents of this file is free and unencumbered software released into the
     |public domain. For more information, please refer to <http://unlicense.org/>""".stripMargin
  )
}
```

## Mill Customization

```scala
import header._

object core extends HeaderModule {
  override def license: HeaderLicense = HeaderLicense.Apache2("2023", "lewisjkl")

  // defaults to List("scala")
  override def includeFileExtensions: List[String] = List("scala", "java")

  // if you want more control, you can use the following instead of `includeFileExtensions`
  // This shows the default implementation, but you can make it whatever you would like.
  override def skipFilePredicate: os.Path => Boolean = path => {
    os.isFile(path) && !includeFileExtensions.exists(ext =>
      path.segments.toList.last.endsWith(s".$ext")
    )
  }

  // defaults to this.moduleDir, change this to change where the header checking/creation starts
  // looking for files
  override def headerRootPath: os.Path = this.moduleDir
}
```
