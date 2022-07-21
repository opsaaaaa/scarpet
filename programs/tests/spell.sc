


_new_book_data() -> (


);

_new

// book > page > spell
// book = {'page'->}

_render_page(lines) -> (
  lines = [];
  encode_nbt({
    'pages'->[encode_json([
      '',
      'something\n',
      'nice\n',
      'to read\n',
      {'text'->'someting else\n'},
      { 'text'->'[say hi]',
        'clickEvent'->{
          'action'->'copy_to_clipboard',
          'value'->'/say hi'
        },
        'hoverEvent'->{'action'->'show_text','contents'->'/say hi'}
      }
    ])],
    'title'->'Testing',
    'author'->'http://minecraft.tools/',
    'display'->{
      'Lore'->['here here']
    }
  });
);
