package io.github.mathter.memifydb.command

class IllegalPrefixException(val prefix: Prefix)
  extends CommandException(s"Illegal command prefix: ${prefix}", null, false, false) {
}
