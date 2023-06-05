# B351 Final Project
Final project for IU Intro to Artificial Intelligence
# Problem Space
Pokemon is a turn-based, team building game franchise created by Nintendo, in which players, formally
called trainers, must build a team of six ”Pokemon”
to battle against other trainers. A faithful opensource project known as Pokemon Showdown simulates the battling aspect of the original games. With
our project we would like to formulate a few bots of
increasing complexity which implement various algorithms we have been exposed to in class. Our end
goal is to have a bot that can battle against online
users with moderate success.

# Contributers
- Trevor Cunningham
- Marvellinus Vincent
- Luke Yarian
- Logan Warner

# Libraries & Dependencies 
- [Python 3.7 or greater](https://www.python.org/downloads/) 
- [Poke-env](https://poke-env.readthedocs.io/en/latest/)
- [Node.js v10+](https://nodejs.org/en/download)
- [Smogon's Pokemon Showdown](https://github.com/smogon/Pokemon-Showdown)
- [Numpy](https://numpy.org/)
- [TensorFlow](https://www.tensorflow.org/)
- [OpenAI Gym](https://www.gymlibrary.dev/)

# Steps to run on local machine
- Download poke-env
   ```
   pip install poke_env==0.5.0
   ```
- Clone this repo
   ```
   gh repo clone TrevorC64/B351_Final_Project
   ```
- In the root folder, install and initialize Pokemon Showdown with Node.js
  ```
  git clone https://github.com/smogon/pokemon-showdown.git
  cd pokemon-showdown
  npm install
  cp config/config-example.js config/config.js
  ```
- To start the local Showdown server
  ```
  node pokemon-showdown start --no-security
  ``` 
- To run the first bot, navigate to /Players/ and run
  ```
  python3 maxDMGPlayer.py
  ```
 
# Acknowledgements
Player classes were structured from the starter examples found [here](https://poke-env.readthedocs.io/en/latest/cross_evaluate_random_players.html) on the Poke-env documentation.
