// Copyright (c) 2024. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.jvm

import com.autonomousapps.fixtures.*
import com.autonomousapps.model.Advice
import com.autonomousapps.model.PluginAdvice

import static com.autonomousapps.utils.Runner.build
import static com.google.common.truth.Truth.assertThat

final class JvmSpec extends AbstractJvmSpec {

  private ProjectDirProvider javaLibraryProject = null

  def "reports redundant kotlin-jvm and kapt plugins applied (#gradleVersion)"() {
    given:
    javaLibraryProject = new RedundantKotlinJvmAndKaptPluginsProject()

    when:
    build(gradleVersion, javaLibraryProject, 'buildHealth')

    then:
    Set<PluginAdvice> actualAdvice = javaLibraryProject.buildHealthFor(":").first().pluginAdvice
    def expectedAdvice = RedundantKotlinJvmAndKaptPluginsProject.expectedAdvice().first().pluginAdvice
    assertThat(actualAdvice).containsExactlyElementsIn(expectedAdvice)

    where:
    gradleVersion << gradleVersions()
  }

  def "reports redundant kotlin-jvm plugin applied (#gradleVersion)"() {
    given:
    javaLibraryProject = new RedundantKotlinJvmPluginProject()

    when:
    build(gradleVersion, javaLibraryProject, 'buildHealth')

    then:
    Set<PluginAdvice> actualAdvice = javaLibraryProject.buildHealthFor(":").first().pluginAdvice
    assertThat(actualAdvice)
      .containsExactlyElementsIn(RedundantKotlinJvmPluginProject.expectedAdvice().first().pluginAdvice)

    where:
    gradleVersion << gradleVersions()
  }

  def "does not report kotlin-jvm as redundant (#gradleVersion)"() {
    given:
    javaLibraryProject = new RedundantKotlinJvmPluginProject(true)

    when:
    build(gradleVersion, javaLibraryProject, 'buildHealth')

    then:
    Set<PluginAdvice> actualAdvice = javaLibraryProject.buildHealthFor(":").first().pluginAdvice
    assertThat(actualAdvice).isEmpty()

    where:
    gradleVersion << gradleVersions()
  }

  def "autoservice is used with annotationProcessor (#gradleVersion)"() {
    given:
    javaLibraryProject = new JvmAutoServiceProject()

    when:
    build(gradleVersion, javaLibraryProject, 'buildHealth')

    then:
    Set<Advice> actualAdvice = javaLibraryProject.adviceFor(":")
    assertThat(actualAdvice).containsExactlyElementsIn(JvmAutoServiceProject.expectedAdvice())

    where:
    gradleVersion << gradleVersions()
  }

  def "dagger is unused with annotationProcessor (#gradleVersion)"() {
    given:
    javaLibraryProject = new JvmDaggerProject()

    when:
    build(gradleVersion, javaLibraryProject, 'buildHealth')

    then:
    Set<Advice> actualAdvice = javaLibraryProject.adviceFor(":")
    assertThat(actualAdvice).containsExactlyElementsIn(JvmDaggerProject.expectedAdvice())

    where:
    gradleVersion << gradleVersions()
  }

  def "root projects can contain source (#gradleVersion)"() {
    given:
    javaLibraryProject = new SingleProject()

    when:
    build(gradleVersion, javaLibraryProject, 'buildHealth')

    then:
    Set<Advice> actualAdvice = javaLibraryProject.adviceFor(":")
    assertThat(actualAdvice).containsExactlyElementsIn(SingleProject.expectedAdvice())

    where:
    gradleVersion << gradleVersions()
  }
}
