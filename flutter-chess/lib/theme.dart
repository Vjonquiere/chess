import "package:flutter/material.dart";

class MaterialTheme {
  final TextTheme textTheme;

  const MaterialTheme(this.textTheme);

  static ColorScheme lightScheme() {
    return const ColorScheme(
      brightness: Brightness.light,
      primary: Color(0xff002f56),
      surfaceTint: Color(0xff2c6197),
      onPrimary: Color(0xffffffff),
      primaryContainer: Color(0xff00467b),
      onPrimaryContainer: Color(0xff85b5f1),
      secondary: Color(0xff4f6076),
      onSecondary: Color(0xffffffff),
      secondaryContainer: Color(0xffcfe1fc),
      onSecondaryContainer: Color(0xff53647b),
      tertiary: Color(0xff79573d),
      onTertiary: Color(0xffffffff),
      tertiaryContainer: Color(0xffb0896b),
      onTertiaryContainer: Color(0xff3e240d),
      error: Color(0xffba1a1a),
      onError: Color(0xffffffff),
      errorContainer: Color(0xffffdad6),
      onErrorContainer: Color(0xff93000a),
      surface: Color(0xfff9f9fe),
      onSurface: Color(0xff191c1f),
      onSurfaceVariant: Color(0xff42474f),
      outline: Color(0xff727780),
      outlineVariant: Color(0xffc2c7d1),
      shadow: Color(0xff000000),
      scrim: Color(0xff000000),
      inverseSurface: Color(0xff2e3034),
      inversePrimary: Color(0xffa0c9ff),
      primaryFixed: Color(0xffd2e4ff),
      onPrimaryFixed: Color(0xff001c37),
      primaryFixedDim: Color(0xffa0c9ff),
      onPrimaryFixedVariant: Color(0xff06497e),
      secondaryFixed: Color(0xffd2e4ff),
      onSecondaryFixed: Color(0xff0a1c30),
      secondaryFixedDim: Color(0xffb6c8e2),
      onSecondaryFixedVariant: Color(0xff37485e),
      tertiaryFixed: Color(0xffffdcc3),
      onTertiaryFixed: Color(0xff2d1603),
      tertiaryFixedDim: Color(0xffeabe9d),
      onTertiaryFixedVariant: Color(0xff5e4027),
      surfaceDim: Color(0xffd9dadf),
      surfaceBright: Color(0xfff9f9fe),
      surfaceContainerLowest: Color(0xffffffff),
      surfaceContainerLow: Color(0xfff3f3f8),
      surfaceContainer: Color(0xffededf3),
      surfaceContainerHigh: Color(0xffe7e8ed),
      surfaceContainerHighest: Color(0xffe2e2e7),
    );
  }

  ThemeData light() {
    return theme(lightScheme());
  }

  static ColorScheme lightMediumContrastScheme() {
    return const ColorScheme(
      brightness: Brightness.light,
      primary: Color(0xff002f56),
      surfaceTint: Color(0xff2c6197),
      onPrimary: Color(0xffffffff),
      primaryContainer: Color(0xff00467b),
      onPrimaryContainer: Color(0xffc7deff),
      secondary: Color(0xff26374c),
      onSecondary: Color(0xffffffff),
      secondaryContainer: Color(0xff5d6e86),
      onSecondaryContainer: Color(0xffffffff),
      tertiary: Color(0xff4c3018),
      onTertiary: Color(0xffffffff),
      tertiaryContainer: Color(0xff89664a),
      onTertiaryContainer: Color(0xffffffff),
      error: Color(0xff740006),
      onError: Color(0xffffffff),
      errorContainer: Color(0xffcf2c27),
      onErrorContainer: Color(0xffffffff),
      surface: Color(0xfff9f9fe),
      onSurface: Color(0xff0f1115),
      onSurfaceVariant: Color(0xff31363e),
      outline: Color(0xff4e535b),
      outlineVariant: Color(0xff686d76),
      shadow: Color(0xff000000),
      scrim: Color(0xff000000),
      inverseSurface: Color(0xff2e3034),
      inversePrimary: Color(0xffa0c9ff),
      primaryFixed: Color(0xff3d70a7),
      onPrimaryFixed: Color(0xffffffff),
      primaryFixedDim: Color(0xff1f578d),
      onPrimaryFixedVariant: Color(0xffffffff),
      secondaryFixed: Color(0xff5d6e86),
      onSecondaryFixed: Color(0xffffffff),
      secondaryFixedDim: Color(0xff45566c),
      onSecondaryFixedVariant: Color(0xffffffff),
      tertiaryFixed: Color(0xff89664a),
      onTertiaryFixed: Color(0xffffffff),
      tertiaryFixedDim: Color(0xff6e4e34),
      onTertiaryFixedVariant: Color(0xffffffff),
      surfaceDim: Color(0xffc5c6cb),
      surfaceBright: Color(0xfff9f9fe),
      surfaceContainerLowest: Color(0xffffffff),
      surfaceContainerLow: Color(0xfff3f3f8),
      surfaceContainer: Color(0xffe7e8ed),
      surfaceContainerHigh: Color(0xffdcdce2),
      surfaceContainerHighest: Color(0xffd1d1d6),
    );
  }

