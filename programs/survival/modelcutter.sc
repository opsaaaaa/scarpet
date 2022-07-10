

__config()->{
  'requires' -> {
    'nbtcrafting' -> '*'
  },
  'commands' -> {
    '' -> 'help',
    'list' -> 'list_constructors',
    '<packname> set <ingredient> <result> <model>' -> 'set_model_recipe',
    '<packname> range <ingredient> <result> <rangeMin> <rangeMax>' -> 'range_model_recipes',
    '<packname> list' -> 'list_recipes',
    '<packname> raw' -> 'read_raw_recipes',
    '<packname> build' -> 'build_datapack'
  },
  'arguments' -> {
    'packname' -> {'type' -> 'string'},
    'ingredient' -> {'type' -> 'item'},
    'result' -> {'type' -> 'item'},
    'model' -> {'type' -> 'int', 'min' -> 1, 'max' -> 1000000},
    'rangeMin' -> {'type' -> 'int', 'min' -> 1, 'max' -> 1000000},
    'rangeMax' -> {'type' -> 'int', 'min' -> 1, 'max' -> 1000000}
  }
};

help() -> (
  print(player(), join('\n',[
    '',
    'This command makes CustomModelData varients craftable in the stonecutter.',
    'For example it could make various carved pumkin hats craftable.',
    'This script requires nbtcrafting mod to function.',
    'It does this by generating a datapack containing stonecutter recipes for each model varient.',
    '',
    'Step #1. Install/ create a resource pack with custom model varients.',
    'If you are hosting a server you will want to look into server resourcepacks.',
    '',
    'Step #2. prepare the model recipes.',
    'The resource pack list all the numbers it uses for the CustomModelData.',
    'you can prepare recipes for these with one of two commands',
    '',
    '/modelcutter <packname> set <item1> <item2> <model>',
    'ie. /modelcutter pumpkin_hats set carved_pumpkin 21',
    'Run this command for each custom carved_pumpkin varient you want craftable.',
    '',
    '/modelcutter <packname> range <item1> <item2> <rangeMin> <rangeMax>',
    'ie. /modelcutter pumpkin_hats range carved_pumpkin 21 29',
    'This command will prepare a recipe for each varient between 21 and 29. 21,22,23...29 ',
    '',
    'Step #3. Once all the recipes are prepared you can build the datapack.',
    '/modelcutter <packname> build',
    'ie /modelcutter pumpkin_hats build',
    'This will create and load a datapack called pumpkin_hats.zip containing the recipes you defined',
    'There is currently no way to replace existing datapacks so make sure to use a unique packname every time.',
    ''
  ]));
);

testing() -> (
  print(player(), nbt_storage())
);

list_constructors() -> (
  print(player(), list_files('packs/', 'json'))
);

build_datapack(packname) -> (
  p = player();
  datapack = _read_pack(packname);
  if(datapack, 
    create_datapack(packname, datapack);
    _delete_pack(packname);
    print(p, str('Built %s datapack!', packname ));
  ,
    print(p,'No datapack contructor named `'+packname+'` found.');
  );
);

range_model_recipes(packname, item1, item2, min, max) -> (
  item1 = item1:0;
  item2 = item2:0;
  datapack = _read_pack(packname) || _default_datapack(packname);
  for(range(min,max),
    datapack:'data':packname:'recipes':str('%s_%s_%d.json', item1, item2, _) = _new_model_recipe(item1, item2, _);
    print(player(), str('%s_%s_%d.json recipe prepared!', item1, item2, _));
  );
  _write_pack(packname, datapack);
);

set_model_recipe(packname, item1, item2, model) -> (
  item1 = item1:0;
  item2 = item2:0;
  datapack = _read_pack(packname) || _default_datapack(packname);
  datapack:'data':packname:'recipes':str('%s_%s_%d.json', item1, item2, model) = _new_model_recipe(item1, item2, model);
  _write_pack(packname, datapack);
  print(player(), str('%s_%s_%d.json recipe prepared!', item1, item2, model));
);

read_raw_recipes(packname) -> (
  p = player();
  datapack = _read_pack(packname);
  if(datapack, 
    print(p, datapack);
  ,
    print(p,'No datapack contructor named `'+packname+'` found.');
  );
);

list_recipes(packname) -> (
  p = player();
  datapack = _read_pack(packname);
  if(datapack, 
    print(p, str('Current %s cunstructor recipes', packname));
    print(p, '---');
    for(keys(datapack:'data':packname:'recipes'),
      print(p, _);
    );
  ,
    print(p,'No datapack contructor named `'+packname+'` found.');
  );
);



_default_datapack(packname) -> (
  {'data' -> {packname -> {'recipes' -> {} }}}
);

_new_model_recipe(ingredient, result, model) -> (
  print(player(),model);
  {
    'type' -> 'stonecutting',
    'ingredient' -> {
      'item' -> ingredient
    },
    'result' -> {
      'item' -> result,
      'data' -> {
        'CustomModelData' -> number(model)
      }
    },
    'count' -> 1
  }
);



_read_pack(packname) -> (
  read_file(str('packs/%s',packname), 'json');
);

_write_pack(packname, datapack) -> (
  write_file(str('packs/%s',packname), 'json', datapack);
);

_delete_pack(packname) -> (
  delete_file(str('packs/%s',packname), 'json');
);
