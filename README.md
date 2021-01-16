# Assignments for Coursera's "Functional Programming in Scala"

## Table of Contents
* [Prerequistes](#prereqs)
* [Recursion](#recfun)
* [Functional Sets](#funsets)
* [Object-Oriented Sets](#objsets)

<a name="prereqs"></a>
## Prerequisites
Ensure that:
* JDK 11+ and SBT (Scala Build Tool) are installed on your system.
    * Run `java -version` to figure out if a JDK has been installed on your system, and if so, what version it is.
    * Run `sbt about` to verify that SBT is installed on your system.
* Within each assignment (each of which is a discrete directory with its own src/ and project/ folders):
    * **project/build.properties**'s `sbt.version` key has the correct SBT version (as gleaned from `sbt about` on your system). When downloaded from Coursera, this was initially 1.2.8, but my system's SBT version is 1.4.6.
    * To execute on VSCode with the Scala Metals plugin, disable the `io.get-coursier` plugin in **project/plugins.sbt**.
    * In **build.sbt**, change the Scala version to match the version of scala on your system (as determined via `sbt scalaVersion`) OR to whatever is in the warning message is thrown up by VSCode. I changed it from 2.13 to 2.13.4.

<a name="recfun"></a>
## Assignment 1: Recursion (`recfun`)

### Exercise 1: Pascal's Triangle
The following pattern of numbers is called Pascal’s triangle.
```
    1
   1 1
  1 2 1
 1 3 3 1
1 4 6 4 1
```
The numbers at the edge of the triangle are all 1, and each number inside the triangle is the sum of the two numbers above it. Write a function that computes the elements of Pascal’s triangle by means of a recursive process.

Do this exercise by implementing the pascal function in Main.scala, which takes a column c and a row r, counting from 0 and returns the number at that spot in the triangle. For example, pascal(0,2)=1,pascal(1,2)=2 and pascal(1,3)=3.

```scala
def pascal(c: Int, r: Int): Int
```

### Exercise 2: Parentheses Balancing
Write a recursive function which verifies the balancing of parentheses in a string, which we represent as a List[Char] not a String. For example, the function should return true for the following strings:

(if (zero? x) max (/ 1 x))
I told him (that it’s not (yet) done). (But he wasn’t listening)
The function should return false for the following strings:

:-)
())(
The last example shows that it’s not enough to verify that a string contains the same number of opening and closing parentheses.

Do this exercise by implementing the balance function in Main.scala. Its signature is as follows:
```scala
def balance(chars: List[Char]): Boolean
```

### Exercise 3: Counting Change
Write a recursive function that counts how many different ways you can make change for an amount, given a list of coin denominations. For example, there are 3 ways to give change for 4 if you have coins with denomination 1 and 2: 1+1+1+1, 1+1+2, 2+2.

Do this exercise by implementing the countChange function inMain.scala. This function takes an amount to change, and a list of unique denominations for the coins. Its signature is as follows:
```scala
def countChange(money: Int, coins: List[Int]): Int
```

Once again, you can make use of functions isEmpty, head and tail on the list of integers coins.

Hint: Think of the degenerate cases. How many ways can you give change for 0 CHF(swiss money)? How many ways can you give change for >0 CHF, if you have no coins?

<a name="funsets"></a>
## Assignment 2: Functional Sets (`funsets`)

### Context
We will work with sets of integers.

As an example to motivate our representation, how would you represent the set of all negative integers? You cannot list them all… one way would be to say: if you give me an integer, I can tell you whether it’s in the set or not: for 3, I say ‘no’; for -1, I say yes.

Mathematically, we call the function which takes an integer as argument and which returns a boolean indicating whether the given integer belongs to a set, the characteristic function of the set. For example, we can characterize the set of negative integers by the characteristic function (x: Int) => x < 0.

Therefore, we choose to represent a set by its characteristic function and define a type alias for this representation:
```scala
type Funset = Int => Boolean
```

Using this representation, we define a function that tests for the presence of a value in a set:
```scala
def contains(s: FunSet, elem: Int): Boolean = s(elem)
```

### Part 1: Basic Functions on Sets
Let’s start by implementing basic functions on sets.

1. Define a function which creates a singleton set from one integer value: the set represents the set of the one given element. Its signature is as follows:
```scala
def singletonSet(elem: Int): FunSet
```
2. Define the functions union,intersect, and diff, which takes two sets, and return, respectively, their union, intersection and differences. diff(s, t) returns a set which contains all the elements of the set s that are not in the set t. These functions have the following signatures:
```scala
def union(s: FunSet, t: FunSet): FunSet
def intersect(s: FunSet, t: FunSet): FunSet
def diff(s: FunSet, t: FunSet): FunSet
```
3. Define the function filter which selects only the elements of a set that are accepted by a given predicate p. The filtered elements are returned as a new set. The signature of filter is as follows:
```scala
def filter(s: FunSet, p: Int => Boolean): FunSet
```

### Part 2: Queries and Transformations on Sets
In this part, we are interested in functions used to make requests on elements of a set. 

1. The first function tests whether a given predicate is true for all elements of the set. This forall function has the following signature:
```scala
def forall(s: FunSet, p: Int => Boolean): Boolean
```
Note that there is no direct way to find which elements are in a set. contains only allows to know whether a given element is included. Thus, if we wish to do something to all elements of a set, then we have to iterate over all integers, testing each time whether it is included in the set, and if so, to do something with it. Here, we consider that an integer x has the property -1000 <= x <= 1000 in order to limit the search space.
2. Using forall, implement a function exists which tests whether a set contains at least one element for which the given predicate is true. Note that the functions forall and exists behave like the universal and existential quantifiers of first-order logic.
```scala
def exists(s: FunSet, p: Int => Boolean): Boolean
```
3. Finally, using forall or exists, write a function map which transforms a given set into another one by applying to each of its elements the given function. map has the following signature:
```scala
def map(s: FunSet, f: Int => Int): FunSet
```

<a name="objsets"></a>
## Object-Oriented Sets
### Part 1: Filtering
Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc. filter takes as argument a function, the predicate, which takes a tweet and returns a boolean. filter then returns the subset of all the tweets in the original set for which the predicate is true. For example, the following call:
```scala
tweets.filter(tweet => tweet.retweets > 10)
```
applied to a set tweets of two tweets, say, where the first tweet was not retweeted and the second tweet was retweeted 20 times should return a set containing only the second tweet.

Hint: start by defining the helper method filterAcc which takes an accumulator set as a second argument. This accumulator contains the ongoing result of the filtering.
```scala
def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet
```
The definition of filter in terms of filterAcc should then be straightforward.

### Part 2: Taking Unions
Implement union on tweet sets. Complete the stub for the method union. The method union takes another set that, and computes a new set which is the union of this and that, i.e. a set that contains exactly the elements that are either in this or in that, or in both.
```scala
def union(that: TweetSet): TweetSet
```
Note that in this exercise it is your task to find out in which class(es) to define the union method (should it be abstract in class TweetSet?).

Warning : This method is a crucial part of the assignment. There are many ways to correctly code it, however some implementations run in an exponential time, so be careful, an inefficient implementation might result in a timeout during the grading process.

### Part 3: Sorting Tweets By Their Influence
The more often a tweet is “re-tweeted” (that is, repeated by a different user with or without additions), the more influential it is.

The goal of this part of the exercise is to add a method descendingByRetweet to TweetSet which should produce a linear sequence of tweets (as an instance of class TweetList), ordered by their number of retweets:
```scala
def descendingByRetweet: TweetList
```
This method reflects a common pattern when transforming data structures. While traversing one data structure (in this case, a TweetSet), we’re building a second data structure (here, an instance of class TweetList). The idea is to start with the empty list Nil (containing no tweets), and to find the tweet with the most retweets in the input TweetSet. This tweet is removed from the TweetSet (that is, we obtain a new TweetSet that has all the tweets of the original set except for the tweet that was “removed”; this immutable set operation, remove, is already implemented for you), and added to the result list by creating a new Cons. After that, the process repeats itself, but now we are searching through a TweetSet with one less tweet.

Hint: start by implementing the method mostRetweeted which returns the most popular tweet of a TweetSet.

### Part 4: Tying everything together
In the last step of this assignment your task is to detect influential tweets in a set of recent tweets. We are providing you with a TweetSet containing several hundred tweets from popular tech news sites in the past few days, located in the TweetReader object (file “TweetReader.scala”).TweetReader.allTweets returns an instance of TweetSet containing a set of all available tweets.

Furthermore, you are given two lists of keywords. The first list corresponds to keywords associated with Google and Android smartphones, while the second list corresponds to keywords associated with Apple and iOS devices. Your objective is to detect which platform has generated more interest or activity in the past few days.

As a first step, use the functionality you implemented in the first parts of this assignment to create two different TweetSets, googleTweets andappleTweets. The first TweetSet, googleTweets, should contain all tweets that mention (in their “text”) one of the keywords in the google list. The second TweetSet, appleTweets, should contain all tweets that mention one of the keyword in the apple list. Their signature is as follows:
```scala
lazy val googleTweets: TweetSet
lazy val appleTweets: TweetSet
```

Hint: use the exists method of List and contains method of classjava.lang.String.

From the union of those two TweetSets, produce trending, an instance of class TweetList representing a sequence of tweets ordered by their number of retweets:
```scala
lazy val trending: TweetList
```
