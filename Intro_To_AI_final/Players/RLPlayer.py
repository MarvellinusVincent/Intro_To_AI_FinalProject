import numpy as np 
import os
import time
import asyncio
from gym.spaces import Space, Box
from poke_env.player import Gen5EnvSinglePlayer
from poke_env.player import background_evaluate_player
from poke_env.player import background_cross_evaluate

from maxDMGPlayer import MaxDamagePlayer
from minimaxPlayer import MinimaxPlayer
from poke_env.player import RandomPlayer

# For running players
from gym.utils.env_checker import check_env

# for defining the base model
from tensorflow.keras.layers import Dense, Flatten
from tensorflow.keras.models import Sequential, model_from_json

# for creating the DQN
from rl.agents.dqn import DQNAgent
from rl.memory import SequentialMemory
from rl.policy import LinearAnnealedPolicy, EpsGreedyQPolicy
from tensorflow.keras.optimizers.legacy import Adam
# add legacy to above line
# ref :: https://stackoverflow.com/questions/75356826/attributeerror-adam-object-has-no-attribute-get-updates



class RLPlayer(Gen5EnvSinglePlayer):
    def calc_reward(self, last_battle, current_battle) -> float:
        return self.reward_computing_helper(current_battle, fainted_value=3.5, hp_value=1.0, victory_value=35)
    
    def embed_battle(self, battle):
        # -1 indicates that the move does not have a base power
        # or is not available
        moves_base_power = -np.ones(4)
        moves_dmg_multiplier = np.ones(4)
        for i, move in enumerate(battle.available_moves):
            moves_base_power[i] = (
                move.base_power / 100
            )
            if move.type:
                moves_dmg_multiplier[i] = move.type.damage_multiplier(
                    battle.opponent_active_pokemon.type_1,
                    battle.opponent_active_pokemon.type_2
                )
            
        # Get remaining pokemon from each team and divide to rescale
        fainted_mon_team = len([mon for mon in battle.team.values() if mon.fainted]) / 6
        fainted_mon_opponent = (len([mon for mon in battle.opponent_team.values() if mon.fainted]) / 6)

        # Get the active pokemon's attacking boosts
        atk_boost = battle.active_pokemon.boosts['atk']
        spa_boost = battle.active_pokemon.boosts['spa']

        # Final vector with 12 features
        final_vector = np.concatenate([moves_base_power, moves_dmg_multiplier, [atk_boost, spa_boost], [fainted_mon_team, fainted_mon_opponent]])

        return np.float32(final_vector)

    def describe_embedding(self) -> Space:
        low = [-1, -1, -1, -1, -6, -6, 0, 0, 0, 0, 0, 0] # lowest values for each feature
        high = [3, 3, 3, 3, 6, 6, 4, 4, 4, 4, 1, 1]      # highest values for each feature
        return Box(np.array(low, dtype=np.float32), np.array(high, dtype=np.float32), dtype=np.float32)

def createDQN(model, n):
    # used to generate the DQN
    memory = SequentialMemory(limit=10000, window_length=1)

    policy= LinearAnnealedPolicy(
        EpsGreedyQPolicy(),
        attr="eps",
        value_max=1.0,
        value_min=0.05,
        value_test=0.0,
        nb_steps=10000
    )

    DQN = DQNAgent(
        model=model,
        nb_actions=n,
        policy=policy,
        memory=memory,
        nb_steps_warmup=1000,
        gamma=0.5,
        target_model_update=1,
        delta_clip=0.01,
        enable_double_dqn=True
    )

    DQN.compile(Adam(learning_rate=0.0025), metrics=["mae"])

    return DQN



def newModel(n_action, input_shape):
    # Create model
    model = Sequential()
    model.add(Dense(128, activation="elu", input_shape=input_shape))
    model.add(Flatten())
    model.add(Dense(64, activation="elu"))
    model.add(Dense(n_action, activation="linear"))
    return model


