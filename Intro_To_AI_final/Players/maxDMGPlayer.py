import time
import asyncio

from poke_env.player import Player, RandomPlayer

## Player object that always chooses move with max damage
## https://poke-env.readthedocs.io/en/latest/max_damage_player.html#max-damage-player


class MaxDamagePlayer(Player):
    # One Abstract method ~~ choose_move(self, battle:Battle) -> str
    def choose_move(self, battle):
        # Battle describes the current battle state, has attributes like:active_pokemon, available_moves, available_switches, opponent_active_pokemon, opponent_team and team
        
        # if the player can attack, it will
        if battle.available_moves: 
            # Finds move from avaliable moves with highest base power
            best_move = max(battle.available_moves, key=lambda move: move.base_power)
            return self.create_order(best_move)
        
        # if no attack can be made, make a random switch
        else:
            return self.choose_random_move(battle)
        

async def main():
    start = time.time()

    # Create two players
    random_player = RandomPlayer(
        battle_format = "gen5randombattle",
    )

    max_damage_player = MaxDamagePlayer(
        battle_format="gen5randombattle",
    )

    # Now let the two players battle 100 times
    await max_damage_player.battle_against(random_player, n_battles=100)

    print(
        "Max damage player won %d / 100 battles [this took %f seconds]"
        % (
            max_damage_player.n_won_battles, time.time() - start
        )
    )

if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(main())
