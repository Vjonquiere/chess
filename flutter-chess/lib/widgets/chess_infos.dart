import 'package:chess/services/websocket_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ChessInfos extends StatefulWidget {
  final WebSocketService _socketService;
  final List<Card> moves;
  final String currentPlayer;
  final bool hideHistory;
  final Axis historyDirection;

  ChessInfos(this._socketService, this.moves, this.currentPlayer,
      {this.hideHistory = false, this.historyDirection = Axis.vertical});

  @override
  State<StatefulWidget> createState() {
    return _ChessInfos(_socketService);
  }
}

class _ChessInfos extends State<ChessInfos> {
  final WebSocketService _socketService;

  _ChessInfos(this._socketService);

  @override
  Widget build(BuildContext context) {
    return Expanded(
        child: Padding(
      padding: EdgeInsets.symmetric(horizontal: 8.0),
      child: Column(children: [
        Container(
            decoration: BoxDecoration(
                border: Border.all(
                    width: 2.0,
                    style: BorderStyle.solid,
                    color: Theme.of(context).primaryColor),
                borderRadius: const BorderRadius.all(Radius.circular(5.0))),
            child: Column(
              children: [
                Row(
                  children: [
                    const Icon(
                      Icons.person_rounded,
                      size: 50.0,
                    ),
                    Padding(padding: EdgeInsets.symmetric(horizontal: 5.0)),
                    Text(
                      AppLocalizations.of(context)!.white,
                      style: TextStyle(
                          fontSize: 25.0, fontWeight: FontWeight.w600),
                    ),
                    Padding(padding: EdgeInsets.symmetric(horizontal: 5.0)),
                    widget.currentPlayer == "b"
                        ? Container()
                        : Icon(
                            Icons.radio_button_checked,
                            color: Theme.of(context).primaryColor,
                          ),
                  ],
                ),
                Row(
                  children: [
                    const Icon(
                      Icons.smart_toy,
                      size: 50.0,
                    ),
                    Padding(padding: EdgeInsets.symmetric(horizontal: 5.0)),
                    Text(
                      AppLocalizations.of(context)!.black,
                      style: TextStyle(
                          fontSize: 25.0, fontWeight: FontWeight.w600),
                    ),
                    Padding(padding: EdgeInsets.symmetric(horizontal: 5.0)),
                    widget.currentPlayer == "w"
                        ? Container()
                        : Icon(
                            Icons.radio_button_checked,
                            color: Theme.of(context).primaryColor,
                          ),
                  ],
                ),
              ],
            )),
        Padding(padding: EdgeInsets.symmetric(vertical: 5.0)),
        widget.hideHistory
            ? Container()
            : Flexible(
                child: Container(
                    decoration: BoxDecoration(
                        border: Border.all(
                            width: 2.0,
                            style: BorderStyle.solid,
                            color: Theme.of(context).primaryColor),
                        borderRadius: BorderRadius.all(Radius.circular(5.0))),
                    child: ListView(
                      scrollDirection: widget.historyDirection,
                      clipBehavior: Clip.hardEdge,
                      children: widget.moves,
                    ))),
        Padding(padding: EdgeInsets.symmetric(vertical: 5.0)),
        Row(
          children: [
            Expanded(
                child: Container(
                    padding:
                        EdgeInsets.symmetric(vertical: 5.0, horizontal: 5.0),
                    decoration: BoxDecoration(
                        border: Border.all(
                            width: 2.0,
                            style: BorderStyle.solid,
                            color: Theme.of(context).primaryColor),
                        borderRadius: BorderRadius.all(Radius.circular(5.0))),
                    child: Wrap(
                      runSpacing: 8.0,
                      spacing: 8.0,
                      alignment: WrapAlignment.center,
                      children: [
                        OutlinedButton(
                            onPressed: () {
                              _socketService.send({'type': 'undo'});
                            },
                            child: Text(AppLocalizations.of(context)!.undo)),
                        OutlinedButton(
                            onPressed: () {
                              _socketService.send({'type': 'redo'});
                            },
                            child: Text(AppLocalizations.of(context)!.redo)),
                        OutlinedButton(
                            onPressed: () {
                              _socketService.send({'type': 'draw'});
                            },
                            child: Text(AppLocalizations.of(context)!.draw)),
                        OutlinedButton(
                            onPressed: () {
                              _socketService.send({'type': 'resign'});
                            },
                            child: Text(AppLocalizations.of(context)!.resign)),
                        OutlinedButton(
                            onPressed: () {
                              _socketService.send({'type': 'undraw'});
                            },
                            child: Text(AppLocalizations.of(context)!.undraw)),
                        OutlinedButton(
                            onPressed: () {
                              _socketService.send({'type': 'restart'});
                            },
                            child: Text(AppLocalizations.of(context)!.restart)),
                      ],
                    )))
          ],
        ),
        Padding(padding: EdgeInsets.symmetric(vertical: 5.0)),
      ]),
    ));
  }
}
