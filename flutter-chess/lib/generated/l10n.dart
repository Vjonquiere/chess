// GENERATED CODE - DO NOT MODIFY BY HAND
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'intl/messages_all.dart';

// **************************************************************************
// Generator: Flutter Intl IDE plugin
// Made by Localizely
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, lines_longer_than_80_chars
// ignore_for_file: join_return_with_assignment, prefer_final_in_for_each
// ignore_for_file: avoid_redundant_argument_values, avoid_escaping_inner_quotes

class S {
  S();

  static S? _current;

  static S get current {
    assert(
      _current != null,
      'No instance of S was loaded. Try to initialize the S delegate before accessing S.current.',
    );
    return _current!;
  }

  static const AppLocalizationDelegate delegate = AppLocalizationDelegate();

  static Future<S> load(Locale locale) {
    final name = (locale.countryCode?.isEmpty ?? false)
        ? locale.languageCode
        : locale.toString();
    final localeName = Intl.canonicalizedLocale(name);
    return initializeMessages(localeName).then((_) {
      Intl.defaultLocale = localeName;
      final instance = S();
      S._current = instance;

      return instance;
    });
  }

  static S of(BuildContext context) {
    final instance = S.maybeOf(context);
    assert(
      instance != null,
      'No instance of S present in the widget tree. Did you add S.delegate in localizationsDelegates?',
    );
    return instance!;
  }

  static S? maybeOf(BuildContext context) {
    return Localizations.of<S>(context, S);
  }

  /// `Chess`
  String get title {
    return Intl.message(
      'Chess',
      name: 'title',
      desc: 'Title of the application.',
      args: [],
    );
  }

  /// `Settings`
  String get settings {
    return Intl.message(
      'Settings',
      name: 'settings',
      desc: 'Parameters of the application.',
      args: [],
    );
  }

  /// `Server Address`
  String get servAddr {
    return Intl.message(
      'Server Address',
      name: 'servAddr',
      desc: 'Address of the server to retrieve the game from.',
      args: [],
    );
  }

  /// `Connect to Server`
  String get servConnect {
    return Intl.message(
      'Connect to Server',
      name: 'servConnect',
      desc: 'Button to connect to the server.',
      args: [],
    );
  }

  /// `Game ended in a draw.`
  String get drawMsg {
    return Intl.message(
      'Game ended in a draw.',
      name: 'drawMsg',
      desc: 'End of game status is a draw.',
      args: [],
    );
  }

  /// `Game Ended`
  String get gameEnd {
    return Intl.message(
      'Game Ended',
      name: 'gameEnd',
      desc: 'Title of the end of game window.',
      args: [],
    );
  }

  /// `Game ended in a draw.`
  String get white {
    return Intl.message(
      'Game ended in a draw.',
      name: 'white',
      desc: 'Color of the player.',
      args: [],
    );
  }

  /// `Black`
  String get black {
    return Intl.message(
      'Black',
      name: 'black',
      desc: 'Color of the player.',
      args: [],
    );
  }

  /// `None`
  String get none {
    return Intl.message(
      'None',
      name: 'none',
      desc: 'Nothing selected.',
      args: [],
    );
  }

  /// `All`
  String get all {
    return Intl.message(
      'All',
      name: 'all',
      desc: 'Everything selected.',
      args: [],
    );
  }

  /// `AI Depth`
  String get aiDepth {
    return Intl.message(
      'AI Depth',
      name: 'aiDepth',
      desc: 'Depth of the AI search algorithm.',
      args: [],
    );
  }

  /// `Game configuration`
  String get gameConfig {
    return Intl.message(
      'Game configuration',
      name: 'gameConfig',
      desc: 'Name of the current window.',
      args: [],
    );
  }

  /// `Color of the artificial player`
  String get aiColor {
    return Intl.message(
      'Color of the artificial player',
      name: 'aiColor',
      desc: 'Choose a value for the artificial player color\'s:',
      args: [],
    );
  }

  /// `Start Game`
  String get start {
    return Intl.message(
      'Start Game',
      name: 'start',
      desc: 'Button to start the game with the given configuration',
      args: [],
    );
  }

  /// `White AI config`
  String get whiteConfig {
    return Intl.message(
      'White AI config',
      name: 'whiteConfig',
      desc: 'Configuration of the white aI player',
      args: [],
    );
  }

  /// `Black AI config`
  String get blackConfig {
    return Intl.message(
      'Black AI config',
      name: 'blackConfig',
      desc: 'Configuration of the black aI player',
      args: [],
    );
  }

  /// `Restart`
  String get restart {
    return Intl.message(
      'Restart',
      name: 'restart',
      desc:
          'Button to restart a game with the same configuration as the previous one',
      args: [],
    );
  }

  /// `New Game`
  String get newGame {
    return Intl.message(
      'New Game',
      name: 'newGame',
      desc: 'Click here to configure a new game',
      args: [],
    );
  }

  /// `Infos`
  String get info {
    return Intl.message(
      'Infos',
      name: 'info',
      desc: 'Button to learn more about the team programming the game',
      args: [],
    );
  }

  /// `Game`
  String get game {
    return Intl.message(
      'Game',
      name: 'game',
      desc: 'Click here to go back to the current game',
      args: [],
    );
  }

  /// `Undo`
  String get undo {
    return Intl.message(
      'Undo',
      name: 'undo',
      desc: 'Button to undo the last move',
      args: [],
    );
  }

  /// `Redo`
  String get redo {
    return Intl.message(
      'Redo',
      name: 'redo',
      desc: 'Button to play the last move undone',
      args: [],
    );
  }

  /// `Resign`
  String get resign {
    return Intl.message(
      'Resign',
      name: 'resign',
      desc: 'Button to resign and give up the game.',
      args: [],
    );
  }

  /// `Undraw`
  String get undraw {
    return Intl.message(
      'Undraw',
      name: 'undraw',
      desc: 'Button to remove the draw proposition',
      args: [],
    );
  }

  /// `Draw`
  String get draw {
    return Intl.message(
      'Draw',
      name: 'draw',
      desc: 'Button to ask for a end of game in a draw',
      args: [],
    );
  }

  /// `French`
  String get french {
    return Intl.message(
      'French',
      name: 'french',
      desc: 'Button to change the application language to french',
      args: [],
    );
  }

  /// `English`
  String get english {
    return Intl.message(
      'English',
      name: 'english',
      desc: 'Button to change the application language to english',
      args: [],
    );
  }

  /// `Choose language of the app`
  String get chooseLang {
    return Intl.message(
      'Choose language of the app',
      name: 'chooseLang',
      desc: '',
      args: [],
    );
  }
}

class AppLocalizationDelegate extends LocalizationsDelegate<S> {
  const AppLocalizationDelegate();

  List<Locale> get supportedLocales {
    return const <Locale>[
      Locale.fromSubtags(languageCode: 'en'),
      Locale.fromSubtags(languageCode: 'fr'),
    ];
  }

  @override
  bool isSupported(Locale locale) => _isSupported(locale);
  @override
  Future<S> load(Locale locale) => S.load(locale);
  @override
  bool shouldReload(AppLocalizationDelegate old) => false;

  bool _isSupported(Locale locale) {
    for (var supportedLocale in supportedLocales) {
      if (supportedLocale.languageCode == locale.languageCode) {
        return true;
      }
    }
    return false;
  }
}
