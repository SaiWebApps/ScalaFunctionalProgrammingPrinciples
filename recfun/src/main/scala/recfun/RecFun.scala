package recfun

object RecFun extends RecFunInterface {
  def main(args: Array[String]): Unit = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(s"${pascal(col, row)} ")
      println()
    }
  }

  /**
   * Exercise 1: Get the value at (r, c) in Pascal's Triangle.
   */
  def pascal(c: Int, r: Int): Int =
    if (c > r) 0
    else if (c == 0 || r == 0) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)

  /**
   * Exercise 2: Determine whether or not the given list of characters has
   * balanced parentheses.
   */
  def balance(chars: List[Char]): Boolean = {
    def balanceHelper(numOpenParen: Int, chars: List[Char]): Boolean =
      if (chars.isEmpty) numOpenParen == 0
      else if (chars.head == ')' && numOpenParen <= 0) false
      else if (chars.head == ')') balanceHelper(numOpenParen - 1, chars.tail)
      else if (chars.head == '(') balanceHelper(numOpenParen + 1, chars.tail)
      else balanceHelper(numOpenParen, chars.tail)
  
    balanceHelper(0, chars)
  }

  /**
   * Exercise 3: Determine whether or not we can make the specified amount of
   * change/money from the given coins.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def countChangeHelper(money: Int, coins: List[Int]): Int =
      if (money == 0) 1
      else if (money < 0 || coins.isEmpty) 0
      else countChangeHelper(money - coins.head, coins) + countChangeHelper(money, coins.tail)

    if (money == 0) 0
    else countChangeHelper(money, coins)
  }
}
