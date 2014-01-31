package sorm.core

import collection._, generic._

package object util {

  def memo [ a, z ] ( f : a => z ) = {
     // a WeakHashMap will release cache members if memory tightens
     val cache = new collection.mutable.WeakHashMap[a, z]
     x : a => synchronized { cache.getOrElseUpdate( x, f(x) ) }
  }

  object typeLevel {
    /**
     * Basically a bool with type-level values.
     */
    sealed trait Bool {
      val toBoolean: Boolean
    }
    object Bool {
      sealed trait True extends Bool
      sealed trait False extends Bool
      case object True extends True { val toBoolean = true }
      case object False extends False { val toBoolean = false }
    }

    object hlist {

      import shapeless._

      //  A fixed version with contravariant `hlist`
      trait Selector[ -hlist <: HList, item ] { def apply( hlist: hlist ): item }
      object Selector {
        implicit def head
          [ head, tail <: HList ]
          = new Selector[ head :: tail, head ] { def apply( hlist: head :: tail ) = hlist.head }
        implicit def tail
          [ head, tail <: HList, item ]
          ( implicit tailInstance: Selector[ tail, item ] )
          = new Selector[ head :: tail, item ] { def apply( hlist: head :: tail ) = tailInstance(hlist.tail) }
      }

    }

  }

  object reflection {
    import reflect.runtime.universe._
    def isTuple(t: Type) = t.typeSymbol.fullName.startsWith("scala.Tuple")
    def generic(t: Type, i: Int) = t.asInstanceOf[TypeRef].args(i)
    def name(s: Symbol) = s.name.decoded.trim
    def properties(t: Type) = t.members.toStream.filter(_.isTerm).filter(!_.isMethod).reverse
  }

  implicit class IterableLikeUtil[+A, +Repr]( val base: IterableLike[A, Repr] ) extends AnyVal {
    private def repr: Repr = base.asInstanceOf[Repr]
    def zipAllLazy[B, A1 >: A, That](that: GenIterable[B], thisElem: =>A1, thatElem: =>B)(implicit bf: CanBuildFrom[Repr, (A1, B), That]): That = {
      val b = bf(repr)
      val these = base.iterator
      val those = that.iterator
      while (these.hasNext && those.hasNext)
        b += ((these.next, those.next))
      while (these.hasNext) {
        b += ((these.next, thatElem))
      }
      while (those.hasNext) {
        b += ((thisElem, those.next))
      }
      b.result
    }
  }

}
