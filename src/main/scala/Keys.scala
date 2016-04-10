package com.webninjapr.groovySbtPlugin

import sbt._
import sbt.Keys._
import java.io.File

trait Keys {

  lazy val Config = config("groovy") extend(Compile) hide
  lazy val groovyVersion = settingKey[String]("Groovy version")
  lazy val groovySource = settingKey[File]("Default Groovy source directory")
  lazy val groovyc = taskKey[Unit]("Compile Groovy sources")

}

trait TestKeys extends Keys {
  override lazy val Config = config("test-groovy") extend(Test) hide
}

trait IntegrationTestKeys extends TestKeys {
  override lazy val Config = config("it-groovy") extend (IntegrationTest) hide
}