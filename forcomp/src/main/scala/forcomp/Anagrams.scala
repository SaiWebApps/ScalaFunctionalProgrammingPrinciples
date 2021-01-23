package forcomp

object Anagrams extends AnagramsInterface {
  type Word = String

  /** List of Words */
  type Sentence = List[Word]

  /**
   * `Occurrences` are a `List[(Char, Int)]`, where each (Char, Int) represents a character and the number of times
   * said character appears.
   * - This list is sorted alphabetically w.r.t. to the character in each pair.
   * - All characters in the occurrence list are lowercase.
   * - Any list of pairs of lowercase characters and their frequency which is not sorted is **not** an occurrence list.
   * - If the frequency of some character is zero, then that character should not be in the list.
   */
  type Occurrences = List[(Char, Int)]

  /**
   * Converts the word into its character occurrence list.
   * - The uppercase and lowercase version of the character are treated as the same character and are represented as a
   * lowercase character in the occurrence list.
   * - The output must be sorted alphabetically by character.
   */
  def wordOccurrences(w: Word): Occurrences = w.toLowerCase.groupBy(identity).view.mapValues(_.length).toList.sorted

  /** Converts a sentence into its character occurrence list. */
  def sentenceOccurrences(s: Sentence): Occurrences = wordOccurrences(s.mkString)

  val dictionary: Sentence = Dictionary.loadDictionary

  /**
   * The `dictionaryByOccurrences` is a `Map` from different occurrences to a sequence of all the words that have that
   * occurrence count. This map serves as an easy way to obtain all the anagrams of a word given its occurrence list.
   *
   * For example, the word "eat" has the character occurrence list `List(('a', 1), ('e', 1), ('t', 1))`. Incidentally,
   * so do the words "ate" and "tea". This means that the `dictionaryByOccurrences` map will contain an entry
   * `List(('a', 1), ('e', 1), ('t', 1)) -> Seq("ate", "eat", "tea")`.
   */
  lazy val dictionaryByOccurrences: Map[Occurrences, Sentence] = dictionary.groupBy(wordOccurrences)

  /**
   * If present, returns all anagrams for the given word from `dictionaryByOccurrences`. Otherwise, returns Nil.
   */
  def wordAnagrams(word: Word): Sentence = dictionaryByOccurrences.getOrElse(wordOccurrences(word), Nil)

  /**
   * Returns the list of all subsets of the occurrence list.
   * This includes the occurrence itself, i.e. `List(('k', 1), ('o', 1))` is a subset of `List(('k', 1), ('o', 1))`.
   * It also includes the empty subset `List()`.
   *
   * Example - The subsets of the occurrence list `List(('a', 2), ('b', 2))` are:
   *    List(
   *      List(),
   *      List(('a', 1)),
   *      List(('a', 2)),
   *      List(('b', 1)),
   *      List(('a', 1), ('b', 1)),
   *      List(('a', 2), ('b', 1)),
   *      List(('b', 2)),
   *      List(('a', 1), ('b', 2)),
   *      List(('a', 2), ('b', 2))
   *    )
   */
  def combinations(occurrences: Occurrences): List[Occurrences] = occurrences.foldLeft(List[Occurrences](Nil)) { 
    case (acc, (ch, count)) => acc ++ (for (i <- (1 to count).toList; occ <- acc) yield occ :+ (ch, i))
  }

  /**
   * Given that `y` is a subset of `x` (`y` only contains characters within `x`, and for each character, the count is
   * less than or equal to that in `x`), return the difference between `x` and `y`.
   * - Example: x = [('a', 4), ('b', 5), ('c', 6)]; y = [('a', 2), ('b', 5)]; result = [('a', 2), ('c', 6)]
   */
  def subtract(x: Occurrences, y: Occurrences): Occurrences = {
    val yMap: Map[Char, Int] = y.toMap
    x.foldLeft(List[(Char, Int)]()) { case (acc, (ch, count)) => acc :+ (ch, count - yMap.getOrElse(ch, 0)) }
      .filter { case (ch, count) => count > 0 }
  }

  /**
   * Returns a list of all anagram sentences of the given sentence.
   *
   * An anagram of a sentence is formed by taking the occurrences of all the characters of all the words in the
   * sentence, and producing all possible combinations of words with those characters, such that the words have to be
   * from the dictionary.
   *
   * The number of words in the sentence and its anagrams does not have to correspond. For example, the sentence
   * `List("I", "love", "you")` is an anagram of the sentence `List("You", "olive")`.
   *
   * Also, two sentences with the same words but in a different order are considered two different anagrams.
   * For example, sentences `List("You", "olive")` and `List("olive", "you")` are different anagrams of
   * `List("I", "love", "you")`.
   *
   * Here is a full example of a sentence `List("Yes", "man")` and its anagrams for our dictionary:
   *    List(
   *      List(en, as, my),
   *      List(en, my, as),
   *      List(man, yes),
   *      List(men, say),
   *      List(as, en, my),
   *      List(as, my, en),
   *      List(sane, my),
   *      List(Sean, my),
   *      List(my, en, as),
   *      List(my, as, en),
   *      List(my, sane),
   *      List(my, Sean),
   *      List(say, men),
   *      List(yes, man)
   *    )
   *
   * The different sentences do not have to be output in the order shown above - any order is fine as long as all the
   * anagrams are there. Every returned word has to exist in the dictionary.
   *
   * Note: In case that the words of the sentence are in the dictionary, then the sentence is the anagram of itself,
   * so it has to be returned in this list.
   *
   * Note: There is only one anagram of an empty sentence.
   */
  def sentenceAnagrams(sentence: Sentence): List[Sentence] = {
    def sentenceAnagramsHelper(occurrences: Occurrences): List[Sentence] = occurrences match {
      case Nil => List(Nil)
      case _ => for {
        occurrencesCombo <- combinations(occurrences)
        if dictionaryByOccurrences.contains(occurrencesCombo)
        word <- dictionaryByOccurrences(occurrencesCombo)
        sentence <- sentenceAnagramsHelper(subtract(occurrences, occurrencesCombo))
      } yield word :: sentence
    }

    sentenceAnagramsHelper(sentenceOccurrences(sentence))
  }
}

object Dictionary {
  def loadDictionary: List[String] = {
    val wordstream = Option {
      getClass.getResourceAsStream(List("forcomp", "linuxwords.txt").mkString("/", "/", ""))
    } getOrElse {
      sys.error("Could not load word list, dictionary file not found")
    }
    try {
      scala.io.Source.fromInputStream(wordstream).getLines().toList
    } catch {
      case e: Exception =>
        println("Could not load word list: " + e)
        throw e
    } finally {
      wordstream.close()
    }
  }
}
