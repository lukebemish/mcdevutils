# MC Dev Utils

A general-purpose annotation processor and gradle plugin aimed at ease of development in multi-loader Minecraft mods.

## How it works

## Installing and Configuring

Add the following plugin to your project(s):

```gradle
plugins {
  id "io.github.lukebemish.mcdevutils" version "<version>"
}
```

Check the available versions at [https://plugins.gradle.org/plugin/io.github.lukebemish.mcdevutils](https://plugins.gradle.org/plugin/io.github.lukebemish.mcdevutils)

In each gradle project that you want the plugin applied to (if using the MultiLoader Template, this will be the `Common`,
`Forge`, `Fabric`, and `Quilt` projects), add the following to your buildscript:

```gradle
mcDevUtil {
    platform = '<platform>'
}
```

Where `<platform>` is one of `fabric`, `forge`, `common`, or `quilt`.

## Features

### Side-specific code verification

The `verifySidedCode` task is created by default with the plugin, though no other tasks are set to depend on it as it is
rather slow. This task scans the compiled code and dependencies in an attempt to find places where client-only or
dedicated-server-only code is referenced in common code without an annotated proxy. It will do nothing unless your code
is annotated with the `@CheckSide` annotation.
