package pdp.utils;

import org.apache.commons.cli.Option;

/** Enum composed of the different options available in command line. Use of commons-cli. */
public enum OptionType {
  LANG {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "lang";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .argName("LANGUAGE")
          .hasArg(true)
          .desc("Choose the language for the app (en supported)")
          .build();
    }
  },
  HELP {
    @Override
    public String getShort() {
      return "h";
    }

    @Override
    public String getLong() {
      return "help";
    }

    @Override
    public Option getOption() {
      return new Option(this.getShort(), this.getLong(), false, "Print this message and exit");
    }
  },
  VERSION {
    @Override
    public String getShort() {
      return "V";
    }

    @Override
    public String getLong() {
      return "version";
    }

    @Override
    public Option getOption() {
      return new Option(
          this.getShort(), this.getLong(), false, "Print the version information and exit");
    }
  },
  VERBOSE {
    @Override
    public String getShort() {
      return "v";
    }

    @Override
    public String getLong() {
      return "verbose";
    }

    @Override
    public Option getOption() {
      return new Option(this.getShort(), this.getLong(), false, "Display more information");
    }
  },
  DEBUG {
    @Override
    public String getShort() {
      return "d";
    }

    @Override
    public String getLong() {
      return "debug";
    }

    @Override
    public Option getOption() {
      return new Option(this.getShort(), this.getLong(), false, "Print debugging information");
    }
  },
  BLITZ {
    @Override
    public String getShort() {
      return "b";
    }

    @Override
    public String getLong() {
      return "blitz";
    }

    @Override
    public Option getOption() {
      return new Option(this.getShort(), this.getLong(), false, "Play in blitz mode");
    }
  },
  GUI {
    @Override
    public String getShort() {
      return "g";
    }

    @Override
    public String getLong() {
      return "gui";
    }

    @Override
    public Option getOption() {
      return new Option(
          this.getShort(), this.getLong(), false, "Displays the game with a  graphical interface.");
    }
  },
  TIME {
    @Override
    public String getShort() {
      return "t";
    }

    @Override
    public String getLong() {
      return "time";
    }

    @Override
    public Option getOption() {
      return Option.builder(this.getShort())
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("TIME")
          .desc("Specify time per round for blitz mode (default 30min)")
          .type(Integer.class)
          .build();
    }
  },
  CONTEST {
    @Override
    public String getShort() {
      return "c";
    }

    @Override
    public String getLong() {
      return "contest";
    }

    @Override
    public Option getOption() {
      return Option.builder(this.getShort())
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("FILENAME")
          .desc("AI plays one move in the given file")
          .build();
    }
  },
  CONFIG {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "config";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("FILENAME")
          .desc("Sets the configuration file to use")
          .build();
    }
  },
  AI {
    @Override
    public String getShort() {
      return "a";
    }

    @Override
    public String getLong() {
      return "ai";
    }

    @Override
    public Option getOption() {
      return Option.builder(this.getShort())
          .longOpt(this.getLong())
          .optionalArg(true)
          .argName("COLOR")
          .desc(
              "Launch the program in AI mode, with "
                  + "artificial player with COLOR 'W', 'B' or 'A' (All),(W by default).")
          .build();
    }
  },
  AI_MODE {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-mode";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("ALGORITHM")
          .desc(
              """
                          Choose the exploration algorithm for the artificial players.
                          Available options:
                          - MINIMAX : Uses the MiniMax algorithm.
                          - ALPHA_BETA : Uses the Alpha-Beta Pruning algorithm (default).
                          - MCTS : Uses Monte Carlo Tree Search for AI move exploration.
                          - ALPHA_BETA_PARALLEL, Alpha-Beta with multithreading. More nodes explored but faster.
                          - ALPHA_BETA_ID, Alpha-Beta with iterative deepening, really efficient algorithm.
                          - ALPHA_BETA_ID_PARALLEL, Alpha-Beta with iterative deepening and multithreading.""")
          .build();
    }
  },
  AI_MODE_W {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-mode-w";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("ALGORITHM")
          .desc("Choose the exploration algorithm for the artificial white player.\n")
          .build();
    }
  },
  AI_MODE_B {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-mode-b";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("ALGORITHM")
          .desc("Choose the exploration algorithm for the artificial black player.\n")
          .build();
    }
  },
  AI_SIMULATION {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-simulation";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("SIMULATION")
          .desc("Specify the number of simulations for the MCTS AI algorithm")
          .build();
    }
  },
  AI_SIMULATION_W {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-simulation-w";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("SIMULATIONS")
          .desc("Specify the number of simulations for the White MCTS AI algorithm")
          .build();
    }
  },
  AI_SIMULATION_B {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-simulation-b";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("SIMULATIONS")
          .desc("Specify the number of simulations for the Black MCTS AI algorithm")
          .build();
    }
  },
  AI_DEPTH {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-depth";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("DEPTH")
          .desc(
              "Specify the depth of the AI algorithm or the"
                  + " number of simulations for the MCTS AI algorithm")
          .build();
    }
  },
  AI_DEPTH_W {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-depth-w";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("DEPTH")
          .desc("Specify the depth of the AI algorithm for the white player")
          .build();
    }
  },
  AI_DEPTH_B {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-depth-b";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("DEPTH")
          .desc("Specify the depth of the AI algorithm for the black player")
          .build();
    }
  },
  AI_HEURISTIC {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-heuristic";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("HEURISTIC")
          .desc(
              """
                          Choose the heuristic for the artificial players.
                          Choose between these heuristic (case sensitive)
                          - STANDARD : Aggregates multiple heuristics to \
                          evaluate the board during the start and middle game.
                          - STANDARD_LIGHT : A lighter version of the STANDARD \
                          heuristic, taking less parameters into account.
                          - SHANNON : Basic Heuristic from Shannon.
                          - ENDGAME : Aggregates multiple heuristics to evaluate\
                           the board state during the endgame phase of the match.
                          - BAD_PAWNS : Computes a score according to the \
                          potential weaknesses in the observed pawn structures.
                          - BISHOP_ENDGAME : Computes a score according to \
                          how performant bishops are for an endgame position.
                          - DEVELOPMENT : Computes and returns a score \
                          corresponding to the level of development for each player.
                          - GAME_STATUS : Computes a score based on the possible game endings.
                          - KING_ACTIVITY : Computes a score based on the king's \
                          activity (is in center and has a lot of possible moves).
                          - KING_OPPOSITION : Computes a score according to \
                          the (un)balance of the kings position.
                          - KING_SAFETY : Assigns a score to a player\
                           according to the safety of his king.
                          - MATERIAL : Computes a score based on the pieces on the board.
                          - MOBILITY : Computes a score based on the available moves for each player.
                          - PAWN_CHAIN : Computes a score \
                          according to how strongly pawns are connected.
                          - PROMOTION : Computes a score according to closeness of pawns promoting.
                          - SPACE_CONTROL : Gives a score based on how \
                          much control over the entire board the players have.
                          """)
          .build();
    }
  },
  AI_HEURISTIC_W {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-heuristic-w";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("HEURISTIC")
          .desc("Choose the heuristic for the artificial white player.\n")
          .build();
    }
  },
  AI_HEURISTIC_B {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-heuristic-b";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("HEURISTIC")
          .desc("Choose the heuristic for the artificial black player.\n")
          .build();
    }
  },
  AI_ENDGAME {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-endgame";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("HEURISTIC")
          .desc("Choose the heuristic for the endgame of the artificial players.\n")
          .build();
    }
  },
  AI_ENDGAME_W {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-endgame-w";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("HEURISTIC")
          .desc("Choose the heuristic for the endgame of the white artificial player.\n")
          .build();
    }
  },
  AI_ENDGAME_B {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-endgame-b";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(true)
          .argName("HEURISTIC")
          .desc("Choose the heuristic for the endgame of the black artificial player.\n")
          .build();
    }
  },
  AI_TIME {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-time";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .argName("TIME")
          .hasArg(true)
          .desc("Specify the time of reflexion for AI mode in seconds (default 5 seconds)")
          .build();
    }
  },
  AI_WEIGHT_W {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-weight-w";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .argName("WEIGHT")
          .hasArg(true)
          .desc("Specify the Weight to use with the standard heuristic.")
          .build();
    }
  },
  AI_WEIGHT_B {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "ai-weight-b";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .argName("WEIGHT")
          .hasArg(true)
          .desc("Specify the Weight to use with the standard heuristic.")
          .build();
    }
  },
  LOAD {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "load";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .argName("FILENAME")
          .hasArg(true)
          .desc("The name of the file from which to load the history")
          .build();
    }
  },
  UCI {
    @Override
    public String getShort() {
      return null;
    }

    @Override
    public String getLong() {
      return "uci";
    }

    @Override
    public Option getOption() {
      return Option.builder()
          .longOpt(this.getLong())
          .hasArg(false)
          .desc("Launch the engine in UCI mode")
          .build();
    }
  };

  /**
   * Retrieves the short name of the option.
   *
   * @return short name of the option
   */
  public abstract String getShort();

  /**
   * Retrieves the long name of the option.
   *
   * @return long name of the option
   */
  public abstract String getLong();

  /**
   * Retrieves the option.
   *
   * @return instance of the option
   */
  public abstract Option getOption();
}
