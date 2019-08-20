## Welcome
Hi and thanks for visiting the github for Ollsentials.
Please find all the permissions below with their descriptions and usages.

On first load, the server will create 3 groups. One being an admin group, one a member and the other a guest. 
You will need to log into the server, open the config file and find your userId then change your group from guest to admin,
in order to gain access to the commands listed below.

I made this plugin so that people only have to download one plugin for a permissions manager and basic parts from the
Essentials plugin for a semi-vanilla experience.

## permissions:
- chat.color - talk in colour in chat
- admin.rights - admin commands (BAN,UNBAN, KICK)
- admin.basic - basic admin commands (set spawn, set name, teleport players)
- admin.group - group commands for admin (creategroup, addpermissions, addusertogroup, removeusertogroup)
- admin.sign - create signs ([BUY], [SELL], [PLAY])
- admin.hammer - lets you destroy/place blocks in spawn area.
- admin.name - lets you set your name/other player's names
- vip.name - lets you set your name.
- basic.home - home commands to set home, use home and use /spawn command.
- basic.sign - use of signs ([BUY], [SELL], [PLAY]).

##Sign syntax:
- Can use color codes to change color of sign type. Ie: &6[BUY]
-[BUY]:
	MATERIALNAME
	AMOUNT
	PRICE
-[SELL]:
	MATERIALNAME
	AMOUNT
	PRICE
-[PLAY]:
	MINIGAMENAME
	PRICE
	
##Current minigames:
- TARGET: This is a target practice minigame. Hit the target (needs to be either Yellow, Red, Green or White wool blocks. White wool gives 5 points, Red wool gives 10 points, Green wool gives 20 points and Yellow wool gives 50 points, points are awarded as balance on their player file.) Users must right click play sign to being playing the minigame.

### 19/08/2019 - Updated plugin to 1.14.4 > OnEnable now calls all groups to be created > command /name now functional for admins and vips
### 19/08/2019 - Buy/Sell signs now implemented.
### 19/08/2019 - Players can no longer place/break blocks in spawn radius.
### 20/08/2019 - Target Practice minigame implemented, can be placed anywhere but needs a [PLAY] sign to use > Spawn world protection fixed.

