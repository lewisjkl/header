package header

/** Arguments passed into Runner
  *
  * @param license
  *   Which one to use
  * @param startPath
  *   Where to start looking for files
  * @param skip
  *   Which files to skip along the way (returning true means file is skipped)
  */
final case class HeaderArgs(
    license: HeaderLicense,
    startPath: os.Path,
    skip: os.Path => Boolean
)
