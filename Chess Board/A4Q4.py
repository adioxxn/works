# CMPT 317 A Skeleton Python Class for any 2 player perfect information game

# Copyright (c) 2016-2019 Michael C Horsch,
# Department of Computer Science, University of Saskatchewan

# This file is provided solely for the use of CMPT 317 students.  Students are permitted
# to use this file for their own studies, and to make copies for their own personal use.

# This file should not be posted on any public server, or made available to any party not
# enrolled in CMPT 317.

# This implementation is provided on an as-is basis, suitable for educational purposes only.

# The Game Class encodes the rules of a game.
# Game Class Interface:
#    initial_state(self)
#       - returns an initial game state
#       - the state can be any object that stores the details
#         needed to keep track of the game, including any information
#         convenient to store
#
#    is_mins_turn(self, state)
#    is_maxs_turn(self, state)
#       - return a boolean that indicates if it's Min/Max's turn
#
#    is_terminal(self, state)
#       - return a boolean that indicates if the state represents
#         a true end of the game situation, i.e, a win or a draw
#
#    utility(self, state)
#       - return the utility value of the given terminal state
#       - must return one of three values: k_min, k_draw, k_max
#           k_min: the value returned if Min is the winner
#           k_max: the value returned if Max is the winner
#           k_draw: the value returned if the game ends in a draw
#           - any range is allowed.
#           - probably best if k_min < k_draw < k_max
#             and k_draw half way between k_min, k_max
#       - will only be called if the state is determined to be
#         a terminal state by is_terminal()
#       - only terminal states have utility; other states get
#         their value from searching.
#
#    actions(self, state)
#       - returns a list of actions legal in the given state
#
#    result(self, state, action)
#       - returns the state resulting from the action in the given state
#
#    cutoff_test(self, state, depth)
#       - returns a bolean that indicates if this state and depth is suitable
#         to limit depth of search.  A simple implementation might just look
#         at the depth; a more sophisticated implementation might look at
#         the state as well as the depth.
#
#    eval(self, state)
#       - returns a numeric value that estimates the minimax value of the
#         given state.  This gets called if cutoff_test() returns true.
#         Instead of searching to the bottom of the tree, this function
#         tries to guess who might win.  The function should return a value
#         that is in the range defined by utility().  Because this is an
#         estimate, values close to k_min (see utility()) indicate that
#         a win for Min is likely, and values close to k_max should indicate
#         a win for Max is likely.  Should not return values outside the range
#         (k_min, k_max).  k_min means "Min wins"; a value smaller than k_min
#         makes no sense.  An estimate from eval() cannot be more extreme than a
#         fact known from utility().
#
#    transposition_string(self)
#       - return a string representation of the state
#       - for use in a transposition table
#       - this string should represent the state exactly, but also without
#         too much waste.  In a normal game, lots of these get stored!
#
#    congratulate(self)
#       - could be called at the end of the game to indicate who wins
#       - this is not absolutely necessary, but could be informative
import random as rand
import math as math
import Players
import sys as sys
import MinimaxDL


# from CMPT317_A4_Provided import MinimaxData


import Game
import Players
import sys as sys
# import MinimaxData as Searcher
# from CMPT317_A4_Provided import Minimax
# from CMPT317_A4_Provided import MinimaxTR
# from CMPT317_A4_Provided import AlphaBeta
# from CMPT317_A4_Provided import MinimaxData
import MinimaxDL
import math as math


