

//--- GLOBALS ---//

global_app = system_info('app_name');
global_lang = 'en';

global_spells = {
  'home'->{
    'key'->'home',
    'title'->'Lets Go Home',
    'command'->'/say To bad looser',
    'tooltip'->'tp 730 79 -240',
    'owner'->'TinkerSwan',
    'tags'->['warp'],
    'v'->0
  }
};

global_tags = {
  'warp'->['home']
};



//--- DATA METHODS ---//

_locales(lang, root, code)->({
  'en'->{
    'error'->{
      'no_spell'->'No spell called "%s" found.',
      'cant_run_spells'->'Only opped players can run spells directly.',
      'cant_delete'->'Only opped players or the owner of the spell can delete it.',
      'cant_change'->'Only opped players or the owner change the "%s".',
      'no_player_found'->'No player named "%s" was found online.'
    },
    'info'->{
      'set_detail'->'set "%s" for "%s" to "%s".',
      'set_spell'->'set spell "%s" to command "%s".',
      'did_delete_spell'->'Deleted "%s" spell forever.',
      'should_delete_spell'->'Are you sure you want to delete "%s"?',
      'added_tags'->'added tags "%s" to the "%s" spell.',
      'removed_tags'->'removed tags "%s" from the "%s" spell.'
    },
    'shorthand_title'->{
      'warp'->'%s',
      'forceload_add'->'%s +',
      'forceload_remove'->'%s -',
      'bot_spawn'->'%s +',
      'bot_kill'->'%s -'
    },
    'shorthand_tooltip'->{
      'warp'->'Warp to %s in %s',
      'forceload_add'->'forceload chunks from %s to %s in %s',
      'forceload_remove'->'unload chunks from %s to %s in %s',
      'bot_spawn'->'Spawn %s bot at %s in %s',
      'bot_kill'->'Kill %s bot'
    },
    'btn'->{
      'edit'->'[Edit]',
      'title'->'[Title]',
      'tooltip'->'[Tooltip]',
      'color'->'[Color]',
      'owner'->'[Owner]',
      'run'->'(Run)',
      'tags'->'[Tags]'
    }
  }
}:lang:root:code);

_color_labels()->[
  'aqua', 'dark_aqua',
  'black',
  'blue', 'dark_blue',
  'gold',
  'gray', 'dark_gray',
  'green', 'dark_green',
  'dark_purple', 'light_purple',
  'red', 'dark_red',
  'white',
  'yellow'
];

_shorthand_command(code)->({
  'warp'->'/execute as @p in %s run tp %s',
  'forceload_add'->'/execute in %s run forceload add %s %s',
  'forceload_remove'->'/execute in %s run forceload remove %s %s',
  'bot_spawn'->'/player %s spawn at %s facing 1 1 in %s',
  'bot_kill'->'/player %s kill',
  'gamerule_true'->'/gamerule %s true',
  'gamerule_false'->'/gamerule %s false'
}:code);



//--- TINY LIB STYLE METHODS ---//

uniq(list)->(keys(m(... list)));

merge(out, ... lists)->(
  for(lists,
    put(out, -1, _, 'extend');
  );
  out
);

titlize(s)->(replace(s, '[-_]', ' '));

//--- CONFIG ---//

