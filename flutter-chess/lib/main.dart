import 'package:chess/providers/game_provider.dart';
import 'package:chess/providers/history_provider.dart';
import 'package:chess/screens/game_screen.dart';
import 'package:chess/theme.dart';
import 'package:chess/util.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => GameProvider()),
        ChangeNotifierProvider(create: (_) => HistoryProvider()),
      ],
      child: const MyApp(),
    ),
  );
  //runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    TextTheme textTheme = createTextTheme(context, "Inter Tight", "Inter");

    MaterialTheme theme = MaterialTheme(textTheme);
    return MaterialApp(
      title: 'Chess App',
      localizationsDelegates: [
        AppLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: [
        Locale('en'),
        Locale('fr'),
      ],
      //theme: ThemeData(primarySwatch: Colors.blue),
      //theme: brightness == Brightness.light ? theme.light() : theme.dark(),
      theme: theme.light(),
      home: GameScreen(),
    );
  }
}
