

global_lang = 'en';

global_spells = {
  'home'->{
    'title'->'Lets Go Home',
    'command'->'/say To bad looser',
    'tooltip'->'tp 730 79 -240',
    'owner'->'TinkerSwan',
    'tags'->['warp'],
    'v'->0
  }
};


__config()->{
  'command_permission' -> 1, 
  'commands' -> {
    'list'->['list', 1], 
    'list <page>'->'list', 
    'run <spell>' -> 'run_spell',
    'set <spell> <command>' -> 'set_spell',
    'add <spell> <command>' -> 'set_spell',
    'delete <spell>' -> ['delete_spell', false],
    'delete <spell> confirm' -> ['delete_spell', true],
    // 'warp <spell>' -> 'set_warp_at_player',
    // 'warp <spell> at <location> in <dimension>' -> 'set_warp',
    // 'forceload <spell> <from> <to>' -> 'set_forceload_at_player',
    // 'forceload <spell> <from> <to> in <dimension>' -> 'set_forceload',
    // 'bot <spell> <bot>' -> 'set_bot_at_player',
    // 'bot <spell> <bot> at <location> in <dimension>' -> 'set_bot',
    // 'remove <spell>' -> 'delete_command',
    'tag <spell> <tag>' -> ['set_detail', 'color'],

    'color <spell> <color>' -> ['set_detail', 'color'],
    'tooltip <spell> <tooltip>' -> ['set_detail', 'tooltip'],
    'owner <spell> <owner>' -> ['set_detail', 'owner'],
    'title <spell> <text>' -> ['set_detail', 'title']
  },
  'arguments' -> {
    'owner' -> {
      'type' -> 'players',
      'single'->true
    },
    'command' -> {
      'type' -> 'text',
      'suggest' -> ['/tp @p x y z', '/gamerule doFireTick true']
    },
    'spell' -> {
      'type' -> 'term',
      'suggester' -> _(args) -> (
        options = ['fire_off', 'summon_bob', 'teleport_homeland', 'load_iron_farm'];
        put(options, -1, keys(global_spells), 'extend');
      )
    },
    'chapter' -> {
      'type' -> 'term',
      'options' -> ['main', 'basics', 'shorthands', 'customize', 'commands']
    },
    'page' -> {
      'type' -> 'int',
      'suggest' -> [1,2,3,5]
    },
    'tooltip' -> {
      'type' -> 'text',
      'suggest' -> ['Spawn a witch', 'Turn Fire Tick on']
    },
    'color' -> {
      'type' -> 'string',
      'suggest' -> [
        'dark_red', 'red', 'gold', 'yellow', 'dark_green',
        'green', 'aqua', 'dark_aqua', 'dark_blue', 'blue',
        'light_purple', 'dark_purple', 'white', 'gray', 'dark_gray',
        'black', '"#000000"', '"#ffffff"'
      ]
    },
    'from' -> {'type' -> 'columnpos'},
    'to' -> {'type' -> 'columnpos'},
    'bot' -> {'type' -> 'term', 'suggest'-> ['Alex', 'Steve']},
    'location' -> {'type' -> 'location'},
    'dimension' -> {'type' -> 'dimension'}
  }
};

list(page) -> (
  _chat(reduce(keys(global_spells),
    _a += ('b '+_+'\n');
  ,[]));
);

delete_spell(spell, force) -> (
  if(force,
    delete(global_spells, spell);
    _info('did_delete_spell', spell);
  ,
    _info('should_delete_spell', spell);
  );
);

set_detail(spell, val, detail) -> (
  if(has(global_spells, spell),
    global_spell:spell:detail = val;
    global_spell:spell:'v' += 1;
    _info('set_detail', [detail, spell, val])
  ,
    _error('no_spell', spell);
  )
);

run_spell(spell) -> (
  if(player()~'permission_level' >= 2 && has(global_spells, spell),
    run(replace(global_spells:spell:'command', '^/', ''));
  ,
    _error('cant_run_spells', null);
  )
);

set_spell(spell,command) -> (
  if(!has(global_spell, spell), global_spell:spell = {});

  data = _default_spell(spell, command) + global_spells:spell + {
    'key'->spell,
    'command'->command
  };

  data:'v' += 1;

  global_spells:spell = data;
  print(i18n('info','set_spell'));
  _info('set_spell', [spell, command]);
);

_default_spell(spell,command)->{
  'title'->replace(spell, '[-_]', ' '),
  'command'->command,
  'tooltip'->command,
  'color'->'dark_purple',
  'owner'->player()~'command_name',
  'v'->0,
  'tags'->[]
};




//--- MESSAGES and CHAT DISPLAY ---//

_info(info, params)->_msg('mb '+i18n('info',info), params);

_error(err, params)->_msg('br '+i18n('error',err), params);

_msg(msg, params)->print(player(),format(str(msg, params)));

_chat(lines)->print(player(),format(lines));

i18n(root,code) -> ({
  'en'->{
    'error'->{
      'no_spell'->'No spell called %s found.',
      'cant_run_spells'->'Sorry only opped players can run spells directly.',
      null->'i18n not found...'
    },
    'info'->{
      'set_detail'->'set "%s" for "%s" to "%s".',
      'set_spell'->'set spell "%s" to command "%s".',
      'did_delete_spell'->'Deleted "%s" spell forever.',
      'should_delete_spell'->'Are you sure you want to delete "%s"?',
      null->'i18n not found...'
    }
  }
}:global_lang:root:code);


// _render_page(lines) -> (
//   lines = [];
//   encode_nbt({
//     'pages'->[encode_json([
//       '',
//       'something\n',
//       'nice\n',
//       'to read\n',
//       {'text'->'someting else\n'},
//       { 'text'->'[say hi]',
//         'clickEvent'->{
//           'action'->'copy_to_clipboard',
//           'value'->'/say hi'
//         },
//         'hoverEvent'->{'action'->'show_text','contents'->'/say hi'}
//       }
//     ])],
//     'title'->'Testing',
//     'author'->'http://minecraft.tools/',
//     'display'->{
//       'Lore'->['here here']
//     }
//   });
// );
