import time
import asyncio

from poke_env.player import Player, RandomPlayer
from poke_env import PlayerConfiguration, ShowdownServerConfiguration
from BattleNode import BattleTree 

class MinimaxPlayer(Player):

    def choose_move(self, battle):
        depth = 3
        bt = BattleTree()
        bt.populateFromBattleState(battle)
        bt.generate(depth, True)
        print(f"Subnodes: {bt.subnodes}")

        if battle.available_moves:
            best_eval, best_move = self.minimax(bt, True)
            mvs = ""
            for i in battle.available_moves:
                mvs += f"| {str(i)} ~ {i.self_boost} |"
            print(mvs)
            print(f"Best Eval : {best_eval}\nBest Move: {best_move}")
            print("---------------------------------------------------")

            if(best_move in battle.available_moves):
                return self.create_order(best_move)
        print(f"Move Choosen: {best_move}")
        print("RANDOM MOVE")
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
    # Create player
    minimax_player = MinimaxPlayer(
        battle_format="gen5randombattle",
        player_configuration = PlayerConfiguration("bot-test-23", "password1"),
        server_configuration = ShowdownServerConfiguration
    )

    await minimax_player.accept_challenges(None, 10)

if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(main())