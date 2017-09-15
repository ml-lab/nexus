package nexus.func

import nexus._
import nexus.algebra._
import nexus.algebra.syntax._

@implicitNotFound("Cannot apply Sin to ${X}.")
trait SinF[X, Y] extends DOp1[X, Y] {
  def name = "Sin"
}

object SinF {

  implicit def scalar[R](implicit R: IsReal[R]): SinF[R, R] =
    new SinF[R, R] {
      import R._
      def tag = R
      def backward(dy: R, y: R, x: R) = dy * cos(x)
      def forward(x: R) = sin(x)
    }

}

@implicitNotFound("Cannot apply Sin.Elementwise to ${X}.")
trait ESinF[X, Y] extends DOp1[X, Y] {
  def name = "ESin"
}

object ESinF {

  implicit def tensor[T[_ <: $$], R, A <: $$](implicit T: IsTypedRealTensor[T, R]) = new ESinF[T[A], T[A]] {
    import T._
    def tag = T.ground[A]
    def forward(x: T[A]) = eSin(x)
    def backward(dy: T[A], y: T[A], x: T[A]) = dy |*| eCos(x)
  }

}

@implicitNotFound("Cannot apply Cos to ${X}.")
trait CosF[X, Y] extends DOp1[X, Y] {
  def name = "Cos"
}

object CosF {

  implicit def scalar[R](implicit R: IsReal[R]): CosF[R, R] =
    new CosF[R, R] {
      import R._
      def tag = R
      def backward(dy: R, y: R, x: R) = -dy * sin(x)
      def forward(x: R) = cos(x)
    }

}

@implicitNotFound("Cannot apply Cos.Elementwise to ${X}.")
trait ECosF[X, Y] extends DOp1[X, Y] {
  def name = "ECos"
}

object ECosF {

  implicit def tensor[T[_ <: $$], D, A <: $$](implicit T: IsTypedRealTensor[T, D]) = new ECosF[T[A], T[A]] {
    import T._
    def tag = T.ground[A]
    def forward(x: T[A]) = eCos(x)
    def backward(dy: T[A], y: T[A], x: T[A]) = -dy |*| eSin(x)
  }

}

//TODO: Tan, ETan, ATan2
//TODO: Sinh, Cosh, Tanh
//TODO: Arcsin, Arccos, Arctan
