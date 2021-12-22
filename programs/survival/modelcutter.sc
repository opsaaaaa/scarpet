

__config()->{
  'requires' -> {
    'nbtcrafting' -> '*'
  },
  'commands' -> {
    'list' -> 'list_constructors',
    '<packname> set <item> <model>' -> 'set_model_recipe',
    '<packname> range <item> <rangeMin> <rangeMax>' -> 'range_model_recipes',
    '<packname> list' -> 'list_recipes',
    '<packname> raw' -> 'read_raw_recipes',
    '<packname> build' -> 'build_datapack'
  },
  'arguments' -> {
    'packname' -> {'type' -> 'string'},
    'item' -> {'type' -> 'item'},
    'model' -> {'type' -> 'int', 'min' -> 1, 'max' -> 1000000},
    'rangeMin' -> {'type' -> 'int', 'min' -> 1, 'max' -> 1000000},
    'rangeMax' -> {'type' -> 'int', 'min' -> 1, 'max' -> 1000000}
  }
};


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

range_model_recipes(packname, item, min, max) -> (
  item = item:0;
  datapack = _read_pack(packname) || _default_datapack(packname);
  for(range(min,max),
    datapack:'data':packname:'recipes':str('%s_%d.json', item, _) = _new_model_recipe(item, _);
    print(player(), str('%s_%d.json recipe prepared!', item, _));
  );
  _write_pack(packname, datapack);
);

set_model_recipe(packname, item, model) -> (
  item = item:0;
  datapack = _read_pack(packname) || _default_datapack(packname);
  datapack:'data':packname:'recipes':str('%s_%d.json', item, model) = _new_model_recipe(item, model);
  _write_pack(packname, datapack);
  print(player(), str('%s_%d.json recipe prepared!', item, model));
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

_new_model_recipe(item, model) -> (
  print(player(),model);
  {
    'type' -> 'stonecutting',
    'ingredient' -> {
      'item' -> item
    },
    'result' -> {
      'item' -> item,
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
