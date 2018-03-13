# TraderSk
 
TraderSk let's you open merchant GUIs (the one vanilla mc villagers use) and customize everything about them.

## Features

- Let's you choose whatever items you want

- Add as many pages you want

- Let one of the input items be air

- Change and color the title

## Effects

#### Create trader
```
(create|make) [a] [merchant] trader [named] %string%
```
#### Remove a trader
```
(remove|clear) [the] [merchant] trader [named] %string%
```
#### Change items in a page
```
set items in page %number% (for|of) [merchant] trader %string% to %itemstack% as output[ item](,| and) %itemstack%[ and %-itemstack%] as input[ item[s]]
```
#### Remove a page from a trader
```
(remove|clear|delete) page %number% (from|of|for) [merchant] trader %string%
```
#### Remove all traders
```
(remove|clear|delete) all [merchant] traders
```
#### Show the trader to player
```
(open|show) [merchant] trader %string% to %player%
```
#### Change the title in the gui
```
(rename|set name [of|for]) [merchant] trader %string% to %string%
```
#### Save changes to disk (this actions is not performed automatically)
```
save all [merchant] trader[s]
```

## Pictures

<details>
  <summary>Click to expand</summary>
  <img src="https://i.imgur.com/2iSG2bb.png"></img>  
  <img src="https://i.imgur.com/5D94dj8.png"></img>  
</details>


## Example

```
command /set:
	trigger:
		create a trader named "test"
		set items in page 0 for trader "test" to 8 iron ingots as output item and gold block as input items
		set items in page 1 for trader "test" to gravel as output, sand and cobblestone as input
		set items in page 2 for trader "test" to glass block as output item and sand as input item
		send "set"

command /rename <text> <text>:
	trigger:
		set name of trader "%arg-1%" to "%colored arg-2%"

command /test:
	trigger:
		send "&f --- Removing if any" to console
		remove trader "test"
		send "&f --- DONE Removing if any" to console

		send "&f --- Creating" to console
		create a trader named "test"
		send "&f --- DONE Creating" to console

		send "&f --- Setting proper name" to console
		set name of trader "test" to "&3Test &MTrader"
		send "&f --- DONE Setting proper name" to console

		send "&f --- Adding proper" to console
		send "&f 	-- ITEM 1" to console
		set items in page 0 for trader "test" to 8 iron ingots as output item and gold block as input items
		send "&f 	-- ITEM 2" to console
		set items in page 1 for trader "test" to gravel as output, sand and cobblestone as input
		send "&f 	-- ITEM 3" to console
		set items in page 2 for trader "test" to glass block as output item and sand as input item
		send "&f --- DONE Adding proper" to console

		send "&f --- Adding too high" to console
		set items in page 9 for trader "test" to cobblestone as output and stone as input
		send "&f --- DONE Adding too high" to console

		send "&f --- Adding too low" to console
		set items in page 11 for trader "test" to cobblestone as output and dirt as input
		send "&f --- DONE Adding too low" to console

		send "&f --- Removing too high" to console
		remove page 4 for trader "test"
		send "&f --- DONE Removing too high" to console

		send "&f --- Removing too low" to console
		remove page -1 for trader "test"
		send "&f --- DONE Removing too low" to console

		#send "&f --- Removing in the middle" to console
		#remove page 1 for trader "test"
		#send "&f --- Removing in the middle" to console

		#send "&f --- Removing proper" to console
		#remove page 2 for trader "test"
		#send "&f --- Removing proper" to console

		send "&f --- Showing nonexistent trader" to console
		show trader "fake" to player
		send "&f --- DONE Showing nonexistent trader " to console

		send "&f --- Showing proper trader to nonexistent player" to console
		show trader "test" to "fake" parsed as player
		send "&f --- DONE Showing proper trader to nonexistent player" to console

		send "&f --- Showing proper trader" to console
		show trader "test" to player
		send "&f --- DONE Showing proper trader" to console

		send "&f --- Removing nonexistent trader" to console
		remove trader "fake"
		send "&f --- DONE Removing nonexistent trader" to console

		send "&fdone" to console

command /open <text>:
	trigger:
		show trader arg-1 to player

command /create <text>:
	trigger:
		create a trader named arg-1

command /set2 <text> <integer> <item> <item> [<item>]:
	trigger:
		set {_rounds} to 0
		while {_rounds} is less than arg-2:
			set items in page {_rounds} for trader arg-1 to arg-3 as output item and arg-4 and arg-5 as input items
			add 1 to {_rounds}
		send "set"

command /save:
	trigger:
		save all traders

command /sh:
	trigger:
		show trader "test" to player
```

