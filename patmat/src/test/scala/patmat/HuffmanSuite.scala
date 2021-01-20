package patmat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout

class HuffmanSuite {
  import Huffman._

  trait TestTrees {
    val leaf1 = Leaf('a', 2)
    val leaf2 = Leaf('b', 3)
    val leaf3 = Leaf('d', 4)
    val tree1 = Fork(leaf1, leaf2, List('a', 'b'), 5)
    val tree2 = Fork(tree1, leaf3, List('a', 'b', 'd'), 9)

    val charsList = "bookkeeper".toList
    val charsTimes = List(('b', 1), ('o', 2), ('k', 2), ('e', 3), ('p', 1), ('r', 1))
    val sortedLeaves = List(Leaf('b', 1), Leaf('p', 1), Leaf('r', 1), Leaf('k', 2), Leaf('o', 2), Leaf('e', 3))
    val combined1 = List(
      Leaf('r', 1),
      Fork(Leaf('b', 1), Leaf('p', 1), List('b', 'p'), 2),
      Leaf('k', 2),
      Leaf('o', 2),
      Leaf('e', 3)
    )
    val combined2 = List(
      Leaf('k', 2),
      Leaf('o', 2),
      Fork(Leaf('r', 1), Fork(Leaf('b', 1), Leaf('p', 1), List('b', 'p'), 2), List('r', 'b', 'p'), 3),
      Leaf('e', 3)
    )
    val combined3 = List(
      Fork(Leaf('r', 1), Fork(Leaf('b', 1), Leaf('p', 1), List('b', 'p'), 2), List('r', 'b', 'p'), 3),
      Leaf('e', 3),
      Fork(Leaf('k', 2), Leaf('o', 2), List('k', 'o'), 4)
    )
    val combined4 = List(
      Fork(Leaf('k', 2), Leaf('o', 2), List('k', 'o'), 4),
      Fork(
        Fork(Leaf('r', 1), Fork(Leaf('b', 1), Leaf('p', 1), List('b', 'p'), 2), List('r', 'b', 'p'), 3),
        Leaf('e', 3),
        List('r', 'b', 'p', 'e'),
        6
      )
    )
    val combined5 = List(
      Fork(
        Fork(Leaf('k', 2), Leaf('o', 2), List('k', 'o'), 4),
        Fork(
          Fork(Leaf('r', 1), Fork(Leaf('b', 1), Leaf('p', 1), List('b', 'p'), 2), List('r', 'b', 'p'), 3),
          Leaf('e', 3),
          List('r', 'b', 'p', 'e'),
          6
        ),
        List('k', 'o', 'r', 'b', 'p', 'e'),
        10
      )
    )
    val tree3 = combined5.head
  }

  @Rule def individualTestTimeout = new Timeout(10 * 1000)

  @Test
  def `get weight of CodeTree`: Unit = new TestTrees {
    // Leaves
    assertEquals(2, weight(leaf1))
    assertEquals(3, weight(leaf2))
    assertEquals(4, weight(leaf3))
    // Forks
    assertEquals(5, weight(tree1))
    assertEquals(9, weight(tree2))
  }

  @Test
  def `get chars of CodeTree`: Unit = new TestTrees {
    // Leaves
    assertEquals(List('a'), chars(leaf1))
    assertEquals(List('b'), chars(leaf2))
    assertEquals(List('d'), chars(leaf3))
    // Forks
    assertEquals(List('a', 'b'), chars(tree1))
    assertEquals(List('a', 'b', 'd'), chars(tree2))
  }

  @Test
  def `string2chars "hello, world"`: Unit = assertEquals("hello, world".toList, string2Chars("hello, world"))

  @Test
  def `times gets frequency table for some input list of chars`: Unit = new TestTrees {
    assertEquals(charsTimes, times(charsList))
  }

  @Test
  def `make ordered leaf list for some frequency table`: Unit = new TestTrees {
    assertEquals(sortedLeaves, makeOrderedLeafList(charsTimes))
  }

  def `singleton detects if a List of CodeTrees has a single element`: Unit = new TestTrees {
    assertFalse(singleton(Nil))
    assertFalse(singleton(sortedLeaves))
    assertTrue(singleton(combined5))
  }

  @Test
  def `combine some leaf lists`: Unit = new TestTrees {
    val c1 = combine(sortedLeaves)
    assertEquals(combined1, c1)

    val c2 = combine(c1)
    assertEquals(combined2, c2)

    val c3 = combine(c2)
    assertEquals(combined3, c3)

    val c4 = combine(c3)
    assertEquals(combined4, c4)

    val c5 = combine(c4)
    assertEquals(combined5, c5)
  }

  @Test
  def `create code tree by combining leaves in list until only one remains`: Unit = new TestTrees {
    assertEquals(combined5, until(singleton, combine)(sortedLeaves))
    assertEquals(tree3, createCodeTree(charsList))
  }

  @Test
  def `decode and encode very short text should be identity`: Unit = new TestTrees {
    val cList1 = "ab".toList
    val encoded1 = encode(tree1)(cList1)
    assertEquals(List(0, 1), encoded1)
    assertEquals(cList1, decode(tree1, encoded1))

    val cList2 = "abba".toList
    val encoded2 = encode(tree1)(cList2)
    assertEquals(List(0, 1, 1, 0), encoded2)
    assertEquals(cList2, decode(tree1, encoded2))

    val cList3 = "abd".toList
    val encoded3 = encode(tree2)(cList3)
    assertEquals(List(0, 0, 0, 1, 1), encoded3)
    assertEquals(cList3, decode(tree2, encoded3))
  }

  @Test
  def `decode and encode longer text should be identity`: Unit = new TestTrees {
    val encoded = encode(tree3)(charsList)
    assertEquals(List(1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0), encoded)
    assertEquals(charsList, decode(tree3, encoded))
  }

  @Test
  def `decode and encode secret should be identity`: Unit = {
    assertEquals(secret, encode(frenchCode)("huffmanestcool".toList))
    assertEquals("huffmanestcool".toList, decodedSecret)
  }

  @Test
  def `convert builds CodeTable from given CodeTree`: Unit = new TestTrees {
    assertEquals(
      List(
        ('a', List(0)),
        ('b', List(1))
      ),
      convert(tree1)
    )
    assertEquals(
      List(
        ('a', List(0, 0)),
        ('b', List(0, 1)),
        ('d', List(1))
      ),
      convert(tree2)
    )
    assertEquals(
      List(
        ('k', List(0, 0)),
        ('o', List(0, 1)),
        ('r', List(1, 0, 0)),
        ('b', List(1, 0, 1, 0)),
        ('p', List(1, 0, 1, 1)),
        ('e', List(1, 1))
      ),
      convert(tree3)
    )
  }

  @Test
  def `quickEncode yields same results as encode but faster`: Unit = new TestTrees {
    assertEquals(secret, quickEncode(frenchCode)("huffmanestcool".toList))
  }
}