  ThemeData lightMediumContrast() {
    return theme(lightMediumContrastScheme());
  }

  static ColorScheme lightHighContrastScheme() {
    return const ColorScheme(
      brightness: Brightness.light,
      primary: Color(0xff002d53),
      surfaceTint: Color(0xff2c6197),
      onPrimary: Color(0xffffffff),
      primaryContainer: Color(0xff00467b),
      onPrimaryContainer: Color(0xffffffff),
      secondary: Color(0xff1c2d42),
      onSecondary: Color(0xffffffff),
      secondaryContainer: Color(0xff394a60),
      onSecondaryContainer: Color(0xffffffff),
      tertiary: Color(0xff40260f),
      onTertiary: Color(0xffffffff),
      tertiaryContainer: Color(0xff614229),
      onTertiaryContainer: Color(0xffffffff),
      error: Color(0xff600004),
      onError: Color(0xffffffff),
      errorContainer: Color(0xff98000a),
      onErrorContainer: Color(0xffffffff),
      surface: Color(0xfff9f9fe),
      onSurface: Color(0xff000000),
      onSurfaceVariant: Color(0xff000000),
      outline: Color(0xff272c34),
      outlineVariant: Color(0xff444952),
      shadow: Color(0xff000000),
      scrim: Color(0xff000000),
      inverseSurface: Color(0xff2e3034),
      inversePrimary: Color(0xffa0c9ff),
      primaryFixed: Color(0xff0b4b80),
      onPrimaryFixed: Color(0xffffffff),
      primaryFixedDim: Color(0xff00345e),
      onPrimaryFixedVariant: Color(0xffffffff),
      secondaryFixed: Color(0xff394a60),
      onSecondaryFixed: Color(0xffffffff),
      secondaryFixedDim: Color(0xff233449),
      onSecondaryFixedVariant: Color(0xffffffff),
      tertiaryFixed: Color(0xff614229),
      onTertiaryFixed: Color(0xffffffff),
      tertiaryFixedDim: Color(0xff482c15),
      onTertiaryFixedVariant: Color(0xffffffff),
      surfaceDim: Color(0xffb8b8bd),
      surfaceBright: Color(0xfff9f9fe),
      surfaceContainerLowest: Color(0xffffffff),
      surfaceContainerLow: Color(0xfff0f0f5),
      surfaceContainer: Color(0xffe2e2e7),
      surfaceContainerHigh: Color(0xffd3d4d9),
      surfaceContainerHighest: Color(0xffc5c6cb),
    );
  }

  ThemeData lightHighContrast() {
    return theme(lightHighContrastScheme());
  }

  static ColorScheme darkScheme() {
    return const ColorScheme(
      brightness: Brightness.dark,
      primary: Color(0xffa0c9ff),
      surfaceTint: Color(0xffa0c9ff),
      onPrimary: Color(0xff00325a),
      primaryContainer: Color(0xff00467b),
      onPrimaryContainer: Color(0xff85b5f1),
      secondary: Color(0xffb6c8e2),
      onSecondary: Color(0xff203146),
      secondaryContainer: Color(0xff37485e),
      onSecondaryContainer: Color(0xffa5b6d0),
      tertiary: Color(0xffeabe9d),
      onTertiary: Color(0xff452a13),
      tertiaryContainer: Color(0xffb0896b),
      onTertiaryContainer: Color(0xff3e240d),
      error: Color(0xffffb4ab),
      onError: Color(0xff690005),
      errorContainer: Color(0xff93000a),
      onErrorContainer: Color(0xffffdad6),
      surface: Color(0xff111317),
      onSurface: Color(0xffe2e2e7),
      onSurfaceVariant: Color(0xffc2c7d1),
      outline: Color(0xff8c919a),
      outlineVariant: Color(0xff42474f),
      shadow: Color(0xff000000),
      scrim: Color(0xff000000),
      inverseSurface: Color(0xffe2e2e7),
      inversePrimary: Color(0xff2c6197),
      primaryFixed: Color(0xffd2e4ff),
      onPrimaryFixed: Color(0xff001c37),
      primaryFixedDim: Color(0xffa0c9ff),
      onPrimaryFixedVariant: Color(0xff06497e),
      secondaryFixed: Color(0xffd2e4ff),
      onSecondaryFixed: Color(0xff0a1c30),
      secondaryFixedDim: Color(0xffb6c8e2),
      onSecondaryFixedVariant: Color(0xff37485e),
      tertiaryFixed: Color(0xffffdcc3),
      onTertiaryFixed: Color(0xff2d1603),
      tertiaryFixedDim: Color(0xffeabe9d),
      onTertiaryFixedVariant: Color(0xff5e4027),
      surfaceDim: Color(0xff111317),
      surfaceBright: Color(0xff37393d),
      surfaceContainerLowest: Color(0xff0c0e12),
      surfaceContainerLow: Color(0xff191c1f),
      surfaceContainer: Color(0xff1d2023),
      surfaceContainerHigh: Color(0xff282a2e),
      surfaceContainerHighest: Color(0xff333539),
    );
  }

