Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / turbo := true
ThisBuild / watchTriggeredMessage := Watch.clearScreenOnTrigger

lazy val zioVersion = "1.0.4-2"

lazy val scala3Derivation =
  project
    .in(file("."))
    .settings(
      scalaVersion := "3.0.0-RC1",
      scalacOptions := Vector(
        "-feature",
        "-unchecked"
      )
    )
