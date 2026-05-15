package cz.krokviak.kalky.core.common.entities

import cz.krokviak.kalky.core.i18n.ProfileStrings

enum class Gender { MALE, FEMALE }

fun Gender.label(strings: ProfileStrings): String = when (this) {
    Gender.MALE -> strings.male
    Gender.FEMALE -> strings.female
}
