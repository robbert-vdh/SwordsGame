# Tested with RB [_1000_] (http://ci.bukkit.org/job/dev-CraftBukkit/1000/)
## Dependencies:
-   Permissions 3 (or you could use SuperPerms)
-   BukkitContrib (will be downloaded automatically)

## Permission nodes:
-   swordsgame.define - Define/remove arenas and be able to modify existing arenas
-   swordsgame.play - Play SwordsGame and list games/arenas
-   (SuperPerms)swordsgame.* - This will give acces to the above two permission nodes

## Todo:
-   Some major code rewriting.
-   Make people face the center of the arena upon joining.
-   People can put out fires.
	
## Ideas:
-   Add economy support.
-   Boot people from games when they use fly- or speedhacks.

## Notes:
-   I would GREATLY appreciate it if people could help me fix the English localisation.
-   The client side Spout mod is not required, but it will make the experience better, using sounds, notes and several other things.
-   Please wall off the arenas yourself, since there is no easy way to prevent people from entering.
-   Both the killer and the killed player are send to their spawn upon a kill, to prevent camping. This can be disabled in the config.

## Changelog:

__1.2.5__

> -   You can no longer punch out fires when playing

__1.2.4__

> -   People will stay in the arena when killed by non-players (and be demoted if possible)

__1.2.3__

> -   Corrupt files should now automatically be deleted and recreated (there is no point in keeping them)

__1.2.2__

> -   Fixed SuperPerms support

__1.2.1__

> -   Migrated from BukkitContrib to Spout
-   Added an update check
-   Fixed signs not updating

__1.2__

> -   Added lobbies
-   Added lobby-only mode (enableable in the configuration file)
-   Removed some useless debug information still present

__1.1__

> -   Made the (almost) everything localizable

__1.0.2__

> -   Added SuperPerms support

__1.0.1__

> -   Fixed the configuration

__1.0__

> -   Initial release