class GameState(object):
    """ The GameState class stores the information about the state of the game.
    """
    _ablank = '  '
    _anRebel = 'R'
    _anSith = 'S'
    _anJedi = 'J'

    def __init__(self, size):
        # the gameState dictionary stores the position of each piece
        self.size = size
        self.sithlocation = math.ceil(self.size / 2)
        self.gameState = dict()
        self.siths = dict()
        self.rebels = dict()
        self.jedis = dict()
        self.sithN = 0
        self.rebelN = 0
        self.turn = 0
        for r in range(1, size + 1):
            for c in range(1, size + 1):
                if (r == 1 and c == self.sithlocation):
                    self.gameState[r, c] = 's' + str(self.sithN)
                    # first number is the sith number and the second is the location
                    self.siths['s' + str(self.sithN)] = (r, c)
                    self.sithN += 1
                elif (r == self.size):
                    self.gameState[r, c] = 'r' + str(self.rebelN)
                    self.rebels['r' + str(self.rebelN)] = (r, c)
                    self.rebelN += 1
                else:
                    self.gameState[r, c] = self._ablank
        # the blanks show what's left to choose from
        # a boolean to store if it's Max's turn; True by default
        self.maxs_turn = True
        # if this state is a winning state, store that information
        # because it is cheaper to check once, than a bunch of times
        self.cachedWin = False

        # if cachedWin is True, then cachedWinner is a boolean
        # True means Max won; False means Min won
        self.cachedWinner = None

        # now cache the string that represents this state
        self.stringified = str(self)

    def myclone(self):
        """ Make and return an exact copy of the state.
        """
        new_state = GameState(self.size)
        for rc in self.gameState:
            new_state.gameState[rc] = self.gameState[rc]
        new_state.jedis.clear()
        for i in self.jedis:
            new_state.jedis[i] = self.jedis.get(i)
        new_state.turn = self.turn
        new_state.rebels.clear()
        for i in self.rebels:
            new_state.rebels[i] = self.rebels.get(i)
        new_state.siths.clear()
        for i in self.siths:
            new_state.siths[i] = self.siths.get(i)
        new_state.maxs_turn = self.maxs_turn
        new_state.cachedWin = self.cachedWin
        new_state.cachedWinner = self.cachedWinner
        new_state.stringified = self.stringified  # copy the reference not the string
        return new_state

    def display(self):
        """
        Present the game state to the console.
        """
        for r in range(1, self.size + 1):
            print("+-" * (self.size + 4))
            print("|", end="")
            for c in range(1, self.size):
                print(self.gameState[r, c], end="")
                print("|", end="")
            print(self.gameState[r, self.size], end="")
            print("|")
        print("+-" * (self.size + 4))


