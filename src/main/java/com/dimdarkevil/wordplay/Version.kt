package com.dimdarkevil.wordplay

import java.util.Properties

object Version : Versioner()

/**
 * Class that parses a version.properties file to easily get the version.
 *
 * Make an object that extends this, and use its [version] property:
 *
 *     package com.example
 *     object Version: com.example.Version()
 */
open class Versioner {

  val properties:Properties by lazy {
    val cls = javaClass
    Properties().apply {
      cls.getResourceAsStream("version.properties")?.use { stream ->
        load(stream)
      }
    }
  }

  val version:String? by lazy {
    properties.getProperty("version")
  }

}