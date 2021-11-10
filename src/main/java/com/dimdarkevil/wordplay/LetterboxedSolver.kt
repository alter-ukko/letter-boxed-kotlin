package com.dimdarkevil.wordplay

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.streams.toList
import kotlin.system.exitProcess

/**
 * Finds two-word solutions to any valid [New York Times Letterboxed puzzle](https://www.nytimes.com/puzzles/letter-boxed).
 *
 * A Letterboxed puzzle is a square with three unique (ASCII) letters per side. To solve it, you form
 * words by drawing lines between the letters on different sides. Your goal is to use up all the letters.
 * You can make multiple words, but each subsequent word must begin with the letter that the previous
 * word ended with. Words must be at least 3 letters long, and each letter in a word must move to a
 * different side of the puzzle.
 */
class LetterboxedSolver(val puzzle: String, val wordList: List<LetterboxedSolver.Wrd>) {
  // holds a normalized word and its letter bitmask
  data class Wrd(val strip: String, val mask: Long)
  // the sides of the puzzle as a list of strings
  private val sides = puzzle.lowercase().split(",").map { it.trim() }
    .apply{ validatePuzzle(this) }
  // the puzzle represented as a Wrd (so we can use its letter bitmask)
  private val whole = calcWrd(puzzle)
  // the subset of our dictionary that contains only valid, make-able words for this puzzle
  private val good = wordList.filter { fits(it) }

  companion object {
    // usage message
    private val usage = """
      usage: letterboxed {puzzle}
      where {puzzle} is in the format ABC,DEF,GHI,JKL
      example: ./letterboxed RKM,UIC,PHG,NAY 
    """.trimIndent()
    // static function to load the word list. We filter out 2-letter words as well as multi-word entries
    fun load() = BufferedReader(InputStreamReader(LetterboxedSolver::class.java.getResourceAsStream("/words.txt")!!)).use { rdr ->
      rdr.lines().filter { !it.contains(' ') && it.length > 2 }.map { calcWrd(it) }.toList()
    }

    // calculates a normalized word and its bitmask of letters
    private fun calcWrd(txt: String) : Wrd {
      val strip = txt.toLowerCase().filter { it.isLetter() }
      var mask = 0L
      strip.forEach { c -> mask = (mask or (1L shl (c.code-97))) }
      return Wrd(strip, mask)
    }

    @JvmStatic
    fun main(args: Array<String>) {
      if (args.isEmpty()) {
        println(usage)
        exitProcess(1)
      }
      var st = System.currentTimeMillis()
      val words = load()
      println("load took: ${System.currentTimeMillis()-st}ms")

      st = System.currentTimeMillis()
      val lst = try {
        LetterboxedSolver(args[0], words).solve()
      } catch (e : Exception) {
        println(e.message)
        exitProcess(1)
      }
      lst.forEach { println("$it") }
      println("calc took: ${System.currentTimeMillis()-st}ms")
    }
  }

  // finds two-word solutions to the puzzle
  fun solve() = good.flatMap { word -> findCombs(word, good) }

  // given a first valid word, finds a second one that starts with the letter the first word ends with,
  // and uses up any letters the first word didn't touch.
  private fun findCombs(word: Wrd, good: List<Wrd>) = good.mapNotNull { testWord ->
    if (testWord.strip.startsWith(word.strip[word.strip.lastIndex]) && ((word.mask or testWord.mask) and whole.mask) == whole.mask) {
      Pair(word.strip, testWord.strip)
    } else {
      null
    }
  }

  // returns true if the word is a valid, make-able word in the puzzle
  private fun fits(word: Wrd) : Boolean {
    if (word.strip.length < 3) return false
    if ((word.mask and whole.mask) != word.mask) return false
    (0 until word.strip.length-1).forEach { i ->
      if (getSide(word.strip[i]) == getSide(word.strip[i+1])) {
        return false
      }
    }
    return true
  }

  // returns a zero-based index for which side of the puzzle a letter lies on
  private fun getSide(let: Char) = sides.indexOfFirst { it.contains(let) }

  // validates that the puzzle provided is a correct letterboxed puzzle
  private fun validatePuzzle(parsedPuzzle: List<String>) {
    when {
      (parsedPuzzle.size != 4) -> "Puzzle must have 4 sides."
      (parsedPuzzle.any { it.length != 3 }) -> "Each side of the puzzle must have 3 letters."
      (parsedPuzzle.any { side -> side.any { !it.isLetter() }}) -> "Puzzle can consist only of letters and commas."
      (parsedPuzzle.joinToString("").toSet().size != 12) -> "Puzzle can't have repeating letters"
      else -> null
    }?.let {
      val invalid = "invalid puzzle: $puzzle"
      throw RuntimeException("${invalid}\n${it}\n${usage}")
    }
  }
}