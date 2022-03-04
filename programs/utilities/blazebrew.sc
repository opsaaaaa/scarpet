// Adds a utility to create brewing recipies for the nbt crafting mode
// 

// {id: "minecraft:%s", Count:1, tag:{
// CustomPotionEffects:[{Duration:9600, Id: 30, Amplifier: 3}], 
// CustomPotionColor: 5592575, display: {Name:\'{ "text":"Potion of Grace"}\'}}
// }
// minecraft:glass_bottle{display: {Name: '{ "text":"Doom"}'}}

// global_example = {
//     'type' -> 'nbtcrafting:brewing',
//     'base' -> {
//         'item' -> 'minecraft:stone'
//     },
//     'ingredient' -> {
//         'item' -> 'minecraft:stick'   
//     },
//     'result' -> {
//         'item' -> 'minecraft:player_head',
//         'data' -> {
//             'display' -> {
//                 'Name' -> 'nametaginput'
//             },
//             'SkullOwner' -> 'Siphalor'
//         }
//     }
// };





// <pack> <ingrediant> <base> <effects>
__config()->{
  'requires' -> {
    'nbtcrafting' -> '*'
  },
  'commands' -> {
    '' -> 'new_brewing_recipe_screen',
    'recipes new' -> 'new_brewing_recipe_screen',
    'recipes clear' -> 'clear_recipes',
    'recipes build' -> 'build_recipes'
  }
};

global_buttons = {
    10 -> ['base',      '_on_edit_recipe_part_clicked', 'potion',       nbt('{display: {Name: \'{ "text":"Base"}\'}, Potion: "minecraft:water"}')],
    11 -> ['ingredient','_on_edit_recipe_part_clicked', 'nether_wart',  nbt('{display: {Name: \'{ "text":"Ingredient"}\'}}')],
    12 -> ['result',    '_on_edit_recipe_part_clicked', 'splash_potion',nbt('{display: {Name: \'{ "text":"Result"}\'}, CustomPotionColor: 5592575, Potion: "minecraft:water"}')],
    16 -> ['save',      '_on_create_recipe_clicked',    'green_wool',   nbt('{display: {Name: \'{ "text":"Create"}\'}}')]
};

global_screen_ready = false;

global_recipes = [];

global_interface = {
    'type'->'nbtcrafting:brewing',
    'base'->{},
    'ingredient'->{},
    'result'->{},
};

_on_edit_recipe_part_clicked(key, screen, p, action, data) -> (
    item = inventory_get(screen,-1);
    if( item, 
        global_interface:key:'item' = item:0;
        if(item:2, 
            global_interface:key:'data' = parse_nbt(item:2);
        ,
            delete(global_interface:key:'data'); 
        );

        _pause_screen_for_editing();
        inventory_set(screen, data:'slot', item:1, item:0, item:2);
    );
);

_on_create_recipe_clicked(key, screen, p, action, data) -> (
    print(p, global_interface);
    put( global_recipes, null, global_interface , 'insert');
    print(p, global_recipes);
    close_screen(screen);
);

clear_recipes()->(
    global_recipes = [];
    global_interface:'base' = {};
    global_interface:'ingredient' = {};
    global_interface:'result' = {};
);

build_recipes()->(

);

new_brewing_recipe_screen() -> (
    screen = create_screen(player(), 'generic_9x3', format('kb Brewing Recipe'), _(screen, p, action, data) -> (
        if(global_screen_ready && data:'slot' < 26,
            btn = global_buttons:(data:'slot');
            if(btn, 
                call(btn:1, btn:0, screen, p, action, data);
            )
            'cancel'
        ); 
    ));

    _pause_screen_for_editing();
    for(pairs(global_buttons), inventory_set(screen, _:0, 1, _:1:2, _:1:3) );

);

_pause_screen_for_editing() -> (
    global_screen_ready = false;
    schedule(2,_() -> (global_screen_ready = true));
);
