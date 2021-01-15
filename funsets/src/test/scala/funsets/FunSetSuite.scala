package funsets

import org.junit._

class FunSetSuite {
  import FunSets._

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
  }

  @Rule def individualTestTimeout = new org.junit.rules.Timeout(10 * 1000)

  @Test
  def `contains is implemented`: Unit = {
    assert(contains(x => true, 100))
  }

  @Test
  def `singleton sets contain exactly one element`: Unit = {
    new TestSets {
      assert(contains(s1, 1), "Singleton set {1} should contain 1!")
      assert(contains(s2, 2), "Singleton set {2} should contain 2!")
      assert(contains(s3, 3), "Singleton set {3} should contain 3!")
    }
  }

  @Test
  def `union contains all elements of each set`: Unit = {
    new TestSets {
      val u12 = union(s1, s2)
      assert(contains(u12, 1), "{1} U {2} should contain 1!")
      assert(contains(u12, 2), "{1} U {2} should contain 2!")
      assert(!contains(u12, 3), "{1} U {2} should NOT contain 3!")

      val u13 = union(s1, s3)
      assert(contains(u13, 1), "{1} U {3} should contain 1!")
      assert(contains(u13, 3), "{1} U {3} should contain 3!")
      assert(!contains(u13, 2), "{1} U {3} should NOT contain 2!")

      val u23 = union(s2, s3)
      assert(contains(u23, 2), "{2} U {3} should contain 2!")
      assert(contains(u23, 3), "{2} U {3} should contain 3!")
      assert(!contains(u23, 1), "{2} U {3} should NOT contain 1!")

      val u123 = union(s1, u23)
      assert(contains(u123, 1), "{1} U {2} U {3} should contain 1!")
      assert(contains(u123, 2), "{1} U {2} U {3} should contain 2!")
      assert(contains(u123, 3), "{1} U {2} U {3} should contain 3!")
    }
  }

  @Test
  def `intersect/filter contains only elements shared between sets`: Unit = {
    new TestSets {
      val i123 = intersect(intersect(s1, s2), s3)
      val f123 = filter(filter(s1, s2), s3)
      assert(!contains(i123, 1) && !contains(f123, 1),
        "{1} ∩ {2} ∩ {3} should NOT contain 1!")
      assert(!contains(i123, 2) && !contains(f123, 2),
        "{1} ∩ {2} ∩ {3} should NOT contain 2!")
      assert(!contains(i123, 3) && !contains(f123, 3),
        "{1} ∩ {2} ∩ {3} should NOT contain 3!")

      val u12 = union(s1, s2)
      val u23 = union(s2, s3)
      val i1223 = intersect(u12, u23)
      val f1223 = filter(u12, u23)
      assert(contains(i1223, 2) && contains(f1223, 2),
        "{1, 2} ∩ {2, 3} should contain 2!")
      assert(!contains(i1223, 1) && !contains(f1223, 1),
        "{1, 2} ∩ {2, 3} should NOT contain 1!")
      assert(!contains(i1223, 3) && !contains(f1223, 3),
        "{1, 2} ∩ {2, 3} should NOT contain 3!")

      val u123 = union(u12, s3)
      val i23123 = intersect(u23, u123)
      val f23123 = filter(u23, u123)
      assert(contains(i23123, 2) && contains(f23123, 2),
        "{1, 2, 3} ∩ {2, 3} should contain 2!")
      assert(contains(i23123, 3) && contains(f23123, 3),
        "{1, 2, 3} ∩ {2, 3} should contain 3!")
      assert(!contains(i23123, 1) && !contains(f23123, 1),
        "{1, 2, 3} ∩ {2, 3} should NOT contain 1!")
    }
  }

  @Test
  def `diff contains only elements in one set but not the other`: Unit = {
    new TestSets {
      val d12 = diff(s1, s2)
      assert(contains(d12, 1), "{1} - {2} should contain 1!")
      assert(!contains(d12, 2), "{1} - {2} should NOT contain 2!")

      val d21 = diff(s2, s1)
      assert(contains(d21, 2), "{2} - {1} should contain 2!")
      assert(!contains(d21, 1), "{2} - {1} should NOT contain 1!")

      val u12 = union(s1, s2)
      val u123 = union(u12, s3)
      val d12312 = diff(u123, u12)
      assert(contains(d12312, 3), "{1, 2, 3} - {1, 2} should contain 3!")
      assert(!contains(d12312, 1), "{1, 2, 3} - {1, 2} should NOT contain 1!")
      assert(!contains(d12312, 2), "{1, 2, 3} - {1, 2} should NOT contain 2!")

      val d12123 = diff(u12, u123)
      assert(!contains(d12123, 1), "{1, 2} - {1, 2, 3} should NOT contain 1!")
      assert(!contains(d12123, 2), "{1, 2} - {1, 2, 3} should NOT contain 2!")
      assert(!contains(d12123, 3), "{1, 2} - {1, 2, 3} should NOT contain 3!")
    }
  }

  @Test
  def `forall works as expected`: Unit = {
    new TestSets {
      val u123 = union(union(s1, s2), s3)
      assert(forall(u123, (x: Int) => x >= 1 && x <= 3),
        "{1, 2, 3} should only contain values between 1 and 3 inclusive!")
      assert(!forall(u123, (x: Int) => x < 3),
        "{1, 2, 3} should contain an element (3) that is not less than 3!")
    }
  }

  @Test
  def `exists works as expected`: Unit = {
    new TestSets {
      val u123 = union(union(s1, s2), s3)
      assert(exists(u123, (x: Int) => x >= 1&& x <= 3),
        "{1, 2, 3} should contain at least 1 value between 1 and 3 inclusive!")
      assert(exists(u123, (x: Int) => x < 3),
        "{1, 2, 3} should contain at least 1 value strictly less than 3!")
      assert(!exists(u123, (x: Int) => x > 3),
        "{1, 2, 3} should NOT contain any values greater than 3!")
    }
  }

  @Test
  def `map transforms input set as specified`: Unit = {
    new TestSets {
      val u123 = union(union(s1, s2), s3)
      val t123 = map(u123, (x: Int) => x * 4)
      assert(contains(t123, 4), "Transformed {1, 2, 3} should contain 4!")
      assert(contains(t123, 8), "Transformed {1, 2, 3} should contain 8!")
      assert(contains(t123, 12), "Transformed {1, 2, 3} should contain 12!")
      assert(!contains(t123, 1), "Transformed {1, 2, 3} should NOT contain 1!")
      assert(!contains(t123, 2), "Transformed {1, 2, 3} should NOT contain 2!")
      assert(!contains(t123, 3), "Transformed {1, 2, 3} should NOT contain 3!")
    }
  }
}
