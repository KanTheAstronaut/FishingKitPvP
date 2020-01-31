# FishingKitPvP
Fishing rods now pull people towards the attacker (1.8.x). Highly customizable with many features.

## This plugin is protected under the Apache 2.0 License

<p align="center">
 <img src="https://i.gyazo.com/5abb66187b6ea081c35d65c251d664d6.png">
</p>

## Config:
```
#Controls the speed a victim is pushed towards the attacker (MUST BE A DOUBLE)
speed: 1.5
#whether it should tp or drag player (drag is calculated from attacker location and may cause the victim to fly farther)
drag: false
#Send message to victim? (%player% is replaced with player name (its case sensitive!)) (Color codes work use '&')
victimmsg: false
victimmsgi: "%player% is pulling you!"
#Send message to attacker? (%player% is replaced with player name (its case sensitive!)) (Color codes work use '&')
attackermsg: false
attackermsgi: "You are pulling %player%!"
#Use cooldown (Set to 0 to disable)
cooldown: 7
#Pull range
range: 17

#IF NOT USING DRAG
#TP above attacker by how many blocks
height: 2
```
## Features:
- High customizability
- Drag and tp
- Reload command
- Custom messages
- Cooldown

## Permissions:
- fishingkitpvp.use - Players fishing other players without this permission will result in nothing happening
- fishingkitpvp.exempt - Players with this permission can't be fished
- fishingkitpvp.reload - Players with this permission can use the config reload command

## Commands:
- /fishingkitpvpreload - fishingkitpvp.reload - Reloads the config
###### Aliases:
- /fishingreload
- /fishingkitpvp
