import A4Q4 as Game
import Players
import sys as sys
# import MinimaxData as Searcher
# from CMPT317_A4_Provided import Minimax
# from CMPT317_A4_Provided import MinimaxTR
# from CMPT317_A4_Provided import AlphaBeta
# from CMPT317_A4_Provided import MinimaxData
import A4Q3
# import A4Q2
import MinimaxDL


# Operating System: Unix
# Programming language: python

# Compilation step:
# To compile my submission, copy and paste the commond- line as follows if you want create a 5*5 board
#  depth = 2
# <python3  A4Q5.py 5 2>
#
# create the game, and the initial state
all = []
for i in range(1, 9):
    if i < 7:
        game = Game.Game(5, i)
        game1 = Game.Game(5, i)
    if i==7:
        game = Game.Game(5, i-1)
        game1 = Game.Game(5, 2)
    if i == 8:
        game = Game.Game(5, 2)
        game1 = Game.Game(5, 6)
    state = game.initial_state()

    # set up the players
    current_player = Players.VerboseComputer(game, A4Q3.AlphaBeta(game))
    #current_player = Players.HumanMenu(game)

    other_player = Players.VerboseComputer(game, A4Q3.AlphaBeta(game1))
    #other_player = Players.HumanMenu(game)


    # play the game
    while not game.is_terminal(state):
        #show the board out of courtesy
        #state.display()

        # ask the current player for a move
        choice = current_player.ask_move(state)
        # check the move
        if (choice == None):
            state.turn = 40
        else:
            assert choice in game.actions(state), "The action <{}> is not legal in this state".format(choice)
            state = game.result(state, choice)

        # apply the move
        # swap the players
        current_player, other_player = other_player, current_player

    # game's over
    state.display()
    all.append(game.congratulate(state))
print(all)
