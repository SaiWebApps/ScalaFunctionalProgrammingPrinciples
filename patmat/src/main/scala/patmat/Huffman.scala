package patmat

/**
 * A Huffman code is represented by a binary tree.
 *
 * Every `Leaf` node of the tree represents one character of the alphabet that the tree can encode. The weight of a
 * `Leaf` is the frequency of appearance of
 * the character.
 *
 * The branches of the huffman tree, the `Fork` nodes, represent a set containing all the characters present in the
 * leaves below it. The weight of a `Fork` node is the sum of the weights of these leaves.
 */
abstract class CodeTree
case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
case class Leaf(char: Char, weight: Int) extends CodeTree

trait Huffman extends HuffmanInterface {
  def weight(tree: CodeTree): Int = tree match {
    case Fork(_, _, _, weight) => weight
    case Leaf(_, weight) => weight
  }

  def chars(tree: CodeTree): List[Char] = tree match {
    case Fork(_, _, chars, _) => chars
    case Leaf(char, _) => List(char)
  }

  def makeCodeTree(left: CodeTree, right: CodeTree) =
    Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))

  /**
   * In this assignment, we are working with lists of characters. This function allows you to easily create a character
   * list from a given string.
   */
  def string2Chars(str: String): List[Char] = str.toList

  /**
   * This function computes for each unique character in the list `chars` the number of times it occurs.
   * For example, the invocation `times(List('a', 'b', 'a'))` should return `List(('a', 2), ('b', 1))`. To make things
   * simpler, the resulting list's order should match the order in which unique characters occur within the word (e.g.,
   * in the example above, this order is ['a', 'b']).
   *
   * The type `List[(Char, Int)]` denotes a list of pairs, where each pair consists of a character and an integer.
   * Pairs can be constructed easily using parentheses: `val pair: (Char, Int) = ('c', 1)`.
   *
   * In order to access the two elements of a pair, you can use either the accessors `_1` and `_2`:
   *   val theChar = pair._1
   *   val theInt  = pair._2
   *
   * Or pattern matching:
   *   pair match {
   *     case (theChar, theInt) =>
   *       println("character is: "+ theChar)
   *       println("integer is  : "+ theInt)
   *   }
   */
  def times(chars: List[Char]): List[(Char, Int)] = {
    def distinct(chars: List[Char], acc: List[Char]): List[Char] = chars match {
      case Nil => acc
      case ch :: rem if acc.contains(ch) => distinct(rem, acc)
      case ch :: rem => distinct(rem, acc ::: List(ch))
    }

    def charTimes(char: Char, chars: List[Char], acc: Int): Int = chars match {
      case Nil => acc
      case ch :: rem if (char == ch) => charTimes(char, rem, acc + 1)
      case ch :: rem => charTimes(char, rem, acc)
    }

    def timesHelper(distinctChars: List[Char], acc: List[(Char, Int)]): List[(Char, Int)] = distinctChars match {
      case Nil => acc
      case ch :: rem => timesHelper(rem, acc ::: List((ch, charTimes(ch, chars, 0))))
    }

    timesHelper(distinct(chars, Nil), Nil)
  }

  /**
   * Given frequency table `freqs`, returns a List of `Leaf` nodes ordered by ascending weights (i.e., the list's head
   * should have the smallest weight) first and then by ascending character (i.e., if two `Leaf` nodes have the same
   * weight, then the node with the smaller character should come first).
   */
  def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] = {
    def insert(target: (Char, Int), freqs: List[(Char, Int)]): List[(Char, Int)] = freqs match {
      case Nil => List(target)
      case curr :: rem if (target._2 > curr._2 || (target._2 == curr._2 && target._1 > curr._1)) =>
        curr :: insert(target, rem)
      case freqs => target :: freqs
    }

    def insertionSort(freqs: List[(Char, Int)]): List[(Char, Int)] = freqs match {
      case Nil => Nil
      case curr :: rem => insert(curr, insertionSort(rem))
    }

    def toLeaves(freqs: List[(Char, Int)]): List[Leaf] = freqs match {
      case Nil => Nil
      case (char, weight) :: rem => Leaf(char, weight) :: toLeaves(rem)
    }

    toLeaves(insertionSort(freqs))
  }

  /**
   * Checks whether the list `trees` contains only one single code tree.
   */
  def singleton(trees: List[CodeTree]): Boolean = trees.size == 1

  /**
   * The parameter `trees` of this function is a list of code trees ordered by ascending weights.
   *
   * This function takes the first two elements of the list `trees` and combines them into a single `Fork` node.
   * This node is then added back into the remaining elements of `trees` at a position such that the ordering by
   * weights is preserved.
   *
   * If `trees` is a list of less than two elements, that list should be
   * returned unchanged.
   */
  def combine(trees: List[CodeTree]): List[CodeTree] = {
    def insert(tree: CodeTree, trees: List[CodeTree]): List[CodeTree] = trees match {
      case Nil => List(tree)
      case curr :: rem if (weight(tree) > weight(curr)) => curr :: insert(tree, rem)
      case trees => tree :: trees
    }

    trees match {
      case _ @ (x :: y :: rem) => insert(makeCodeTree(x, y), rem)
      case _ => trees
    }
  }

  /**
   * This function will be called in the following way:
   *
   *   until(singleton, combine)(trees)
   *
   * where `trees` is of type `List[CodeTree]`, `singleton` and `combine` refer to
   * the two functions defined above.
   *
   * In such an invocation, `until` should call the two functions until the list of
   * code trees contains only one single tree, and then return that singleton list.
   */
  def until(done: List[CodeTree] => Boolean, merge: List[CodeTree] => List[CodeTree])
      (trees: List[CodeTree]): List[CodeTree] =
    if (done(trees)) trees
    else until(done, merge)(merge(trees))

  /**
   * This function creates a code tree which is optimal to encode the text `chars`.
   *
   * The parameter `chars` is an arbitrary text. This function extracts the character
   * frequencies from that text and creates a code tree based on them.
   */
  def createCodeTree(chars: List[Char]): CodeTree = (until(singleton, combine)(makeOrderedLeafList(times(chars)))).head

  type Bit = Int

  /**
   * This function decodes the bit sequence `bits` using the code tree `tree` and returns
   * the resulting list of characters.
   */
  def decode(tree: CodeTree, bits: List[Bit]): List[Char] = {
    def decodeHelper(t: CodeTree, bits: List[Bit], acc: List[Char]): List[Char] = (t, bits) match {
      case (Leaf(char, _), Nil) => acc ::: List(char)
      case (Leaf(char, _), curr :: rem) => decodeHelper(tree, bits, acc ::: List(char))
      case (Fork(left, _, _, _), curr :: rem) if (curr == 0) => decodeHelper(left, rem, acc)
      case (Fork(_, right, _, _), curr :: rem) => decodeHelper(right, rem, acc)
      case _ => throw new IllegalArgumentException("Encountered unexpected case during Huffman tree decoding!")
    }

    decodeHelper(tree, bits, Nil)
  }

  /**
   * A Huffman coding tree for the French language, generated from the data at
   * http://fr.wikipedia.org/wiki/Fr%C3%A9quence_d%27apparition_des_lettres_en_fran%C3%A7ais
   */
  val frenchCode: CodeTree = Fork(
    Fork(
      Fork(
        Leaf('s', 121895),
        Fork(
          Leaf('d', 56269),
          Fork(
            Fork(
              Fork(Leaf('x', 5928), Leaf('j', 8351), List('x', 'j'), 14279),
              Leaf('f', 16351),
              List('x', 'j', 'f'),
              30630
            ),
            Fork(
              Fork(
                Fork(
                  Fork(
                    Leaf('z', 2093),
                    Fork(Leaf('k', 745), Leaf('w', 1747), List('k', 'w'), 2492),
                    List('z', 'k', 'w'),
                    4585
                  ),
                  Leaf('y', 4725),
                  List('z', 'k', 'w', 'y'),
                  9310
                ),
                Leaf('h', 11298),
                List('z', 'k', 'w', 'y', 'h'),
                20608
              ),
              Leaf('q', 20889),
              List('z', 'k', 'w', 'y', 'h', 'q'),
              41497
            ),
            List('x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q'),
            72127
          ),
          List('d','x','j','f','z','k','w','y','h','q'),
          128396
        ),
        List('s','d','x','j','f','z','k','w','y','h','q'),
        250291
      ),
      Fork(
        Fork(Leaf('o', 82762), Leaf('l', 83668), List('o', 'l'), 166430),
        Fork(
          Fork(Leaf('m', 45521), Leaf('p', 46335), List('m', 'p'), 91856),
          Leaf('u', 96785),
          List('m', 'p', 'u'),
          188641
        ),
        List('o', 'l', 'm', 'p', 'u'),
        355071
      ),
      List('s', 'd', 'x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q', 'o', 'l', 'm', 'p', 'u'),
      605362
    ),
    Fork(
      Fork(
        Fork(
          Leaf('r', 100500),
          Fork(
            Leaf('c', 50003),
            Fork(
              Leaf('v', 24975),
              Fork(Leaf('g', 13288), Leaf('b', 13822), List('g', 'b'), 27110),
              List('v','g','b'),
              52085
            ),
            List('c','v','g','b'),
            102088
          ),
          List('r', 'c', 'v', 'g', 'b'),
          202588
        ),
        Fork(Leaf('n', 108812), Leaf('t', 111103), List('n', 't'), 219915),
        List('r', 'c', 'v', 'g', 'b', 'n', 't'),
        422503
      ),
      Fork(
        Leaf('e', 225947),
        Fork(Leaf('i', 115465), Leaf('a', 117110), List('i', 'a'), 232575),
        List('e', 'i', 'a'),
        458522
      ),
      List('r', 'c', 'v', 'g', 'b', 'n', 't', 'e', 'i', 'a'),
      881025
    ),   
    List('s', 'd', 'x', 'j', 'f', 'z', 'k', 'w', 'y', 'h', 'q', 'o', 'l', 'm', 'p', 'u', 'r', 'c', 'v', 'g', 'b', 'n',
      't', 'e', 'i', 'a'),
    1486387
  )

  /**
   * What does the secret message say? Can you decode it?
   * For the decoding use the `frenchCode' Huffman tree defined above.
   */
  val secret: List[Bit] = List(0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1,
    1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1)

  /**
   * Write a function that returns the decoded secret
   */
  def decodedSecret: List[Char] = decode(frenchCode, secret)

  /**
   * This function encodes `text` using the code tree `tree`
   * into a sequence of bits.
   */
  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    def encodeHelper(t: CodeTree, text: List[Char], acc: List[Bit]): List[Bit] = text match {
      case Nil => acc
      case curr :: rem => t match {
        case Leaf(_, _) => encodeHelper(tree, rem, acc)
        case Fork(left, _, _, _) if (chars(left).contains(curr)) => encodeHelper(left, text, acc ::: List(0))
        case Fork(_, right, _, _) => encodeHelper(right, text, acc ::: List(1))
      }
    }

    encodeHelper(tree, text, Nil)
  }

  type CodeTable = List[(Char, List[Bit])]

  /**
   * This function returns the bit sequence that represents the character `char` in
   * the code table `table`.
   */
  def codeBits(table: CodeTable)(char: Char): List[Bit] = table match {
    case Nil => Nil
    case (ch, bits) :: _ if (ch == char) => bits
    case (ch, bits) :: rem => codeBits(rem)(char)
  }

  /**
   * Given a code tree, create a code table which contains, for every character in the
   * code tree, the sequence of bits representing that character.
   *
   * Hint: think of a recursive solution: every sub-tree of the code tree `tree` is itself
   * a valid code tree that can be represented as a code table. Using the code tables of the
   * sub-trees, think of how to build the code table for the entire tree.
   */
  def convert(tree: CodeTree): CodeTable = {
    def convertHelper(tree: CodeTree, bits: List[Bit], acc: CodeTable): CodeTable = tree match {
      case Leaf(char, _) => (char, bits) :: acc
      case Fork(left, right, _, _) =>
        mergeCodeTables(convertHelper(left, bits ::: List(0), acc), convertHelper(right, bits ::: List(1), acc))
    }

    convertHelper(tree, Nil, Nil)
  }

  /**
   * This function takes two code tables and merges them into one. Depending on how you
   * use it in the `convert` method above, this merge method might also do some transformations
   * on the two parameter code tables.
   */
  def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = a ::: b

  /**
   * This function encodes `text` according to the code tree `tree`.
   * To speed up the encoding process, it first converts the code tree to a code table
   * and then uses it to perform the actual encoding.
   */
  def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    def quickEncodeHelper(table: CodeTable, text: List[Char], acc: List[Bit]): List[Bit] = text match {
      case Nil => acc
      case ch :: rem => quickEncodeHelper(table, rem, acc ::: codeBits(table)(ch))
    }

    quickEncodeHelper(convert(tree), text, Nil)
  }
}

object Huffman extends Huffman
