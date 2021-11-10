## letter-boxed-kotlin

A simple program in [Kotlin](https://kotlinlang.org/) to produce two-word solutions to the NY Times [Letter-Boxed](http://nytimes.com/puzzles/letter-boxed) puzzles, given a puzzle and a word dictionary.

The puzzle is a square with three unique (ASCII) letters per side. The player's job is to draw lines from letter to letter to makes words, eventually completing the puzzle by using all twelve letters in the puzzle. There are a couple of constraints:

1. You can start on any letter. But after the first word, each word you make must start with the last letter of the previous word.
2. Each line you draw must be to a letter on a different side.

Here's an example puzzle:

```
  --R----K----M--
  |             |
  N             U
  |             |
  A             I
  |             |
  Y             C
  |             |
  --P----H----G--
```

In this puzzle, you could not make the word `PICK`, because `I` and `C` fall on the same side. If you made the word `PINKY`, the next word you made would have to start with `Y`.

### building

To build, execute the following from the repository root:

```
mvn package
tar xvf target/letterboxed.tgz
cd letterboxed
```

### usage

To solve the above puzzle, you could type this:

```
./letterboxed RKM,UIC,PHG,NAY
```
You'd get the following output:

```
load took: 96ms
(hacking, grumpy)
calc took: 29ms
```

### notes

* The program uses a kind of interesting "word mask" technique, where each letter present in a word is encoded as a binary digit. This allows really fast comparisons between words to see if they contain the same letters.
* The program handles normal four-sided letter-boxed puzzles, but it can also solve any puzzle with three or more sides.
* The program only looks for two-word solutions. In the actual puzzle on the NYT site, you can string together any number of words to use up the twelve letters (although there is always a possible two-word solution).
