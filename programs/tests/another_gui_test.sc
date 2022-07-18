__config()->{
    'scope' -> 'global'
};

// just some proof of concept settings
// global_toggle_setting = false;
global_count = 1;

_item_title(txt) -> str('{display:{Name:\'[{"text":"%s"}]\'}}', txt);
_make_lots_of_btns() -> (
  btns = {};
  items = ['stone', 'chain', 'apple', 'copper_ingot', 'carrot', 'potato'];

  c_for(i=0,i<200,i+=1,
      btns:i={};
      btns:i:'item' = items:floor(rand(6));
  );
  btns
);

// all the buttons and everything required to render each inventory gui view
global_gui_views = {
  'main'->{
    'name' -> 'Main Page',
    'rows' -> 'auto',
    'foot' -> 'cancel',
    'buttons' -> {
      0 -> {
        'item'->['glass',1,_item_title('right click')],
        'off'->'tinted_glass',
        'on'->'glass',
        'action'->{
          'pickup:0'->'_on',
          'pickup:1'->'_off'
        }
      },

      1 -> {
        'item'->['minecart',1,_item_title('Fill Me')],
        'on'->'furnace_minecart',
        'off'->'minecart',
        'action'->{'pickup'->'_toggle'}
      },

      8 -> {
        'item'->['blaze_rod',1,_item_title('Sub Menu')],
        'nav'->{'pickup'->'sub'}
      },

      7 -> {
        'item'->'paper',
        'nav'->{'pickup'->'pages'}
      },

      3 -> {'item'->'carrot','action'->{'pickup'->'_a'}},

      6 -> {'item'->'bone','action'->{'pickup:0'->'_a'}},

      5 -> {'item'->'potato','action'->{'pickup:1'->'_a'}}
    }
  },

  'sub'->{
    'name' -> 'Sub Page',
    'rows' -> 1,
    'foot' -> 'cancel',
    'buttons' -> {
      5 -> {
        'item' -> ['coal',1,_item_title('Back')],
        'nav' -> {'pickup'->'main'}
      }
    }
  },
  'pages'->{
    'name' -> 'Random Pagination',
    'rows' -> 'auto',
    'foot' -> 'cancel',
    'pager' -> 'birch_sign',
    'paginate' -> true,
    'buttons' -> _make_lots_of_btns()
  }
};



__command()->inventory_gui(player(),'main',global_gui_views,{'rows'->3});


// create a inventory gui, example buttons can be found in the global_gui_views variable
inventory_gui(p, current_view, view_map, options) -> (
  for(keys(view_map),

    view_map:_:'max_slot' = max(keys(view_map:_:'buttons'));
    
    if(view_map:_:'rows' == 'auto', 
      view_map:_:'rows' = min(6,max(
        ceil((view_map:_:'max_slot'+1)/9),
      1))
    );
   
    if(!has(view_map:_,'pager'),
      view_map:_:'pager' = 'arrow';
    );

    view_map:_:'size' = (view_map:_:'rows' * 9) - 1;

    view_map:_:'page' = 0;
    view_map:_:'paginate' = view_map:_:'paginate' && view_map:_:'max_slot' > view_map:_:'size';

    if(!has(view_map:_,'blank'), view_map:_:'blank' = 'air');
  );
  __create_gui_screen(p,current_view,view_map,options);
);



// create the screen and pass in buttons and options
__create_gui_screen(p, current_view, view_map, options)->(
  global_gui_lock = true;
  kind = str('generic_9x%d',view_map:current_view:'rows');
  screen = create_screen(
    p,
    kind,
    view_map:current_view:'name', 
  _(  
    screen,
    p,
    action,
    data,
    outer(current_view),
    outer(view_map),
    outer(options),
    outer(active)
  )->(
    if(global_gui_lock, return());

    view = view_map:current_view;
    buttons = view:'buttons';
    page = view:'page';
    paginate = view:'paginate';
    size = view:'size';

    slot = data:'slot';
    if(paginate,
      slot = slot + (page * size);
    );

    button = buttons:slot;

    // handle pagnination
    if(paginate && data:'slot' == size,
      if(page > 0 && data:'button' == 0,
        page = page - 1;
      );
      if(page + 1 < ceil(view:'max_slot'/size) && data:'button' == 1,
        page = page + 1;
      );
      if(page != view_map:current_view:'page',
        view_map:current_view:'page' = page;
        schedule(1,'__create_gui_screen', p, current_view, view_map, options);
      );

    // elif
    // hangle buttons and nav
    ,has(buttons, slot),
      data:'size' = options:'size';

      if(has(button,'actions'),
        // call actions when buttons are pressed
        // fetch both the 'pickup' and 'pickup:0' actions
        method = button:'action':action || button:'action':str('%s:%d',action,data:'button');

        if(method, 
          call(mothod, p, screen, data, button);
        );

      // elif
      // handle naviation between screens
      ,has(button,'nav'),
        nav = button:'nav';
        nav_view = nav:action || nav:str('%s:%d',action,data:'button');
        if(nav_view,
          schedule(1,'__create_gui_screen', p, nav_view, view_map, options);
        );
      );
    );

    // handle 'cancel' so we can avoid players picking up items.
    // or allow them to by setting 'foot'->'cancel'
    if(has(button,'foot'), 
      button:'foot'

    ,data:'slot' <= view_map:current_view:'size',//elif 
      options:'foot' || view_map:current_view:'foot'
    )
  ));
  __fill_gui_screen(screen, view_map:current_view);
  schedule(2, _()->(global_gui_lock = false));
);

__fill_gui_screen(screen, view) -> (
  
  buttons = view:'buttons';
  size = view:'size'; 
  blank = view:'blank'; 
  page = view:'page';
  pager = view:'pager';
  max_slot = max(keys(buttons));
  // loop over each button and set the slot to it.
  if(view:'paginate',
    inventory_set(screen, size, page+1, pager, str(
      '{display:{Name:\'[{"text":"%d/%d"}]\',Lore:[\'{"text":"%s"}\',\'{"text":"%s"}\']},Enchantments:[{id: "_null", lvl: 1b}]}',
      page+1,ceil(max_slot/size),
      'Right Click +1',
      'Left Click -1'
    ));
  ,// else
    size = size + 1;
  );
  
  offset = size * page;
  c_for(slot=0, slot<size, slot+=1, 
    if(has(buttons,slot),
      btn = buttons:(slot+offset);
      if(btn,
        item = btn:'item';

        // turn 'stone' into ['stone', 1, null]
        // so that you can define or pass item_tuples in buttons
        if(type(item) != 'list', item = [item, 1, null]);
        try(
          inventory_set(screen, slot, item:1, item:0, item:2);
        ,'unknown_item',
          print(player(), format(str('r unknown_item:%d: [%s, %s, %s]', (slot+offset), item:0, item:1, item:2)))
        );
      )
    ,
      inventory_set(screen, 
        slot, 
        if(blank=='air',0,1), 
        blank,
        '{display:{Name:\'[{"text":""}]\'}}'
      );
    );
  );
);





_a(p,s,d,b) -> (
    print(p,[d,b])
);

_on(p, screen, data, button) -> (
  inventory_set(screen, data:'slot',1, button:'on', _item_title('right click'));
  print(p,'on')
);
_off(p, screen, data, button) -> (
  inventory_set(screen, data:'slot',1, button:'off', _item_title('left click'));
  print(p,'off')
);
_toggle(p, screen, data, button) -> (
  global_toggle_setting = !global_toggle_setting;
  state = if(global_toggle_setting, 'on', 'off');
  inventory_set(screen, data:'slot',1, button:state, _item_title(state));
);



