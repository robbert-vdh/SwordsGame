# Tested with RB [_1000_] (http://ci.bukkit.org/job/dev-CraftBukkit/1000/)
## Dependencies:
-   Permissions 3
-   BukkitContrib (will be downloaded automatically)

## Permission nodes:
-   swordsgame.define - Define/remove arenas and be able to modify existing arenas
-   swordsgame.play - Play SwordsGame and list games/arenas
-   (SuperPerms)swordsgame.* - This will give acces to the above two permission nodes

## Todo:
-   Some major code rewriting.
-   Make people face the center of the arena upon joining.
	
## Ideas:
-   Add economy support.
-   Spawn protection
-   Boot people from games when they use fly- or speedhacks.

## Notes:
-   I would GREATLY appreciate it if people could help me fix the English localisation.
-   The client side BukkitContrib mod is not required, but it will make the experience better, using sounds, notes and several other things.
-   Please wall off the arenas yourself, since there is no easy way to prevent people from entering.
-   Both the killer and the killed player are send to their spawn upon a kill, to prevent camping. This can be disabled in the config.

## Changelog:
__1.0.2__

> -   Added SuperPerms support

__1.0.1__

> -   Fixed the configuration

__1.0__

> -   Initial release