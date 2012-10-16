package sorm.samples

import sorm._
import core._
import jdbc._
import samples._
import save._
import structure._
import mapping._
import sext._
import Sorm._

object TestingInstance {

  def simulator
    ( entities : Entity* )
    = new Api {
        protected[sorm] val connection
          = new ConnectionAdapterSimulator()
              with SaveAdapter

        protected[sorm] val mappings
          = {
            val settings
              = entities.view
                  .map{ e =>
                    e.reflection -> EntitySettings(e.indexes, e.uniqueKeys)
                  }
                  .toMap

            settings.keys
              .zipBy{ new EntityMapping(None, _, settings) }
              .toMap
          }
      }
  def h2
    ( entities : Entity* )
    = new Instance( entities, "jdbc:h2:mem:test", initMode = InitMode.DropAllCreate )
  def mysql
    ( entities : Entity* )
    = new Instance( entities, "jdbc:mysql://localhost/test", initMode = InitMode.DropAllCreate )
  def sqlite
    ( entities : Entity* )
    = new Instance( entities, "jdbc:sqlite::memory:", initMode = InitMode.DropAllCreate )
  def hsql
    ( entities : Entity* )
    = new Instance( entities, "jdbc:hsqldb:mem:testdb", initMode = InitMode.DropCreate)

}