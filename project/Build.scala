import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "addressManager"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "pdf" % "pdf_2.10" % "0.5",
    "commons-io" % "commons-io" % "2.4"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here 
    resolvers += Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns)
  )

}
