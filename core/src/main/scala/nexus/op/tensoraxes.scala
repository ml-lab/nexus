package nexus.op

import nexus._
import nexus.algebra._
import nexus.algebra.typelevel._
import shapeless._

/**
 * Renaming an axis in any tensor.
 * @example {{{ Rename(U -> V) }}}
 * @author Tongfei Chen
 * @since 0.1.0
 */
object Rename extends ParameterizedPolyOp1 {

  implicit def renameF[T[_], E, A, U: Label, V: Label, B]
  (implicit r: Replace.Aux[A, U, V, B], T: IsTensorK[T, E]) = (uv: (U, V)) =>
    new F[T[A], T[B]] {
      val (u, v) = uv
      import T._
      def name = s"Rename[${typeName(u)} -> ${typeName(v)}]"
      def tag(tx: Type[T[A]]) = T.ground[B]
      def forward(x: T[A]) = typeWith[B](untype(x))
      def backward(dy: T[B], y: T[B], x: T[A]) = typeWith[A](untype(dy))
    }

}

object Concat extends ParameterizedPolyOp2 {

  implicit def concatF[T[_], E, A, U: Label, N <: Nat]
  (implicit n: IndexOf.Aux[A, U, N], T: IsTensorK[T, E]) = (u: U) =>
    new F[T[A], T[A], T[A]] {
        def name = s"Concat[${typeName(u)}]"
        def tag(tx1: Type[T[A]], tx2: Type[T[A]]) = T.ground[A] // TODO: size-aware
        def forward(x1: T[A], x2: T[A]) = ???
        def backward1(dy: T[A], y: T[A], x1: T[A], x2: T[A]) = ???
        def backward2(dy: T[A], y: T[A], x1: T[A], x2: T[A]) = ???
    }

}


/**
 * @see `tf.expand_dims`, `torch.unsqueeze`
 */
object ExpandDim extends ParameterizedPolyOp1 {

  implicit def expandDimF[T[_], E, A, N <: Nat, U: Label, B]
  (implicit ia: InsertAt.Aux[A, N, U, B], T: IsTensorK[T, E]) = (nu: (N, U)) =>
    new F[T[A], T[B]] {
        val (n, u) = nu
        def name = s"ExpandDim[$n -> ${typeName(u)}]"
        def tag(tx1: Type[T[A]]) = T.ground[B]
        def forward(x: T[A]) = ???
        def backward(dy: T[B], y: T[B], x: T[A]) = ???
    }

}

/**
 * @see `tf.squeeze`; `torch.squeeze`
 */
object Squeeze extends ParameterizedPolyOp1 {

  implicit def squeezeF[T[_], E, A, N <: Nat, U: Label, B]
  (implicit ix: IndexOf.Aux[A, U, N], rx: RemoveAt.Aux[A, N, B], T: IsTensorK[T, E]) = (u: U) =>
    new F[T[A], T[B]] {
        def name = s"Squeeze[${typeName(u)}]"
        def tag(tx: Type[T[A]]) = T.ground[B]
        def forward(x: T[A]) = ???
        def backward(dy: T[B], y: T[B], x: T[A]) = ???
    }

}

/**
 * @example {{{ a |> SwapAxes(Batch, Length) }}}
 */
object SwapAxes extends ParameterizedPolyOp1

/**
 * @example {{{ a |> MergeAxes((Layer, Embedding) -> Embedding) }}}
 */
object MergeAxes extends ParameterizedPolyOp1 {

  implicit def mergeAxesF[T[_], E, A, U: Label, V: Label, W: Label, B]
  (implicit T: IsTensorK[T, E]) = (uvw: ((U, V), W)) => new F[T[A], T[B]] {
    def tag(tx: Type[T[A]]) = ???
    def forward(x: T[A]) = ???
    def backward(dy: T[B], y: T[B], x: T[A]) = ???
    def name = ???
  }

}

/**
 * Unpacks a tensor along a specific axis, generating a sequence of tensors with one dimension less.
 * @see `tf.unstack`; `torch.unbind`
 * @example {{{
 *    val sentence: FloatTensor[(Length, Embedding)]
 *    sentence |> UnstackAlong(Length) // Seq[FloatTensor[Embedding]]
 * }}}
 * @author Tongfei Chen
 * @since 0.1.0
 */
object UnstackAlong extends ParameterizedPolyOp1 {

  implicit def unstackAlongF[T[_], E, A, X: Label, N <: Nat, B]
  (implicit T: IsTensorK[T, E], ix: IndexOf.Aux[A, X, N], r: RemoveAt.Aux[A, N, B]): X => F[T[A], Seq[T[B]]] =
    (x: X) => new F[T[A], Seq[T[B]]] {
      def name = s"UnstackAlong[${typeName(x)}]"
      def tag(tx: Type[T[A]]) = ???
      def forward(x: T[A]) = ???
      def backward(dy: Seq[T[B]], y: Seq[T[B]], x: T[A]) = ???
    }

}

/**
 * Given a sequence of tensors of the same shape, stacks them to a new tensor with one more dimension.
 * @see `tf.stack`; `torch.stack`
 * @author Tongfei Chen
 * @since 0.1.0
 */
object Stack extends ParameterizedPolyOp1 {

  implicit def stackF[T[_], E, A, X: Label, B]
  (implicit T: IsTensorK[T, E], ia: InsertAt.Aux[A, _0, X, B]): X => F[Seq[T[A]], T[B]] =
    (x: X) => new F[Seq[T[A]], T[B]] {
      def name = s"Stack[${typeName(x)}]"
      def tag(tx: Type[Seq[T[A]]]) = ???
      def forward(x: Seq[T[A]]) = ???
      def backward(dy: T[B], y: T[B], x: Seq[T[A]]) = ???
    }
}
