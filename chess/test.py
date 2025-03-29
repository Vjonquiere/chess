import chess
import chess.engine
import time

def play_uci_match(engine1_path, engine2_path, time_limit=0.5, elo1=1320, elo2=1320):
    board = chess.Board()


    engine1 = chess.engine.SimpleEngine.popen_uci(engine1_path)
    engine1.configure({"UCI_LimitStrength": True, "UCI_Elo": elo1})


    engine2 = chess.engine.SimpleEngine.popen_uci(["java", "-jar", engine2_path,"-uci"])
    #engine2.configure({"UCI_LimitStrength": True, "UCI_Elo": elo2})

    engines = [engine2, engine1]
    turn = 0

    while not board.is_game_over():
        if turn == 0:
            result = engines[0].play(board, chess.engine.Limit(time=15000))
        else:
            result = engines[1].play(board, chess.engine.Limit(time=time_limit))
        board.push(result.move)
        #print(board, "\n")

        turn = 1 - turn  # Switch turn

    print("Game Over!", board.result())

    engine1.quit()
    engine2.quit()

if __name__ == "__main__":
    engine1_path = "/usr/games/stockfish"
    engine2_path = "target/chess-0.0.3.jar"
    for i in range(0,11,1):
        try:
            play_uci_match(engine1_path, engine2_path)
        except Exception as e:
            print(e)