class Game(object):
    """ The Game object defines the interface that is used by Game Tree Search
        implementation.
    """

    def __init__(self, size1, depthlimit=0):
        """ Initialization.
        """
        self.size = size1
        self.depth_limit = depthlimit

    def initial_state(self):
        """ Return an initial state for the game.
        """
        state = GameState(self.size)
        return state

    def is_maxs_turn(self, state):
        """ Indicate if it's Min's turn
            :return: True if it's Max's turn to play
        """
        return state.maxs_turn

    def is_mins_turn(self, state):
        """ Indicate if it's Min's turn
            :param state: a legal game state
            :return: True if it's Min's turn to play
        """
        return not state.maxs_turn

    def is_terminal(self, state):
        """ Indicate if the game is over.
            :param node: a game state with stored game state
            :return: a boolean indicating if node is terminal
        """

        if ((len(state.jedis) + len(state.rebels)) == 0):
            return True
        elif (len(state.siths) == 0):
            return True
        elif state.turn == 40:
            return True
        else:
            return False

    def rebel_move(self, rebelMoves, sith, rebels, jedis):
        """
        find all the avaliable move for rebel
        :param rebelMoves:
        :param sith:
        :param rebels:
        :param jedis:
        :return:
        """
        allmove = []
        for key in rebelMoves:
            up = True
            if rebelMoves[key][0] < 1:
                up = False
            for sKey in sith:
                if sKey == (rebelMoves[key][0] - 1, rebelMoves[key][1]):
                    up = False
            for rKey in rebels:
                if rKey == (rebelMoves[key][0] - 1, rebelMoves[key][1]):
                    up = False
            for jKey in jedis:
                if jKey == (rebelMoves[key][0] - 1, rebelMoves[key][1]):
                    up = False
            if up == True:
                allmove.append((key, rebelMoves[key][0] - 1, rebelMoves[key][1]))
        capture = []
        for i in allmove:
            templeft = (i[1], i[2] - 1)
            tempright = (i[1], i[2] + 1)
            for key in sith:
                if key == templeft:
                    capture.append((i[0], i[1], i[2] - 1))
                if key == tempright:
                    capture.append((i[0], i[1], i[2] + 1))
        for i in capture:
            allmove.append(i)
        return allmove

    def jedis_move(self, jediMoves, sith, rebels, jedis):
        """
        find all the jedis's move
        :param jediMoves:
        :param sith:
        :param rebels:
        :param jedis:
        :return:
        """
        allmove = []
        for key in jediMoves:
            # add up move
            row = jediMoves[key][0]
            keepGoing = True
            while keepGoing == True:
                if (row - 1 < 1):
                    keepGoing = False
                elif ((row - 1, jediMoves[key][1]) in rebels):
                    keepGoing = False
                elif ((row - 1, jediMoves[key][1]) in jedis):
                    keepGoing = False
                if (keepGoing == True):
                    allmove.append((key, row - 1, jediMoves[key][1]))
                    if ((row - 1, jediMoves[key][1]) in sith):
                        keepGoing = False
                row -= 1
            # add down move
            row = jediMoves[key][0]
            keepGoing = True
            while keepGoing == True:
                if (row + 1 > self.size):
                    keepGoing = False
                elif ((row + 1, jediMoves[key][1]) in rebels):
                    keepGoing = False
                elif ((row + 1, jediMoves[key][1]) in jedis):
                    keepGoing = False
                if (keepGoing == True):
                    allmove.append((key, row + 1, jediMoves[key][1]))
                    if ((row + 1, jediMoves[key][1]) in sith):
                        keepGoing = False
                row += 1
            col = jediMoves[key][1]
            # go right
            keepGoing = True
            while keepGoing == True:
                if (col + 1 > self.size):
                    keepGoing = False
                elif ((jediMoves[key][0], col + 1,) in rebels):
                    keepGoing = False
                elif ((jediMoves[key][0], col + 1,) in jedis):
                    keepGoing = False
                if (keepGoing == True):
                    allmove.append((key, jediMoves[key][0], col + 1))
                    if ((jediMoves[key][0], col + 1,) in sith):
                        keepGoing = False
                col += 1
            # go left up
            row = jediMoves[key][0]
            col = jediMoves[key][1]
            while keepGoing == True:
                if (col - 1 < 1 or row - 1 < 1):
                    keepGoing = False
                elif ((row - 1, col - 1,) in rebels):
                    keepGoing = False
                elif ((row - 1, col - 1,) in jedis):
                    keepGoing = False
                if (keepGoing == True):
                    allmove.append((key, row - 1, col - 1))
                    if ((row - 1, col - 1,) in sith):
                        keepGoing = False
                col -= 1
                row -= 1
            # go right up
            row = jediMoves[key][0]
            col = jediMoves[key][1]
            while keepGoing == True:
                if (col + 1 > self.size or row - 1 < 1):
                    keepGoing = False
                elif ((row - 1, col + 1,) in rebels):
                    keepGoing = False
                elif ((row - 1, col + 1,) in jedis):
                    keepGoing = False

                if (keepGoing == True):
                    allmove.append((key, row - 1, col + 1))
                    if ((row - 1, col + 1,) in sith):
                        keepGoing = False
                col += 1
                row -= 1
            # go left down
            row = jediMoves[key][0]
            col = jediMoves[key][1]
            while keepGoing == True:
                if (col - 1 < 1 or row + 1 > self.size):
                    keepGoing = False
                elif ((row + 1, col - 1,) in rebels):
                    keepGoing = False
                elif ((row + 1, col - 1,) in jedis):
                    keepGoing = False

                if (keepGoing == True):
                    allmove.append((key, row + 1, col - 1))
                    if ((row + 1, col - 1,) in sith):
                        keepGoing = False
                col -= 1
                row += 1
            # go right down
            row = jediMoves[key][0]
            col = jediMoves[key][1]
            while keepGoing == True:
                if (col + 1 > self.size or row + 1 > self.size):
                    keepGoing = False
                elif ((row + 1, col + 1,) in rebels):
                    keepGoing = False
                elif ((row + 1, col + 1,) in jedis):
                    keepGoing = False

                if (keepGoing == True):
                    allmove.append((key, row + 1, col + 1))
                    if ((row + 1, col + 1,) in sith):
                        keepGoing = False
                col += 1
                row += 1
        return allmove

    def actions(self, state):
        """ Returns all the legal actions in the given state.
            :param state: a state object
            :return: a list of actions legal in the given state
        """
        if (state.maxs_turn):
            sith = []
            rebelMoves = dict(state.rebels)
            for sKey in state.siths:
                sith.append(state.siths.get(sKey))
            rebels = []
            for rKey in rebelMoves:
                rebels.append(state.rebels.get(rKey))
            jedis = []
            for jKey in state.jedis:
                jedis.append(state.jedis.get(jKey))
            allmove = self.rebel_move(rebelMoves, sith, rebels, jedis)
            # add jedis moves
            jediMoves = dict(state.jedis)
            jedimove = self.jedis_move(jediMoves, sith, rebels, jedis)
            allmove = allmove + jedimove
            return allmove
        else:
            sithMoves = dict(state.siths)
            allMove = []
            for key in sithMoves:
                allMove.append((key, sithMoves[key][0] - 1, sithMoves[key][1]))
                allMove.append((key, sithMoves[key][0] + 1, sithMoves[key][1]))
                allMove.append((key, sithMoves[key][0], sithMoves[key][1] - 1))
                allMove.append((key, sithMoves[key][0], sithMoves[key][1] + 1))
                allMove.append((key, sithMoves[key][0] - 1, sithMoves[key][1] + 1))
                allMove.append((key, sithMoves[key][0] - 1, sithMoves[key][1] - 1))
                allMove.append((key, sithMoves[key][0] + 1, sithMoves[key][1] + 1))
                allMove.append((key, sithMoves[key][0] + 1, sithMoves[key][1] - 1))
            remove = []
            for i in allMove:
                if (i[1] < 1 or i[1] > state.size):
                    remove.append(i)
                elif (i[2] < 1 or i[2] > state.size):
                    remove.append(i)
                for k in sithMoves:
                    if sithMoves.get(k) == i:
                        remove.append(i)
            for i in remove:
                allMove.remove(i)
            return allMove

    def result(self, state, action):
        """ Return the state that results from the application of the
            given action in the given state.
            :param state: a legal game state
            :param action: a legal action in the game state
            :return: a new game state
        """
        # make a clone of this state
        new_state = state.myclone()
        who = state.maxs_turn
        where = action
        capture = False
        key = where[0]
        # update the clone state, using the information in action
        if where[0][:1] == 'r':
            oldL = new_state.rebels.get(key)
            new_state.gameState[oldL] = state._ablank
            new_state.rebels[key] = (where[1], where[2])
            new_state.gameState[(where[1], where[2])] = where[0]
            for m in new_state.siths:
                location = new_state.siths.get(m)
                if (where[1], where[2]) == location:
                    capture = True
                    kill = m
            if (capture):
                new_state.siths.pop(kill)
            if (where[1] == 1):
                del new_state.rebels[where[0]]
                newjedis = 'j' + str(len(new_state.jedis))
                new_state.jedis[newjedis] = (where[1], where[2])
                new_state.gameState[(where[1], where[2])] = newjedis
        elif where[0][:1] == 'j':
            oldL = new_state.jedis.get(key)
            new_state.jedis[key] = (where[1], where[2])
            new_state.gameState[(where[1], where[2])] = where[0]
            new_state.gameState[oldL] = state._ablank
            for m in new_state.siths:
                location = new_state.siths.get(m)
                if (where[1], where[2]) == location:
                    capture = True
                    kill = m
            if (capture == True):
                new_state.siths.pop(kill)
        elif where[0][:1] == 's':
            isJedi = False
            oldL = new_state.siths.get(key)
            for m in new_state.rebels:
                location = new_state.rebels.get(m)
                if (where[1], where[2]) == location:
                    kill = m
                    capture = True
            for m in new_state.jedis:
                location = new_state.jedis.get(m)
                if (where[1], where[2]) == location:
                    kill = m
                    isJedi = True
                    capture = True
            if (capture):
                if (isJedi):
                    new_state.jedis.pop(kill)
                    newSith = 's' + str(len(new_state.siths))
                    new_state.siths[newSith] = (where[1], where[2])
                    new_state.gameState[(where[1], where[2])] = newSith
                else:
                    new_state.rebels.pop(kill)
                    new_state.siths[key] = (where[1], where[2])
                    new_state.gameState[(where[1], where[2])] = where[0]
                    new_state.gameState[oldL] = state._ablank
            else:
                new_state.siths[key] = (where[1], where[2])
                new_state.gameState[(where[1], where[2])] = where[0]
                new_state.gameState[oldL] = state._ablank

        new_state.maxs_turn = not who
        new_state.turn += 1
        # now, finally, create the stringification
        new_state.stringified = str(new_state)
        return new_state

    def utility(self, state):
        """ Calculate the utility of the given state.
            :param state: a legal game state
            :return: utility of the terminal state
        """
        if len(state.siths) == 0:
            return 500
        elif ((len(state.jedis) + len(state.rebels)) == 0):
            return -500
        else:
            return 0

    def cutoff_test(self, state, depth):
        """
            Check if the search should be cut-off early.
            In a more interesting game, you might look at the state
            and allow a deeper search in important branches, and a shallower
            search in boring branches.

            :param state: a game state
            :param depth: the depth of the state,
                          in terms of levels below the start of search.
            :return: True if search should be cut off here.
        """
        return self.depth_limit > 0 and depth > self.depth_limit

    def eval(self, state):
        """
            When a depth limit is applied, we need to evaluate the
            given state to estimate who might win.
            state: a legal game state
            :return: a numeric value in the range of the utility function
        """
        # size = state.size
        # rebelsC = 0
        # sithC = 0
        #
        # for i in state.rebels:
        #     sithC+=10
        #     currentL = state.rebels.get(i)
        #     left = (currentL[0]-1, currentL[1]-1)
        #     right = (currentL[0]-1, currentL[1]+1)
        #     for temp in state.siths:
        #         if left == state.siths.get(temp):
        #             rebelsC+=100
        #         if right == state.siths.get(temp):
        #             rebelsC+=100
        # allMove = []
        # for i in state.siths:
        #     currentL = state.siths.get(i)
        #     allMove.append((currentL[0] - 1, currentL[1]))
        #     allMove.append((currentL[0]  + 1,currentL[1]))
        #     allMove.append((currentL[0], currentL[1] - 1))
        #     allMove.append((currentL[0], currentL[1] + 1))
        #     allMove.append((currentL[0] - 1, currentL[1] + 1))
        #     allMove.append((currentL[0] - 1, currentL[1] - 1))
        #     allMove.append((currentL[0] + 1, currentL[1] + 1))
        #     allMove.append((currentL[0] + 1, currentL[1] - 1))
        # for i in state.rebels:
        #     if i in allMove:
        #         sithC+=50
        # for p in state.jedis:
        #     if p in allMove:
        #         sithC+=100

        #return len(state.jedis) * 80 + rebelsC-sithC- len(state.siths)*50

        return len(state.jedis) * 80 + 10 * len(state.rebels) - 60 * len(state.siths)

    def congratulate(self, state):
        """ Called at the end of a game, display some appropriate
            sentiments to the console. Could be used to display
            game statistics as well.
            :param state: a legal game state
        """
        winstring = 'Congratulations, {} wins'
        if ((len(state.jedis) + len(state.rebels)) == 0):
            return winstring.format("Siths")
        elif len(state.siths) == 0:
            return winstring.format("Ribels")
        else:
            return 'No winner'
          # not really needed, but indicates the end of the method

    def transposition_string(self, state):
        """ Returns a unique string for the given state.  For use in
            any Game Tree Search that employs a transposition table.
            :param state: a legal game state
            :return: a unique string representing the state
        """
        return state.stringified