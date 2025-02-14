package pdp.utils;

import org.apache.commons.cli.Option;

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
              "Launch the program in AI mode, with artificial player with COLOR ’B’ or ’A’ (All),(W by default).")
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
          .desc("Choose the exploration algorithm for the artificial player.")
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
          .desc("Specify the depth of the AI algorithm")
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
          .desc("Choose the heuristic for the artificial player")
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
          .desc("Specify the time of reflexion for AI mode (default 5 seconds)")
          .build();
    }
  };

  public abstract String getShort();

  public abstract String getLong();

  public abstract Option getOption();
}
