__config()->{
    'scope' -> 'global'
};

// just some proof of concept settings
global_toggle_setting = false;
global_count = 1;

_item_title(txt) -> str('{display:{Name:\'[{"text":"%s"}]\'}}', txt);

// all the buttons and everything required to render each inventory gui page
global_pages = {

  'main'->{
    // call _on and _off functions for right and left mouse buttons
    0->{
      'item'->['glass',1,_item_title('right click')],
      'off'->'tinted_glass',
      'on'->'glass',
      'action'->{
        'pickup:0'->'_on',
        'pickup:1'->'_off'
      }
    },

    // call _toggle when clicked, then update the item in the _toggle function
    1->{
      'item'->['minecart',1,_item_title('Fill Me')],
      'on'->'furnace_minecart',
      'off'->'minecart',
      'action'->{'pickup'->'_toggle'}
    },

    // call the nav function, then open the sub menu page
    8->{
      'item'->['blaze_rod',1,_item_title('Sub Menu')],
      'nav'->'sub',
      'action'->{'pickup'->'_nav'}
    },
    
    // allow this item to be replaced
    2->{'item'->'apple','action'->{'pickup'->'_a'},'foot'->null},
    
    // call the _a fuction when clicked
    3->{'item'->'carrot','action'->{'pickup'->'_a'}},

    // action on left mouse button
    6->{'item'->'bone','action'->{'pickup:0'->'_a'}},

    // action on right mouse button
    5->{'item'->'potato','action'->{'pickup:1'->'_a'}}
  },

  'sub'->{
    // reuse the nav function to return to the main page
    5->{
      'item'->['coal',1,_item_title('Back')],
      'nav'->'main',
      'action'->{'pickup'->'_nav'}
    }
  }
};

__command()->_open_page(player(),'main');

// open any page defined in global_pages
_open_page(p, page) -> (
  inventory_gui(player(),page,4,global_pages:page,{'foot'->'cancel'})
);


// create a inventory gui, example buttons can be found in the global_pages variable
inventory_gui(p, name, rows, buttons, options) -> (
  
  // set defaul options
  if(!options, optins = {});
  maxSize = (rows * 9) - 1;

  // create the screen and pass in buttons and options
  screen = create_screen(p, str('generic_9x%d',rows), name, 
  _(screen, p, action, data, outer(buttons), outer(options), outer(maxSize))->(

    slot = data:'slot';
    button = buttons:slot;

    if(has(buttons, slot),
      data:'size' = maxSize;

      // fetch both the 'pickup' and 'pickup:0' actions
      // and filter out any null values
      methods = filter([
        button:'action':action,
        button:'action':str('%s:%d',action,data:'button')
      ], _);

      // call the method/action for this event
      for(methods, 
        call(_, p, screen, data, button);
      );

    );

    // handle 'cancel' so we can avoid players picking up items.
    // or allow them to by setting 'foot'->'cancel'
    if(has(button,'foot'), 
      button:'foot'

    ,slot <= maxSize,//elif 
      options:'foot'
    )
  ));

  fill_inventory_gui(screen, buttons);
);

clear_inventory_gui(screen, maxSize) -> (
  loop(maxSize, 
    inventory_set(screen, _, 0, 'air');
  )
);

fill_inventory_gui(screen, buttons) -> (
  // loop over each button and set the slot to it.
  for(pairs(buttons),
    slot = _:0;
    item = _:1:'item';
    action = _:1:'action';

    // turn 'stone' into ['stone', 1, null]
    // so that you can define or pass item_tuples in buttons
    if(type(item) != 'list', item = [item, 1, null]);

    inventory_set(screen, slot, item:1, item:0, item:2);
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

// problem with doing navigation this way is that it places the mouse in the center.
// better to clean the screen and fill it again.
// tho there is an issue with redefining the buttons map after that.
_nav(p, screen, data, button) -> (
  _open_page(p, button:'nav');
  // clear_inventory_gui(screen, data:'size');
  // fill_inventory_gui(screen, global_pages:(button:'nav'));
);


