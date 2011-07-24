# Tested with RB [_1000_] (http://ci.bukkit.org/job/dev-CraftBukkit/1000/)
## Dependencies:
-   Permissions 3
-   BukkitContrib (will be downloaded automatically)

## Permission nodes:
-   swordsgame.define - Define/remove arenas and be able to pass through existing arenas. (and modify them)
-   swordsgame.play - Play SwordsGame and list games/arenas

## Todo:
-   Some major code rewriting.
-   Boot people from games when they use fly- or speedhacks.
-   Add configs for various things.
-   Add a playerlist when joining games, and notify people that someone has joined their game.
-   Start the game when two people have joined.
-   Make people face the center of the arena when joining.
-   Block commands while in-g+ame.

## Ideas:
-   Add iConomy support.
-   Spawn protection

## Notes:
-   I would GREATLY appreciate it if people could help me fix the English localisation.
-   The client side BukkitContrib mod is not required, but it will make the experience better, using sounds, notes and several other things.
-   There's currently a limit of four people per game, but this will be customizable in the future.
-   Please wall off the arenas yourself, since there is no easy way to prevent people from entering.
-   Both the killer and the killed player are send to their spawn upon a kill, to prevent camping.