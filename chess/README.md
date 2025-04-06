# Chess in Java

## Options of the game
You can launch the game with different options.

### User Interface
- By default, the game is in Command line interface
- To launch the application in graphical mode, launch with the `-g` or `--gui` option. If you launch the application in GUI, you can change most of the options listed below.
- To launch the app in UCI mode, to play against other AI, use the option `--uci`.

### For developers
- To display verbose logs, use the `-v` or `--verbose` option.
- To display debug logs, use the `-d` or `--debug` option.
- Be careful, it quickly becomes unreadable when launching with AI.
- It is always logged into the console, even in GUI mode

### Special modes
- We have a blitz mode, where the given time correspond to the duration of a turn for one player. To activate it, use the option `-b` or `--blitz`. By default, the time is 30 minutes per turn.
- To select the time for the blitz, use the option `-t=TIME` or `--time=TIME` The time must be in minutes.
- To play in contest mode, use the option `-c=FILENAME` or `--contest=FILENAME`. An artificial player will play the best move in the game loaded from the file, and save it.

### Settings
- To set the configuration by default of the game, to avoid writing all the options wanted, you can use the option `--config=FILENAME`, with the filename ending by `.chessrc`
- To select the language of the application use the option `--lang=LANGUAGE` with `LANGUAGE` being `FR` for French or `EN` for English. By default, the application is in English.
- If you want to load a file containing a game, you can use the option `--load=FILENAME` with `FILENAME` being the name of the file you want to load. The file can contain a history

### Information about the app
- To display a help message, about the options of the game, launch the app with the option `-h` or `--help`
- To display the version of the app, launch with the option `-V` or `--version`.
- These options will quit after displaying the message.
- If both options are used, only the help is displayed.

### AI
- Most options are adapted for black and white player, to allow the user to do match between two AI parametrized differently.
- To plays against an AI, or do a match between AI, use the option `-a=COLOR` or `--ai=COLOR` with `COLOR` being `A` (all), `W` (white) or `B` (black). If you don't specify a color, White will be used.
- We have several AI algorithms implemented, by default the solver uses Alpha beta. The available values are :
    - `MINIMAX`, Exploration via a tree.
    - `ALPHA_BETA`, Alpha-Beta pruning, like Minimax but way quicker.
    - `ALPHA_BETA_PARALLEL`, Alpha-Beta with multithreading. More nodes explored but faster.
    -  `ALPHA_BETA_ID`, Alpha-Beta with iterative deepening, really efficient algorithm.
    - `ALPHA_BETA_ID_PARALLEL`, Alpha-Beta with iterative deepening and multithreading.
    - `MCTS` : Monte Carlo Tree Search, quite slow, needs around 500 simulations to give quite good result.
    - To choose the algorithm, use `--ai-mode=ALGO` for both players, `--ai-mode-w=ALGO` for the white player and `--ai-mode-b=ALGO` for the black player
- Specify the depth of the AI algorithm, works for Minimax, Alpha-Beta and its variants. By default, the depth is 4.
    - For both AI players (or one if you have only one AI player), use `--ai-depth=DEPTH`
    - For the white AI player, use `--ai-depth-w=DEPTH`
    - For the black AI player, use `--ai-depth-b=DEPTH`
- Specify the number of simulations for the Monte Carlo Tree search algorithm. By default, the value is `150`. To use the option, type `--ai-simulation=SIMULATIONS` for both players, `--ai-simulation-w=SIMULATIONS` for the white player or `--ai-simulation-b=SIMULATIONS` for the black player.
- To specify the time limit for the AI, use the option `--ai-time=TIME`. `TIME` must be in seconds. By default, the time limit is 5 seconds. 
- Specify the heuristic used for the start and middle game. Use the option `--ai-heuristic=HEURISTIC` for both players, `--ai-heuristic-w=HEURISTIC` for white players or `--ai-heuristic-b=HEURISTIC` for black players. The available values are (case-sensitive):
    - `STANDARD` : Aggregates multiple
    heuristics to evaluate the board
    during the start and middle game.
    - `STANDARD_LIGHT` : A lighter version
    of the `STANDARD` heuristic, taking
    less parameters into account.
    - `SHANNON` : Basic Heuristic from
    Shannon.
    - `ENDGAME` : Aggregates multiple
    heuristics to evaluate the board
    state during the endgame phase of
    the match.
    - `BAD_PAWNS` : Computes a score
    according to the potential
    weaknesses in the observed pawn
    structures.
    - `BISHOP_ENDGAME` : Computes a score
    according to how performant bishops
    are for an endgame position.
    - `DEVELOPMENT` : Computes and returns
    a score corresponding to the level
    of development for each player.
    - `GAME_STATUS` : Computes a score
    based on the possible game endings.
    - `KING_ACTIVITY` : Computes a score
    based on the king's activity (is in
    center and has a lot of possible
    moves).
    -`KING_OPPOSITION` : Computes a score
    according to the (un)balance of the
    kings position.
    - `KING_SAFETY` : Assigns a score to a
    player according to the safety of
    his king.
    - `MATERIAL` : Computes a score based
    on the pieces on the board.
    - `MOBILITY` : Computes a score based
    on the available moves for each
    player.
    - `PAWN_CHAIN` : Computes a score
    according to how strongly pawns are
    connected.
    - `PROMOTION` : Computes a score
    according to closeness of pawns
    promoting.
    - `SPACE_CONTROL` : Gives a score
    based on how much control over the
    entire board the players have.
- You can choose the heuristic for the endgame with the option `--ai-endgame=HEURISTIC` for both players, `--ai-endgame-w=HEURISTIC` for the white player or `--ai-endgame-b=HEURISTIC` for the black player. The available values for `HEURISTIC` are listed on top. The default value is `ENDGAME`
- To specify the weights of the standard heuristic, you can use the option `--ai-weight-b=WEIGHTS` for the black player and `--ai-weight-w=WEIGHTS` for the white player.


## How to play

### CLI

When you play in CLI, you can type `help` at any time to have information on the available commands.

### GUI
#### Starting the Game

When launching the game in GUI mode, you can play directly with the options given when launching the game (or the default) or you can customize your game by clicking on the `File` menu, and on the button `New game`. 

To start the game, you have two possibilities : if the first player is human, you have to click on a piece and on its destination or if the player is an AI, you have to go in the `Game` menu and click on `Start`.

#### About the board
When clicking on a square on the board, you will see dots appearing on other squares. They correspond to the possible moves you can make from the selected piece. 

If you see your king in a red square, this means that you are in check.

The last move played is displayed with green squares on the board.


#### Display
To customize the app, you can go in the `Options` menu, where you will be able to change the language of the app and also the color theme. You have several options of already created themes, but you can also create your own by clicking on `Customize`.

You can also resize the application, this will mainly affect the board. Indeed, you can resize to make the board smaller (or bigger), but the text will not change it's size, so it is possible to resize in a way that the display of the Control Panel is truncated.

### UCI

If you want to connect this chess engine with another, you can launch the app in UCI (Universal Chess Interface) mode with the `-uci` option.
By default, the UCI solver will use the configuration given to the white solver which you can configure as usual (see AI section). All UCI commands 
have not been implemented yet, that's why you can't limit search by time or depth directly in the `go` command (those are ignored by default).
