import chess
import chess.engine
import random

STOCKFISH_PATH = "/usr/games/stockfish"
JAR_PATH = "target/chess-0.0.3.jar"
STOCKFISH_ELO = 1320


def play_game(ai_color="white"):

    print("--- New Game ---")
    board = chess.Board()
    engine = chess.engine.SimpleEngine.popen_uci(STOCKFISH_PATH)
    engine2 = chess.engine.SimpleEngine.popen_uci(["java", "-jar", JAR_PATH,"-uci"])

    engine.configure({
        "UCI_LimitStrength": True,
        "UCI_Elo": STOCKFISH_ELO
    })

    while not board.is_game_over():
        #print(board)
        if (board.turn == chess.WHITE and ai_color == "white") or (board.turn == chess.BLACK and ai_color == "black"):
            #print("Chess 2")
            result = engine2.play(board, chess.engine.Limit(None))
            ai_move = result.move
        else:
            #print("Stockfish")
            result = engine.play(board, chess.engine.Limit(depth=4))
            ai_move = result.move

        if ai_move not in board.legal_moves:
            print(f"Illegal move by AI: {ai_move}")
            return "Stockfish wins"
        #print("played move = " + ai_move.uci())
        board.push(ai_move)


    engine.quit()
    engine2.quit()
    return board.result()


def train_ai(games=100):
    results = {"win": 0, "loss": 0, "draw": 0}

    for i in range(games):
        try:
            result = play_game(ai_color=random.choice(["white", "black"]))

            if result == "1-0":
                results["win"] += 1
            elif result == "0-1":
                results["loss"] += 1
            else:
                results["draw"] += 1

            print(f"Game {i+1}/{games}: {result}")
        except Exception as e:
            print("Game failed: ", e)

    print("Final Results:", results)


if __name__ == "__main__":
    train_ai(games=30)

