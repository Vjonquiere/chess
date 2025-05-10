import 'package:chess/services/websocket_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class GameConfigScreen extends StatelessWidget {
  final WebSocketService _socketService;
  final AiChoice _aiChoice = const AiChoice();

  const GameConfigScreen(this._socketService, {super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Game settings"),
      ),
      body: Column(children: [
        Text("AI: "),
        _aiChoice,
        Padding(padding: EdgeInsets.only(top: 30.0)),
        ElevatedButton(
            onPressed: () {
              _socketService.send({'type': 'restart'});
            },
            child: Text("Start game"))
      ]),
    );
  }
}

enum AiModes { n, w, b, a }

class AiChoice extends StatefulWidget {
  const AiChoice({super.key});

  @override
  State<AiChoice> createState() => _AiChoiceState();
}

class _AiChoiceState extends State<AiChoice> {
  AiModes selected = AiModes.n;
  AiConfig? whiteConfig;
  AiConfig? blackConfig;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        SegmentedButton<AiModes>(
          segments: const <ButtonSegment<AiModes>>[
            ButtonSegment<AiModes>(
              value: AiModes.n,
              label: Text('None'),
              icon: Icon(Icons.person),
            ),
            ButtonSegment<AiModes>(
              value: AiModes.w,
              label: Text('White'),
              icon: Icon(Icons.add_circle),
            ),
            ButtonSegment<AiModes>(
              value: AiModes.b,
              label: Text('Black'),
              icon: Icon(Icons.add_circle),
            ),
            ButtonSegment<AiModes>(
              value: AiModes.a,
              label: Text('All'),
              icon: Icon(Icons.smart_toy_sharp),
            ),
          ],
          selected: <AiModes>{selected},
          onSelectionChanged: (Set<AiModes> newSelection) {
            setState(() {
              selected = newSelection.first;
              whiteConfig = null;
              blackConfig = null;
              switch (selected) {
                case AiModes.b:
                  blackConfig = AiConfig(false);
                  break;
                case AiModes.w:
                  whiteConfig = AiConfig(true);
                  break;
                case AiModes.a:
                  whiteConfig = AiConfig(true);
                  blackConfig = AiConfig(false);
                default:
                  break;
              }
            });
          },
        ),
        whiteConfig != null ? whiteConfig! : Container(),
        blackConfig != null ? blackConfig! : Container(),
      ],
    );
  }
}

enum AiAlgorithm {
  ALPHA_BETA,
  MINIMAX,
  ALPHA_BETA_PARALLEL,
  ALPHA_BETA_ID,
  ALPHA_BETA_PARALLEL_ID
}

class AiConfig extends StatefulWidget {
  final bool isWhite;

  AiConfig(this.isWhite);

  @override
  State<AiConfig> createState() => _AiConfig();
}

class _AiConfig extends State<AiConfig> {
  AiAlgorithm selected = AiAlgorithm.ALPHA_BETA;
  double v = 4.0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(padding: EdgeInsets.only(top: 25.0)),
        Text("${widget.isWhite ? "White" : "Black"} Ai config"),
        DropdownButton<AiAlgorithm>(
            value: selected,
            items: AiAlgorithm.values
                .map<DropdownMenuItem<AiAlgorithm>>((AiAlgorithm value) {
              return DropdownMenuItem<AiAlgorithm>(
                  value: value, child: Text(value.toString()));
            }).toList(),
            onChanged: (AiAlgorithm? algorithm) {
              setState(() {
                selected = algorithm!;
              });
            }),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text("Ai depth"),
            Slider(
                min: 1.0,
                max: 7.0,
                divisions: 7,
                value: v,
                label: v.round().toString(),
                onChanged: (double value) {
                  setState(() {
                    v = value;
                  });
                })
          ],
        )
      ],
    );
  }
}
