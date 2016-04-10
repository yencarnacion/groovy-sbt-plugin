package com.webninjapr.groovySbtPlugin

import sbt._
import sbt.Keys._
import java.io.File

import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.Javac
import org.codehaus.groovy.ant.Groovyc
import org.apache.tools.ant.types.Path

class GroovyC(val classpath : Seq[File], val sourceDirectory : File, val stubDirectory : File, val destinationDirectory : File) {

  def compile() : Unit =  {
    IO.createDirectory(sourceDirectory)
    IO.createDirectory(destinationDirectory)
    try{
      //Thread.currentThread.setContextClassLoader(classLoader)
      val project = new Project()
      val javac = new Javac()
      val groovyc = new Groovyc()

      val sourcePath = new Path(project)
      sourcePath.setLocation(sourceDirectory)

      groovyc.setClasspath(new Path(project, classpath.mkString(";")))
      groovyc.setSrcdir(sourcePath)
      groovyc.setStubdir(stubDirectory)
      groovyc.setDestdir(destinationDirectory)
      groovyc.setProject(project)
      groovyc.addConfiguredJavac(javac)
      groovyc.setKeepStubs(true)
      groovyc.setVerbose(true)
      groovyc.execute()
    }
    finally{
      //Thread.currentThread.setContextClassLoader(oldContextClassLoader)
    }
  }
}