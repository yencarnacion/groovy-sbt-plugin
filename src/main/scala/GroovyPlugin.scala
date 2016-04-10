package com.webninjapr.groovySbtPlugin

import sbt._
import Keys._
import java.io.File

object GroovyPlugin extends Plugin {

  private object GroovyDefaults extends Keys {
    val settings = Seq(
      groovyVersion := "2.4.6",
      libraryDependencies ++= Seq[ModuleID](
        "org.codehaus.groovy" % "groovy-all" % groovyVersion.value % Config.name
      )
    )
  }

  object groovy extends Keys {
    val settings = Seq(ivyConfigurations += Config) ++ GroovyDefaults.settings ++ Seq(
      groovySource in Compile := (sourceDirectory in Compile).value / "groovy",
      unmanagedResourceDirectories in Compile += {(groovySource in Compile).value},
      groovyc in Compile := {
        val s: TaskStreams = streams.value
        val sourceDirectory : File = (groovySource in Compile).value
        val nb = (sourceDirectory ** "*.groovy").get.size
        if(nb > 0){
          val s: TaskStreams = streams.value
          s.log.info("Start Compiling Groovy sources")
          val classpath : Seq[File] = update.value.select( configurationFilter(name = "*") ) ++ Seq((classDirectory in Compile).value)
          val stubDirectory : File = (sourceManaged in Compile).value
          val destinationDirectory : File = (classDirectory in Compile).value

          def groovyClazz(file : File) : File = {
            val p = file.getAbsolutePath()
            new File(destinationDirectory.getAbsolutePath() + p.substring(sourceDirectory.getAbsolutePath().length(), p.length() - ".groovy".length()) + ".class")
          }

          (sourceDirectory ** "*.groovy").get map (groovyClazz) foreach {f => if(f.exists()){IO.delete(f)}}

          new GroovyC(classpath, sourceDirectory, stubDirectory, destinationDirectory).compile
        }
      },
      compile in Compile <<= (compile in Compile) dependsOn (groovyc in Compile)
    )
  }

  object testGroovy extends TestKeys {
    val settings = Seq(ivyConfigurations += Config) ++ inConfig(Config)(Defaults.testTasks ++ GroovyDefaults.settings ++ Seq(
      definedTests <<= definedTests in Test,
      definedTestNames <<= definedTestNames in Test,
      fullClasspath <<= fullClasspath in Test,

      groovySource in Test := (sourceDirectory in Test).value / "groovy",
      unmanagedResourceDirectories in Test += {(groovySource in Test).value},
      groovyc in Test := {
        val sourceDirectory : File = (groovySource in Test).value
        val nb = (sourceDirectory ** "*.clj").get.size
        if(nb > 0){
          val s: TaskStreams = streams.value
          s.log.info("Start Compiling Test Groovy sources")
          val classpath : Seq[File] = update.value.select( configurationFilter(name = "*") ) ++ Seq((classDirectory in Test).value) ++ Seq((classDirectory in Compile).value)
          val stubDirectory : File = (sourceManaged in Test).value
          val destinationDirectory : File = (classDirectory in Test).value

          def groovyClazz(file : File) : File = {
            val p = file.getAbsolutePath()
            new File(destinationDirectory.getAbsolutePath() + p.substring(sourceDirectory.getAbsolutePath().length(), p.length() - ".groovy".length()) + ".class")
          }

          (sourceDirectory ** "*.groovy").get map (groovyClazz) foreach {f => if(f.exists()){IO.delete(f)}}

          new GroovyC(classpath, sourceDirectory, stubDirectory, destinationDirectory).compile
        }
      },
      groovyc in Test <<= (groovyc in Test) dependsOn (compile in Test),
      test in Test <<= (test in Test) dependsOn (groovyc in Test)
    ))
  }
}