  ThemeData dark() {
    return theme(darkScheme());
  }

  static ColorScheme darkMediumContrastScheme() {
    return const ColorScheme(
      brightness: Brightness.dark,
      primary: Color(0xffc8deff),
      surfaceTint: Color(0xffa0c9ff),
      onPrimary: Color(0xff002748),
      primaryContainer: Color(0xff6394cd),
      onPrimaryContainer: Color(0xff000000),
      secondary: Color(0xffccdef9),
      onSecondary: Color(0xff15273b),
      secondaryContainer: Color(0xff8192ab),
      onSecondaryContainer: Color(0xff000000),
      tertiary: Color(0xffffd4b4),
      onTertiary: Color(0xff392009),
      tertiaryContainer: Color(0xffb0896b),
      onTertiaryContainer: Color(0xff000000),
      error: Color(0xffffd2cc),
      onError: Color(0xff540003),
      errorContainer: Color(0xffff5449),
      onErrorContainer: Color(0xff000000),
      surface: Color(0xff111317),
      onSurface: Color(0xffffffff),
      onSurfaceVariant: Color(0xffd8dce7),
      outline: Color(0xffadb2bc),
      outlineVariant: Color(0xff8b909a),
      shadow: Color(0xff000000),
      scrim: Color(0xff000000),
      inverseSurface: Color(0xffe2e2e7),
      inversePrimary: Color(0xff084a7f),
      primaryFixed: Color(0xffd2e4ff),
      onPrimaryFixed: Color(0xff001226),
      primaryFixedDim: Color(0xffa0c9ff),
      onPrimaryFixedVariant: Color(0xff003863),
      secondaryFixed: Color(0xffd2e4ff),
      onSecondaryFixed: Color(0xff001225),
      secondaryFixedDim: Color(0xffb6c8e2),
      onSecondaryFixedVariant: Color(0xff26374c),
      tertiaryFixed: Color(0xffffdcc3),
      onTertiaryFixed: Color(0xff1f0c00),
      tertiaryFixedDim: Color(0xffeabe9d),
      onTertiaryFixedVariant: Color(0xff4c3018),
      surfaceDim: Color(0xff111317),
      surfaceBright: Color(0xff424449),
      surfaceContainerLowest: Color(0xff06070b),
      surfaceContainerLow: Color(0xff1b1e21),
      surfaceContainer: Color(0xff26282c),
      surfaceContainerHigh: Color(0xff303337),
      surfaceContainerHighest: Color(0xff3c3e42),
    );
  }

  ThemeData darkMediumContrast() {
    return theme(darkMediumContrastScheme());
  }