__config()->{
  'command_permission' -> 1, 
  'commands' -> {

    'list'->['list', 1, 'all'], 
    'list <page>'->['list', 'all'], 
    'list <page> <spelltag>'->'list', 

    'run <spell>' -> 'run_spell',

    'set <spell> <command>' -> 'set_spell',
    'add <spell> <command>' -> 'set_spell',
    'delete <spell> ' -> 'ask_delete_spell',
    'delete <spell> confirm' -> 'confirm_delete_spell',

    // 'warp <spell>' -> 'set_warp_at_player',
    // 'warp <spell> at <location> in <dimension>' -> 'set_warp',
    // 'forceload <spell> <from> <to>' -> 'set_forceload_at_player',
    // 'forceload <spell> <from> <to> in <dimension>' -> 'set_forceload',
    // 'bot <spell>' -> 'set_bot_at_player',
    // 'bot <spell> <bot> at <location> in <dimension>' -> 'set_bot',

    '<spell> tags <spelltags>' -> 'set_tags',
    '<spell> color <color>' -> ['set_detail', 'color'],
    '<spell> tooltip <tooltip>' -> ['set_detail', 'tooltip'],
    '<spell> owner <owner>' -> 'set_owner',
    '<spell> title <text>' -> ['set_detail', 'title']
  },
  'arguments' -> {
    'spelltag'-> {
      'type'->'term',
      'suggester' -> _(args) -> (keys(global_tags))
    },
    'spelltags' -> {
      'type' -> 'text',
      'suggester' -> _(args) -> (
        if(has(args, 'spelltags'),
          tags = split(' +',args:'spelltags');
          search = tags:(length(tags) - 1);
          delete(tags,length(tags) - 1);
          map(keys(global_tags), join(' ',tags)+' '+_);
        ,//else
          ['tag1 tag2 tag3'];
        )
      )
    },
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
        merge(keys(global_spells),
          ['fire_off', 'spider_farm_bot', 'teleport_homeland', 'load_iron_farm'])
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
      'suggest' -> merge(_color_labels(), ['"#000000"', '"#ffffff"'])
    },
    'from' -> {'type' -> 'columnpos'},
    'to' -> {'type' -> 'columnpos'},
    'bot' -> {'type' -> 'term', 'suggest'-> ['Alex', 'Steve']},
    'location' -> {'type' -> 'location'},
    'dimension' -> {'type' -> 'dimension'}
  }
};


//--- COMMAND ENTRY POINTS ---//

list(page, spelltag) -> (
  spells = spells_by_tag(spelltag);

  print([page, spelltag, spells]);

  for(spells,
    _spell_editor(_);
  );
  // command color tags tooltip owner title
  // _chat(reduce(spells,
  //   _a += ('b '+_+'\n');
  // ,[]));
);


set_tags(spell, tags)->(
  if( _has_error_no_spell(spell) ||
      _has_error_cant_change(player(), spell, 'tags'),
      return());

  _set_detail(spell, split(' +',tags), 'tags');

  _info('set_tags', tags, spell);
);

ask_delete_spell(spell) -> (
  _info('should_delete_spell', spell);
);

confirm_delete_spell(spell)->(
  if( _has_error_no_spell(spell) ||
      _has_error_cant_delete(player(),spell),
      return());

  delete(global_spells, spell);
  _info('did_delete_spell', spell);
);

set_detail(spell, val, detail) -> (
  if( _has_error_no_spell(spell) ||
      _has_error_cant_change(player(), spell, detail),
      return());

  _set_detail(spell, val, detail);

  _info('set_detail', detail, spell, val)
);

set_owner(spell, owner_name) -> (
  owner = player(owner_name);
  if( _has_error_no_spell(spell) ||
      _has_error_cant_change(player(), spell, 'owner') ||
      _has_error_no_player(owner, owner_name),
  return());

  _set_detail(spell,owner_id(owner),'owner');

  _info('set_detail', 'owner', spell, owner);
);

run_spell(spell) -> (
  if( _has_error_cant_run(player()) ||
      _has_error_no_spell(spell), 
      return());

  run(replace(global_spells:spell:'command', '^/', ''));
);

set_spell(spell,command) -> (
  if( has(global_spell, spell) &&
      _has_error_cant_change(player(), spell, 'command'),
      return());
  

  _update_or_create_spell(spell, {
    'key'->spell,
    'command'->command
  });

  _info('set_spell', spell, command);
);

//--- SET STUFF ---//

_set_detail(spell, val, detail) -> (
  global_spell:spell:detail = val;
  global_spell:spell:'v' = global_spell:spell:'v' + 1;
);

