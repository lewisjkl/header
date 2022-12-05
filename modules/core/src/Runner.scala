package header

object Runner {

  /** Check headers
    *
    * @param args
    * @return
    *   list of paths that are missing headers or contain the incorrect headers
    */
  def check(args: HeaderArgs): List[os.RelPath] = {
    walkIfExists(args.startPath, args.skip)
      .filter(os.isFile)
      .flatMap { path =>
        val contents = os.read(path)
        if (contents.startsWith(args.license.renderComment)) List.empty
        else List(path.relativeTo(args.startPath))
      }
      .toList
  }

  /** Create headers
    *
    * @param args
    * @return
    *   list of paths where headers were added
    */
  def create(args: HeaderArgs): List[os.RelPath] = {
    walkIfExists(args.startPath, args.skip)
      .filter(os.isFile)
      .flatMap { path =>
        val contents = os.read(path)
        if (contents.startsWith(args.license.renderComment)) List.empty
        else {
          os.write.over(path, s"${args.license.renderComment}\n\n$contents")
          List(path.relativeTo(args.startPath))
        }
      }
      .toList
  }

  private def walkIfExists(path: os.Path, skip: os.Path => Boolean) = {
    if (os.exists(path)) os.walk(path, skip)
    else IndexedSeq.empty
  }
}
