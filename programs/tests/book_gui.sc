

__command()->(


  screen = create_screen(player(), 'lectern', name, 
  _(screen,p, action, data)->(


    page = screen_property(screen, 'page');
    print(p, [action, data, page]);

  ));

  screen_property(screen, 'page', 2);
  inventory_set(screen, 0, 1, 'written_book', 
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
    })
  );
    // '{
    //   pages:[\'[""]\'],
    //   title:Testing,
    //   author:"http://minecraft.tools/",
    //   display:{Lore:["here here"]}
    // }'

    // '{
    //   pages:[\'[
    //     "",
    //     {"text":"This is just a test.\\n\\n"},
    //     {"text":"[say hi]\\n\\n","clickEvent":{"action":"run_command","value":"/say hi"},"hoverEvent":{"action":"show_text","contents":"/say hi"}},
    //     {"keybind":"key.jump","clickEvent":{"action":"run_command","value":"/say hi"},"hoverEvent":{"action":"show_text","contents":"/say hi"}},
    //     {"text":"\\n\\n"},
    //     {"selector":"@p"}
    //   ]\'],
    //   title:Testing,
    //   author:"http://minecraft.tools/",
    //   display:{Lore:["here here"]}
    // }'
  // /give @p written_book{pages:['["",{"text":"This is just a test.\\n\\n"},{"text":"[say hi]\\n\\n","clickEvent":{"action":"run_command","value":"/say hi"},"hoverEvent":{"action":"show_text","contents":"/say hi"}},{"keybind":"key.jump","clickEvent":{"action":"run_command","value":"/say hi"},"hoverEvent":{"action":"show_text","contents":"/say hi"}},{"text":"\\n\\n"},{"selector":"@p"}]'],title:Testing,author:"http://minecraft.tools/",display:{Lore:["here here"]}}



);