_update_or_create_shorthand(spell, shorthand, ...params)->(
  _update_or_create_spell(spell, {
    'key'->spell,
    'title'->str(i18n('shorthand_title',shorthand),titlize(spell)),
    'tooltip'->str(i18n('shorthand_tooltip',shorthand), params),
    'command'->str(_shorthand_command(shorthand), params)
  });
);

_update_or_create_spell(spell, new_data) -> (
  if(!has(global_spell, spell), global_spell:spell = {});
  data = _default_spell(spell, command) + global_spells:spell + new_data;
  data:'v' += 1;
  global_spells:spell = data;
);

//--- FETCH THINGS ---//


spells_by_tag(spelltag)->if(
  spelltag == 'all',
  values(global_spells),
  filter(values(global_spells), (_:'tags'~spelltag) != null)
);

owner_id(p)->(p~'command_name');

_default_spell(spell,command)->{
  'key'->spell,
  'title'->titlize(spell),
  'command'->command,
  'tooltip'->command,
  'color'->'dark_purple',
  'owner'->owner_id(player()),
  'v'->0,
  'tags'->[]
};


//--- CONDITIONALS ---//

is_opped(p)->(p~'permission_level' >= 2);
// is_opped(p)->(false);

is_owner(p, spell)->(global_spells:spell:'owner'==owner_id(p));


//--- ERR CHECKERS ---//

_has_error_no_spell(spell)->_has_error(!has(global_spells,spell),'no_spell', spell);

_has_error_cant_run(p)->_has_error(!is_opped(p),'cant_run_spells');

_has_error_cant_delete(p, spell)->_has_error(!is_opped(p) && !is_owner(p, spell),'cant_delete');

_has_error_cant_change(p, spell, detail)->_has_error(!is_opped(p) && !is_owner(p, spell),'cant_change', detail);

_has_error_no_player(p,name)->_has_error(!p,'no_player_found', name);

_has_error(check, code, ...params)->(check && (_error(code, ...params); true));

//--- SPELL EDITOR ---//


// Title - Owner - tag1 tag2 tag3
// (run) /command
// [Edit] [Title] [Tooltip] [Color] [owner] [Tags]
_spell_editor(spell)->_chat(
  'w \n',
  'mb '+spell:'title'+' ',
  'f - ',
  'p '+spell:'tooltip'+' ',
  'f - ',
  't '+spell:'owner'+' ',
  
  'w \n',
  'ig '+join(' ',spell:'tags'),

  'w \n',
  ..._editable('l', 'edit', 'set', spell:'key', spell:'command'),
  ..._editable_detail('l', spell, 'title'),
  ..._editable_detail('t', spell, 'owner'),
  ..._editable_detail('q', spell, 'tooltip'),
  ..._editable('q', 'tags', spell:'key', 'tags', join(' ',spell:'tags')),
  ..._editable_detail('q', spell, 'color'),
  ..._clickable('rb', 'run', 'run', spell:'key'),

  'w \n',
  ..._copyable('if', spell:'command'),

  'w \n'
);

_editable_detail(col, spell, detail)->_editable(col, detail, spell:'key', detail, spell:detail);



//--- CHAT DISPLAY ---//

_chat(...lines)->print(player(),format(lines));

_clickable(col,btn,...cmd)->(
  cmd = '/'+global_app+' '+join(' ',cmd);
  [col+' '+_btn(btn)+' ', '!'+cmd, '^g '+cmd]
);

_editable(col,btn,...cmd)->[col+' '+_btn(btn)+' ', '?/'+global_app+' '+join(' ',cmd),];

_copyable(col,txt)->[col+' '+txt, '^'+col+' Copy', '&' + txt];

//--- MESSAGES ---//

_btn(code)->i18n('btn', code);

_info(info, ...params)->_msg('mb '+i18n('info',info), params);

_error(err, ...params)->_msg('br '+i18n('error',err), params);

_msg(msg, params)->print(player(),format(str(msg, params)));

i18n(root,code)->(
  _locales(global_lang,root,code) ||
  _locales('en',root,code) ||
  str('i18n %s:%s not found...', root, code)
);

