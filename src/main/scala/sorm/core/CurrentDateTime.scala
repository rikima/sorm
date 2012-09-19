package sorm.core

import org.joda.time.DateTime
import sorm.jdbc._

trait CurrentDateTime {
  protected def connection : ConnectionAdapter
  private def fetchDate1() : DateTime
    = connection
        .executeQuery( Statement("SELECT NOW()") )
        .parseAndClose().head.head
        .asInstanceOf[DateTime]

  private lazy val deviation
    = System.currentTimeMillis - fetchDate1().getMillis

  def millis = System.currentTimeMillis - deviation
  /**
   * Current DateTime at DB server. Effectively fetches the date only once to
   * calculate the deviation.
   */
  def dateTime = new DateTime(millis)

}
