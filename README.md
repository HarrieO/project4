Appstudio - Native App - Rush Hour
=========

AppStudio minor programmeren 2013-2014 course


Description
========

  Upon launch the user is presented with a main menu, giving the options play/resume, levels.

  The player has to progress through the puzzles, meaning not all puzzles are available from the start.

  A puzzle is available if the previous puzzle is solved or skipped.

  The player is allowed to skip a puzzle after making (minimum for puzzle + 50) moves without solving it.

  When the player opts to skip a puzzle the app will complete the puzzle (as an animation) from the state the player left it in.

  The player sees the state of the game and can touch and drag the blocks to make a move. Besides the game state the player can also see the moves made thus far.

  During gameplay the player has the options to redo a move or revert to the starting state and if allowed to skip the puzzle.

  After solving a puzzle the player is shown how many moves were made and if this was an optimal solution.

  An optimal solution is a solution that moved block as little times as possible, regardless of the distance covered by a move.

  The player can view a list of all unlocked puzzles, for each displaying the length of the best solution so far. From this list a puzzle can be selected to play.


Technical
========

    The app’s UI is sized for a common smart phone (320×480 points) and supports both portrait and landscape orientation.

    The app will be implemented for Android (API 12).
    
    The implementation is based on the AndEngine.
    
    For storage of puzzles and progress sqlite3 is used.
    
    The set of puzzles is provided in a puzzles.db file present in the assets of the project.
    
    The state of the last seen puzzle will be stored in the Preferences of Android.
    
    A puzzle state is represented by a string containing the x, y, size and orientation of each car, a puzzle with two cars might look like this: "2,0,3,1;2,0,2,0". The exit of a puzzle is always located at 5,2 following the standard puzzle format.
    
    The app keeps track of all the moves made by the user, however if the  app is closed these moves will not be saved.
