import time
import asyncio

from poke_env.player import Player, RandomPlayer
from BattleNode import BattleTree 

class MinimaxPlayer(Player):

    def choose_move(self, battle):
        depth = 3
        bt = BattleTree()
        bt.populateFromBattleState(battle)
        bt.generate(depth, True)

        if battle.available_moves:
            best_eval, best_move = self.minimax(bt, True)

            if(best_move in battle.available_moves):
                return self.create_order(best_move)

        return self.choose_random_move(battle)

    
        
    def minimax(self, node, maximizing_player):
        if node.subnodes == []:
            return node.staticEval(), node.last_move
        
        if maximizing_player:
            max_eval = float('-inf')
            max_move = None
            for child in node.subnodes:
                currEval, currMove = self.minimax(child, False)
                if (currEval >= max_eval):
                    max_eval = currEval
                    max_move = currMove
            return max_eval, max_move
        else:
            min_eval = float('inf')
            min_move = None
            for child in node.subnodes:
                currEval, currMove = self.minimax(child, True)
                if (currEval <= min_eval):
                    min_eval = currEval
                    min_move = currMove
            return min_eval, min_move

async def main():
    start = time.time()

    # Create two players
    random_player = RandomPlayer(battle_format="gen5randombattle",)
    minimax_player = MinimaxPlayer(battle_format="gen5randombattle",)

    # Now let the two players battle 100 times
    await minimax_player.battle_against(random_player, n_battles=100)

    print(
        "Minimax player won %d / 10 battles [this took %f seconds]"
        % (
            minimax_player.n_won_battles, time.time() - start
        )
    )

if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(main())