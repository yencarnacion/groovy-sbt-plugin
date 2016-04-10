
sbtPlugin := true

name := "groovy-sbt-plugin"

organization := "com.webninjapr"

version := "0.1.0"


libraryDependencies += "org.codehaus.groovy" % "groovy-all" % "2.4.6"
libraryDependencies += "org.apache.ant" % "ant" % "1.9.6"


bintrayOrganization := Some("webninjapr")
bintrayRepository := "releases"
bintrayPackage := name.value
bintrayReleaseOnPublish := true
bintrayVcsUrl := Some("git@github.com:yencarnacion/groovy-sbt-plugin.git")
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

crossBuildingSettings
CrossBuilding.crossSbtVersions := Seq("0.12", "0.13")
CrossBuilding.scriptedSettings

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)
