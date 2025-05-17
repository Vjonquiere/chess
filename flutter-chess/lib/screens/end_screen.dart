import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class EndScreen extends StatefulWidget {
  final String endType;

  const EndScreen(this.endType, {super.key});

  @override
  State<StatefulWidget> createState() {
    return switch (endType) {
      "DRAW" => _DrawEndScreen(),
      "WIN_WHITE" => _EndScreen(),
      "WIN_BLACK" => _EndScreen(),
      _ => throw UnimplementedError(),
    };
  }
}

class _EndScreen extends State<EndScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(AppLocalizations.of(context)!.gameEnd)),
      body: Row(
        children: [
          Text("Game ended"),
        ],
      ),
    );
  }
}

class _DrawEndScreen extends State<EndScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(AppLocalizations.of(context)!.gameEnd)),
      body: Row(
        children: [
          Text(AppLocalizations.of(context)!.draw),
        ],
      ),
    );
  }
}