  static ColorScheme darkHighContrastScheme() {
    return const ColorScheme(
      brightness: Brightness.dark,
      primary: Color(0xffe9f0ff),
      surfaceTint: Color(0xffa0c9ff),
      onPrimary: Color(0xff000000),
      primaryContainer: Color(0xff99c6ff),
      onPrimaryContainer: Color(0xff000c1c),
      secondary: Color(0xffe9f0ff),
      onSecondary: Color(0xff000000),
      secondaryContainer: Color(0xffb2c4de),
      onSecondaryContainer: Color(0xff000c1c),
      tertiary: Color(0xffffede1),
      onTertiary: Color(0xff000000),
      tertiaryContainer: Color(0xffe6ba99),
      onTertiaryContainer: Color(0xff170700),
      error: Color(0xffffece9),
      onError: Color(0xff000000),
      errorContainer: Color(0xffffaea4),
      onErrorContainer: Color(0xff220001),
      surface: Color(0xff111317),
      onSurface: Color(0xffffffff),
      onSurfaceVariant: Color(0xffffffff),
      outline: Color(0xffecf0fb),
      outlineVariant: Color(0xffbec3cd),
      shadow: Color(0xff000000),
      scrim: Color(0xff000000),
      inverseSurface: Color(0xffe2e2e7),
      inversePrimary: Color(0xff084a7f),
      primaryFixed: Color(0xffd2e4ff),
      onPrimaryFixed: Color(0xff000000),
      primaryFixedDim: Color(0xffa0c9ff),
      onPrimaryFixedVariant: Color(0xff001226),
      secondaryFixed: Color(0xffd2e4ff),
      onSecondaryFixed: Color(0xff000000),
      secondaryFixedDim: Color(0xffb6c8e2),
      onSecondaryFixedVariant: Color(0xff001225),
      tertiaryFixed: Color(0xffffdcc3),
      onTertiaryFixed: Color(0xff000000),
      tertiaryFixedDim: Color(0xffeabe9d),
      onTertiaryFixedVariant: Color(0xff1f0c00),
      surfaceDim: Color(0xff111317),
      surfaceBright: Color(0xff4e5054),
      surfaceContainerLowest: Color(0xff000000),
      surfaceContainerLow: Color(0xff1d2023),
      surfaceContainer: Color(0xff2e3034),
      surfaceContainerHigh: Color(0xff393b40),
      surfaceContainerHighest: Color(0xff45474b),
    );
  }

  ThemeData darkHighContrast() {
    return theme(darkHighContrastScheme());
  }

  ThemeData theme(ColorScheme colorScheme) => ThemeData(
        useMaterial3: true,
        brightness: colorScheme.brightness,
        colorScheme: colorScheme,
        textTheme: textTheme.apply(
          bodyColor: colorScheme.onSurface,
          displayColor: colorScheme.onSurface,
        ),
        scaffoldBackgroundColor: colorScheme.background,
        canvasColor: colorScheme.surface,
      );

  /// Accent
  static const accent = ExtendedColor(
    seed: Color(0xff1d3557),
    value: Color(0xff1d3557),
    light: ColorFamily(
      color: Color(0xff031f41),
      onColor: Color(0xffffffff),
      colorContainer: Color(0xff1d3557),
      onColorContainer: Color(0xff879ec6),
    ),
    lightMediumContrast: ColorFamily(
      color: Color(0xff031f41),
      onColor: Color(0xffffffff),
      colorContainer: Color(0xff1d3557),
      onColorContainer: Color(0xff879ec6),
    ),
    lightHighContrast: ColorFamily(
      color: Color(0xff031f41),
      onColor: Color(0xffffffff),
      colorContainer: Color(0xff1d3557),
      onColorContainer: Color(0xff879ec6),
    ),
    dark: ColorFamily(
      color: Color(0xffb0c7f1),
      onColor: Color(0xff183153),
      colorContainer: Color(0xff1d3557),
      onColorContainer: Color(0xff879ec6),
    ),
    darkMediumContrast: ColorFamily(
      color: Color(0xffb0c7f1),
      onColor: Color(0xff183153),
      colorContainer: Color(0xff1d3557),
      onColorContainer: Color(0xff879ec6),
    ),
    darkHighContrast: ColorFamily(
      color: Color(0xffb0c7f1),
      onColor: Color(0xff183153),
      colorContainer: Color(0xff1d3557),
      onColorContainer: Color(0xff879ec6),
    ),
  );

  List<ExtendedColor> get extendedColors => [
        accent,
      ];
}

class ExtendedColor {
  final Color seed, value;
  final ColorFamily light;
  final ColorFamily lightHighContrast;
  final ColorFamily lightMediumContrast;
  final ColorFamily dark;
  final ColorFamily darkHighContrast;
  final ColorFamily darkMediumContrast;

  const ExtendedColor({
    required this.seed,
    required this.value,
    required this.light,
    required this.lightHighContrast,
    required this.lightMediumContrast,
    required this.dark,
    required this.darkHighContrast,
    required this.darkMediumContrast,
  });
}

class ColorFamily {
  const ColorFamily({
    required this.color,
    required this.onColor,
    required this.colorContainer,
    required this.onColorContainer,
  });

  final Color color;
  final Color onColor;
  final Color colorContainer;
  final Color onColorContainer;
}
