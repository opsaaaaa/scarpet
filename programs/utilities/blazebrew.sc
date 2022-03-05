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


//   "data": {
//     "example": {
//       "recipes": {
//         "pumpkin_1.json": {




// <pack> <ingrediant> <base> <effects>
__config()->{
  'requires' -> {
    'nbtcrafting' -> '*'
  },
  'commands' -> {
    'recipes new <recipeName>' -> 'new_brewing_recipe_screen',
    'recipes clear' -> 'clear_recipes',
    'recipes build' -> 'build_recipes'
  },
  'arguments' -> {
      'recipeName' -> {'type'->'term'}
  }
};

global_buttons = {
    10 -> ['base',      '_on_edit_recipe_part_clicked', 'potion',       nbt('{display: {Name: \'{ "text":"Base"}\'}, Potion: "minecraft:water"}')],
    11 -> ['ingredient','_on_edit_recipe_part_clicked', 'nether_wart',  nbt('{display: {Name: \'{ "text":"Ingredient"}\'}}')],
    12 -> ['result',    '_on_edit_recipe_part_clicked', 'splash_potion',nbt('{display: {Name: \'{ "text":"Result"}\'}, CustomPotionColor: 5592575, Potion: "minecraft:water"}')],
    16 -> ['save',      '_on_create_recipe_clicked',    'green_wool',   nbt('{display: {Name: \'{ "text":"Create"}\'}}')]
};

global_screen_ready = false;



global_interface = {
    'data'->{
        'type'->'nbtcrafting:brewing',
        'base'->{},
        'ingredient'->{},
        'result'->{}
    },
    'name'->null
};

global_datapack = {'data'->{
    'blazebrew_app'-> {
        'recipes'-> {}
    }
}};


_on_edit_recipe_part_clicked(key, screen, p, action, data) -> (
    item = inventory_get(screen,-1);
    if( item, 
        global_interface:'data':key:'item' = item:0;
        if(item:2, 
            global_interface:'data':key:'data' = parse_nbt(item:2);
        ,
            delete(global_interface:'data':key:'data'); 
        );

        _pause_screen_for_editing();
        inventory_set(screen, data:'slot', item:1, item:0, item:2);
    );
);

_set_new_recipe(name, recipe_data) -> (
    global_datapack:'data':'blazebrew_app':'recipes':(name+'.json') = recipe_data; 
    // print(p, global_interface);
);

_on_create_recipe_clicked(key, screen, p, action, data) -> (
    // put( global_recipes, null, global_interface , 'insert');
    _set_new_recipe(global_interface:'name', global_interface:'data' );
    // print(p, global_recipes);
    close_screen(screen);
);

clear_recipes()->(
    global_datapack:'data':'blase_brew_app':'recipes' = {};
    global_interface:'data':'base' = {};
    global_interface:'data':'ingredient' = {};
    global_interface:'data':'result' = {};
);

build_recipes()->(
    print(player(),'bleh -------------------------');
    print(player(),global_datapack);
    create_datapack('blazebrew_app', global_datapack);
);

new_brewing_recipe_screen(name) -> (
    global_interface:'name' = name;

    screen = create_screen(player(), 'generic_9x3', format('kb Brewing Recipe'), _(screen, p, action, data) -> (
        if(global_screen_ready && data:'slot' < 26,
            btn = global_buttons:(data:'slot');
            if(btn, 
                call(btn:1, btn:0, screen, p, action, data);
            );
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
