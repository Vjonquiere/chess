package pdp;

import pdp.utils.CLIOptions;

public class Main {

  public static String returnsA() {
    return "A";
  }

  public static void main(String[] args) {
    CLIOptions.parseOptions(args, Runtime.getRuntime());
    System.out.println("Hello world!");
    // TODO
  }
}
