package io.github.mathter.memifydb.command

class CommandException(message: String,
                       cause: Throwable,
                       enableSuppression: Boolean,
                       writableStackTrace: Boolean)
  extends Exception(message, cause, enableSuppression, writableStackTrace) {

}
