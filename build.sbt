Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / turbo                 := false
ThisBuild / watchTriggeredMessage := Watch.clearScreenOnTrigger

lazy val zioVersion = "1.0.9"

lazy val scala3Derivation =
  project
    .in(file("."))
    .settings(
      scalaVersion := "3.0.0",
      scalacOptions := Vector(
        "-feature",
        "-unchecked"
      )
    )
