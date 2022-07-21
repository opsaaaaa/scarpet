
global_app = system_info('app_name');
global_book = {};
global_lang = 'en';

__config()->{
  'command_permission' -> 'all', 
  'commands' -> {
    '' -> '_editor',
  }
};



_editor() -> (
  p = player();
  book = p~'holds';
  if('written_book' == book:0,
    data = decode_book_nbt(book:2);
    data:'pages' = _add_line_colors(data:'pages');
    print(p,data);
  ,
    _error('not_a_book');
  );
);


_add_line_colors(pages)->(
  for(pages,
    pn = _i;
    for(_,
      ln = _i;
      pages:pn:ln:'color' = _color(ln+pn);
    )
  );
  return(pages)
);

decode_book_nbt(data)->(
  data = parse_nbt(data);
  for(data:'pages',
    page = try(decode_json(_),'json_error', data:'pages':_i);
    data:'pages':_i = if(type(page) != 'array', [page], page);
  );
  data
);

encode_book_nbt(data)->(
  for(data:'pages',
    data:'pages':_i = encode_json(_)
  );
  encode_nbt(data)
);


// book > page > spell
// book = {'page'->}

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

_color(i)->([
  'dark_red',
  'gold',
  'dark_green',
  'dark_aqua',
  'dark_blue',
  'dark_purple'
]:i);

_error(err)->print(player(),format('r '+i18n('error',err)));

i18n(root,code) -> ({
  'en'->{
    'error'->{
      'not_a_book'->'Oi! Thats not a Written Book.'
    }
  }
}:global_lang:root:code);

