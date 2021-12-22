

__config()->{
  'requires' -> {
    'nbtcrafting' -> '*'
  },
  'commands' -> {
    '<packname> set <item> <model>' -> 'set_model_recipe',
    '<packname> list' -> 'list_recipes',
    '<packname> raw' -> 'read_raw_recipes',
    '<packname> build' -> 'build_datapack'
  },
  'arguments' -> {
    'packname' -> {'type' -> 'string'},
    'item' -> {'type' -> 'item'},
    'model' -> {'type' -> 'int', 'min' -> 1, 'max' -> 1000000}
  }
};


// help() -> (
//   print(player(), 
//     '`/modelcutter` adds stonecutter recipes with the CustomModelData tag. 
//     A resourcepack is required for this to have a visual effect.');
//   print(player(), 
//     '`/modelcutter add <datapackName> <item> <model>`');
//   print(player(), 
//     '`/modelcutter range <datapackName> <item> <maxModel> <minModel>`');
// );

build_datapack(packname) -> (
  p = player();
  datapack = read_file(packname, 'json');
  if(datapack, 
    create_datapack(packname, datapack);
    delete_file(packname, 'json');
    print(p, str('Built %s datapack!', packname ));
  ,
    print(p,'No datapack contructor named `'+packname+'` found.');
  );
);


set_model_recipe(packname, item, model) -> (
  item = item:0;
  datapack = read_file(packname, 'json') || _default_datapack(packname);
  datapack:'data':packname:'recipes':str('%s_%d.json', item, model) = _new_model_recipe(item, model);
  write_file(packname, 'json', datapack);
  print(player(), str('%s_%d.json prepared!', item, model));
);

read_raw_recipes(packname) -> (
  p = player();
  datapack = read_file(packname, 'json');
  if(datapack, 
    print(p, datapack);
  ,
    print(p,'No datapack contructor named `'+packname+'` found.');
  );
);


list_recipes(packname) -> (
  p = player();
  datapack = read_file(packname, 'json');
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

// _command_thing() -> (
//   datapack = {};
//   put(
//     datapack, 
//     'data.'+packname+'.recipes.'+replace(item,'.+:','')+'_model_'+model+'.json', 
//     _new_model_recipe(item, model)  
//   );
// );

// _add_model_recipe(name, item, model) -> (
//   item_name = replace(item,'.+:','');
//   create_datapack(name+item+model, {
//     'data' -> { name -> { 'recipes' -> { item_name+'_model_'+model+'.json' -> {
//       'type' -> 'stonecutting',
//       'ingredient' -> {
//         'item' -> item
//       },
//       'result' -> {
//         'item' -> item,
//         'data' -> {
//           'CustomModelData' -> model
//         }
//       },
//       'count' -> 1
//     } } } }
//   });
// );

// __initalize() -> (

//   for(range(21,29), 
//     if( 
//       _add_model_recipe('pumpkin_hats', 'minecraft:carved_pumpkin', _);
//       // print(player(), 'hat_'+_+'.json');
//       // create_datapack('pumkin_hat_'+_, {
//       //   'data' -> { 'hats' -> { 'recipes' -> { 'hat_'+_+'.json' -> {
//       //     'type' -> 'stonecutting',
//       //     'ingredient' -> {
//       //       'item' -> 'minecraft:carved_pumpkin'
//       //     },
//       //     'result' -> {
//       //       'item' -> 'minecraft:carved_pumpkin',
//       //       'data' -> {
//       //         'CustomModelData' -> _
//       //       }
//       //     },
//       //     'count' -> 1
//       //   } } } }
//       // });
//     ,
//       print(player(), _+' worked!')
//     ,
//       print(player(), _+' failed!');
//     );
//   );

// );



// __initalize();
