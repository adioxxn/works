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


class GameState(object):
    """ The GameState class stores the information about the state of the game.
    """
    
    def __init__(self):
        pass

    def myclone(self):
        """ Make and return an exact copy of the state.
        """
        return None

    def display(self):
        """
        Present the game state to the console.
        """
        print('Imagine the state being displayed here.')


class Game(object):
    """ The Game object defines the interface that is used by Game Tree Search
        implementation.
    """
    
    def __init__(self):
        """ Initialization.
        """
        pass

    def initial_state(self):
        """ Return an initial state for the game.
        """
        return None

    def is_mins_turn(self, state):
        """ Indicate if it's Min's turn
            :return: True if it's Min's turn to play
        """
        return False

    def is_maxs_turn(self, state):
        """ Indicate if it's Min's turn
            :return: True if it's Max's turn to play
        """
        return False

    def is_terminal(self, state):
        """ Indicate if the game is over.
            :param node: a game state with stored game state
            :return: a boolean indicating if node is terminal
        """
        return False

    def actions(self, state):
        """ Returns all the legal actions in the given state.
            :param state: a state object
            :return: a list of actions legal in the given state
        """
        return None

    def result(self, state, action):
        """ Return the state that results from the application of the
            given action in the given state.
            :param state: a legal game state
            :param action: a legal action in the game state
            :return: a new game state
        """

        return None

    def utility(self, state):
        """ Calculate the utility of the given state.
            :param state: a legal game state
            :return: utility of the terminal state
        """
        return None

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
        return False

    def eval(self, state):
        """
            When a depth limit is applied, we need to evaluate the
            given state to estimate who might win.
            state: a legal game state
            :return: a numeric value in the range of the utility function
        """
        return None

    def congratulate(self, state):
        """ Called at the end of a game, display some appropriate 
            sentiments to the console. Could be used to display 
            game statistics as well.
            :param state: a legal game state
        """
        pass      

    def transposition_string(self, state):
        """ Returns a unique string for the given state.  For use in 
            any Game Tree Search that employs a transposition table.
            :param state: a legal game state
            :return: a unique string representing the state
        """
        return None
        
# eof