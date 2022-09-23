package header

object Runner {

  /** Check headers
    *
    * @param args
    * @return
    *   list of paths that are missing headers or contain the incorrect headers
    */
  def check(args: HeaderArgs): List[os.Path] = {
    os.walk(args.startPath, args.skip)
      .filter(os.isFile)
      .flatMap { path =>
        val contents = os.read(path)
        if (contents.startsWith(args.license.renderComment)) List.empty
        else List(path)
      }
      .toList
  }

  /** Create headers
    *
    * @param args
    * @return
    *   list of paths where headers were added
    */
  def create(args: HeaderArgs): List[os.Path] = {
    os.walk(args.startPath, args.skip)
      .filter(os.isFile)
      .flatMap { path =>
        val contents = os.read(path)
        if (contents.startsWith(args.license.renderComment)) List.empty
        else {
          os.write.over(path, s"${args.license.renderComment}\n\n$contents")
          List(path)
        }
      }
      .toList
  }
}