def main(train=False):
    opponent = RandomPlayer(battle_format="gen5randombattle")
    eval_env = RLPlayer(
        battle_format="gen5randombattle", opponent=opponent,start_challenging=True
    )


    # Get the dimensions from the RLPlayer
    n_action = eval_env.action_space.n
    input_shape = (1,) + eval_env.observation_space.shape


    if not os.path.isfile('model.h5'):
        model = newModel(n_action, input_shape)
    else:    
        json_file = open("model.json", 'r')
        loaded_json_model = json_file.read()
        json_file.close()
        model = model_from_json(loaded_json_model)
        model.load_weights('model.h5')


    dqn = createDQN(model, n_action)

    if train:
        opponent = RandomPlayer(battle_format="gen5randombattle")
        train_env = RLPlayer(battle_format="gen5randombattle", opponent=opponent,start_challenging=True)   
        # traning the model
        dqn.fit(train_env, nb_steps=10000)
        # to save
        filepath = "model.h5"
        dqn_json = model.to_json()
        with open("model.json", "w") as json_file:
            json_file.write(dqn_json)
        dqn.save_weights(filepath)
    else:
        second_opponent = MaxDamagePlayer(battle_format="gen5randombattle",)
        eval_env.reset_env(restart=True, opponent=second_opponent)
        print("Results against MaxDamagePlayer:")
        dqn.test(eval_env, nb_episodes=50, verbose=False, visualize=False)
        print(
            f"DQN Evaluation: {eval_env.n_won_battles} victories out of {eval_env.n_finished_battles} battles"
        )


async def gatherStats(n_battles):
    start = time.time()
    # Create one environment for training and one for evaluation
    opponent = RandomPlayer(battle_format="gen5randombattle")
    train_env = RLPlayer(
        battle_format="gen5randombattle", opponent=opponent, start_challenging=True
    )
    opponent = RandomPlayer(battle_format="gen5randombattle")
    eval_env = RLPlayer(
        battle_format="gen5randombattle", opponent=opponent, start_challenging=True
    )

     # Get the dimensions from the RLPlayer
    n_action = train_env.action_space.n
    input_shape = (1,) + train_env.observation_space.shape

    # Create model
    json_file = open("model.json", 'r')
    loaded_json_model = json_file.read()
    json_file.close()
    model = model_from_json(loaded_json_model)
    model.load_weights('model.h5')
    
    dqn = createDQN(model, n_action)

    random_player = RandomPlayer(battle_format="gen5randombattle")
    max_dmg_player = MaxDamagePlayer(battle_format="gen5randombattle")
    minimax_player = MinimaxPlayer(battle_format="gen5randombattle")

    win_stats = []

    print("starting RL vs X")
    # Evaluating the models ~ RL vs X
    RL_vs_x = []
    dqn.test(eval_env, nb_episodes=n_battles, verbose=False, visualize=False)
    RL_vs_x.append(eval_env.n_won_battles) # RL vs Random
    eval_env.reset_env(restart=True, opponent=max_dmg_player)
    print("finished random")

    dqn.test(eval_env, nb_episodes=n_battles, verbose=False, visualize=False)
    RL_vs_x.append(eval_env.n_won_battles) # RL vs MAXDMG
    eval_env.reset_env(restart=True, opponent=minimax_player)
    print("finished maxDMG")

    dqn.test(eval_env, nb_episodes=n_battles, verbose=False, visualize=False)
    RL_vs_x.append(eval_env.n_won_battles) # RL vs minimax
    eval_env.reset_env(restart=False)
    print("finished minimax")

    RL_vs_x.append(-1)

    print(f"RL VS X:: {RL_vs_x}")

    win_stats.append(RL_vs_x)

    print("starting Random vs X")

    # Evaluating the models ~ Random vs X
    rand_vs_x = [-1]
    random_player.reset_battles()

    await random_player.battle_against(max_dmg_player, n_battles=n_battles)
    rand_vs_x.append(random_player.n_won_battles)
    random_player.reset_battles()
    print("finished maxDMG")

    await random_player.battle_against(minimax_player, n_battles=n_battles)
    rand_vs_x.append(random_player.n_won_battles)
    random_player.reset_battles()
    print("finished minimax")
    rand_vs_x.append(-1)

    win_stats.append(rand_vs_x)

    # Evaluating the models ~ Max vs Minimax
    max_dmg_player.reset_battles()
    max_vs_x = [-1,-1]

    await max_dmg_player.battle_against(minimax_player, n_battles=n_battles)
    max_vs_x.append(max_dmg_player.n_won_battles)
    max_dmg_player.reset_battles()
    print("finished minimax")   
    max_vs_x.append(-1)

    win_stats.append(max_vs_x)

    print(win_stats)
    print(f"This took {time.time() - start} seconds")


if __name__ == '__main__':
    asyncio.get_event_loop().run_until_complete(gatherStats(10))