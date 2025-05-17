// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a en locale. All the
// messages from the main program should be duplicated here with the same
// function name.

// Ignore issues from commonly used lints in this file.
// ignore_for_file:unnecessary_brace_in_string_interps, unnecessary_new
// ignore_for_file:prefer_single_quotes,comment_references, directives_ordering
// ignore_for_file:annotate_overrides,prefer_generic_function_type_aliases
// ignore_for_file:unused_import, file_names, avoid_escaping_inner_quotes
// ignore_for_file:unnecessary_string_interpolations, unnecessary_string_escapes

import 'package:intl/intl.dart';
import 'package:intl/message_lookup_by_library.dart';

final messages = new MessageLookup();

typedef String MessageIfAbsent(String messageStr, List<dynamic> args);

class MessageLookup extends MessageLookupByLibrary {
  String get localeName => 'en';

  final messages = _notInlinedMessages(_notInlinedMessages);
  static Map<String, Function> _notInlinedMessages(_) => <String, Function>{
        "aiColor": MessageLookupByLibrary.simpleMessage(
          "Color of the artificial player",
        ),
        "aiDepth": MessageLookupByLibrary.simpleMessage("AI Depth"),
        "all": MessageLookupByLibrary.simpleMessage("All"),
        "black": MessageLookupByLibrary.simpleMessage("Black"),
        "blackConfig": MessageLookupByLibrary.simpleMessage("Black AI config"),
        "chooseLang": MessageLookupByLibrary.simpleMessage(
          "Choose language of the app",
        ),
        "draw": MessageLookupByLibrary.simpleMessage("Draw"),
        "drawMsg":
            MessageLookupByLibrary.simpleMessage("Game ended in a draw."),
        "english": MessageLookupByLibrary.simpleMessage("English"),
        "french": MessageLookupByLibrary.simpleMessage("French"),
        "game": MessageLookupByLibrary.simpleMessage("Game"),
        "gameConfig":
            MessageLookupByLibrary.simpleMessage("Game configuration"),
        "gameEnd": MessageLookupByLibrary.simpleMessage("Game Ended"),
        "info": MessageLookupByLibrary.simpleMessage("Infos"),
        "newGame": MessageLookupByLibrary.simpleMessage("New Game"),
        "none": MessageLookupByLibrary.simpleMessage("None"),
        "redo": MessageLookupByLibrary.simpleMessage("Redo"),
        "resign": MessageLookupByLibrary.simpleMessage("Resign"),
        "restart": MessageLookupByLibrary.simpleMessage("Restart"),
        "servAddr": MessageLookupByLibrary.simpleMessage("Server Address"),
        "servConnect":
            MessageLookupByLibrary.simpleMessage("Connect to Server"),
        "settings": MessageLookupByLibrary.simpleMessage("Settings"),
        "start": MessageLookupByLibrary.simpleMessage("Start Game"),
        "title": MessageLookupByLibrary.simpleMessage("Chess"),
        "undo": MessageLookupByLibrary.simpleMessage("Undo"),
        "undraw": MessageLookupByLibrary.simpleMessage("Undraw"),
        "white": MessageLookupByLibrary.simpleMessage("Game ended in a draw."),
        "whiteConfig": MessageLookupByLibrary.simpleMessage("White AI config"),
      };
}
