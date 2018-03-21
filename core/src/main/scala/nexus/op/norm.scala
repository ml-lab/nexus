package nexus.op

import nexus._
import nexus.algebra._
import nexus.algebra.syntax._

/**
 * Absolute value.
 * @author Tongfei Chen
 * @since 0.1.0
 */
object Abs extends PolyOp1 {

  implicit def absF[R](implicit R: IsReal[R]): F[R, R] = new F[R, R] {
    def name = "Abs"
    def tag(tx: Type[R]) = tx
    def forward(x: R) = R.abs(x)
    def backward(dy: R, y: R, x: R) = dy * R.sgn(x)
  }

  object Elementwise extends PolyOp1 {

    implicit def tensor[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], T[A]] = new F[T[A], T[A]] {
      def name = "Abs.Elementwise"
      def tag(tx: Type[T[A]]) = tx
      def forward(x: T[A]) = T.eAbs(x)
      def backward(dy: T[A], y: T[A], x: T[A]) = dy |*| T.eSgn(x)
    }

  }

}

object L1Norm extends PolyOp1 {

  implicit def l1NormF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], R] =
    new F[T[A], R] {
      def name = "L1Norm"
      def tag(tx: Type[T[A]]) = T.R
      def forward(x: T[A]) = T.sum(T.eAbs(x))
      def backward(dy: R, y: R, x: T[A]) = T.eSgn(x) :* dy
    }
}

object L2Norm extends PolyOp1 {

  implicit def l2NormF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], R] =
    new F[T[A], R] {
      def name = "L2Norm"
      def tag(tx: Type[T[A]]) = T.R
      def forward(x: T[A]) = T.R.sqrt(T.dot(x, x))
      def backward(dy: R, y: R, x: T[A]) = x :* T.R.div(dy, y)
    }
}

object LpNorm extends ParameterizedPolyOp1 {

  implicit def lpNormF[T[_], R, A](implicit T: IsRealTensorK[T, R]) = (p: Double) =>
      new F[T[A], R] {
        def name = s"LpNorm[$p]"
        def tag(tx: Type[T[A]]) = T.R
        def forward(x: T[A]) = ???
        def backward(dy: R, y: R, x: T[A]) = ???
      }

}


object L1Distance extends PolyModule2 {
  implicit def l1DistanceF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], T[A], R] = F {
    (x1, x2) => L1Norm(x1 - x2)
  }
}

object L2Distance extends PolyModule2 {
  implicit def l2DistanceF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], T[A], R] = F {
    (x1, x2) => L2Norm(x1 - x2)
  }
}

object L1Normalize extends PolyModule1 {
  implicit def l1NormalizeF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], T[A]] = F {
    x => Scale(Inv(L1Norm(x)), x)
  }
}

object L2Normalize extends PolyModule1 {
  implicit def l2NormalizeF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], T[A]] = F {
    x => Scale(Inv(L2Norm(x)), x)
  }
}

object CosineSimilarity extends PolyModule2 {
  implicit def cosineSimilarityF[T[_], R, A](implicit T: IsRealTensorK[T, R]): F[T[A], T[A], R] = F {
    (x1, x2) => Dot(x1, x2) / L2Norm(x1) / L2Norm(x2)
  }
}
