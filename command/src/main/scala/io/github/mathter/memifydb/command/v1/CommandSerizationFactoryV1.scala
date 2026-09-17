package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.CommandSerizationFactory
import io.github.mathter.memifydb.common.data.ValueSerelizationFactory

trait CommandSerizationFactoryV1 extends CommandSerizationFactory {
  def valueSerelizationFactory: ValueSerelizationFactory
}
