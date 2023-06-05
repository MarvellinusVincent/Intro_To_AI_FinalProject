import time
import asyncio

from poke_env.player import Player, RandomPlayer
from poke_env import PlayerConfiguration, ShowdownServerConfiguration

## Player object that always chooses move with max damage
## https://poke-env.readthedocs.io/en/latest/max_damage_player.html#max-damage-player

## bot-test-23
## password1

class MaxDamagePlayer(Player):

    # One Abstract method ~~ choose_move(self, battle:Battle) -> str
    def choose_move(self, battle):
        # Battle describes the current battle state, has attributes like:active_pokemon, available_moves, available_switches, opponent_active_pokemon, opponent_team and team
        print(battle.active_pokemon)
        print(battle.active_pokemon.status)
        print(battle.active_pokemon.effects)
        print(battle.active_pokemon.boosts)
        
        for i in range(len(battle.available_moves)):
            print(f" {battle.available_moves[i]} {battle.available_moves[i].base_power}")
        # if the player can attack, it will
       #if battle.available_moves: 
       #     # Finds move from avaliable moves with highest base power
       #     for i in range(len(battle.available_moves)):
       #         print(f" {battle.available_moves[i]} {battle.available_moves[i].base_power}")
#
 #           best_move = max(battle.available_moves, key=lambda move: move.base_power)
  #          return self.create_order(best_move)
        
        # if no attack can be made, make a random switch
   #     else:
        return self.choose_random_move(battle)
  

async def main():

    # Create player
    max_damage_player = MaxDamagePlayer(
        battle_format="gen5randombattle",
        player_configuration = PlayerConfiguration("bot-test-23", "password1"),
        server_configuration = ShowdownServerConfiguration
    )

    await max_damage_player.accept_challenges(None, 1)

if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(main())
