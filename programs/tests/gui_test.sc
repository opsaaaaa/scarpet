__config()->{

    'scope' -> 'global'
};

__command()->(

    buttonMap = {};
    items = ['stone', 'chain', 'apple', 'copper_ingot', 'carrot', 'potato'];

    c_for(i=0,i<197,i+=1,
        buttonMap:i={};
        buttonMap:i:'item' = items:floor(rand(6));
        buttonMap:i:'displayText' = {'text' -> str('Slot %d',i),'color' -> 'green'};
    );

    pageMap = {
        'name' -> 'Test',
        'size' -> 3
    };

    pageMap : 'buttons' = buttonMap;

    __createInvScreen(player(),pageMap, 0);

);

_a(screen,player,action,data) -> (
    print(player('all'),data)
);

__createInvScreen(player,pageMap,currentPage) ->(

    nameString = pageMap: 'name';
    buttonMap = pageMap : 'buttons';
    size = pageMap : 'size';

    buttonCount = length(keys(buttonMap));
    rowCount = ceil(buttonCount/9);
    pageCount = 1;

    //If you don't want a custom size, do not set any numeric value to key 'size'

    if((size == null || type(size) != 'number') || size >5,        
        size = 5;
        pageMap:'size' = size;
    );
    pageCount = ceil(rowCount/size);
    if(currentPage == pageCount-1,
        rowNo = rowCount - (currentPage * size),
        rowNo = size;
    );

    for(keys(buttonMap),

        //Trying to differentiate between a list and a lists stored as string {'[1,2,3]' and [1,2,3]} Probably unneccesary 

        if(type(_)=='string' && _~ ',', //Checking for , cuz numbers could be stored too
            //Will figure out a good/short way to do this later
            logger('error','List stored as string')      
        );

        if(type(_) == 'list',
            c_for(i=0, i<length(_), i+=1, 
                    buttonMap: str(_:i) = buttonMap:_;
                );
                delete(buttonMap,_);
        );
    );

    pageMap: 'buttons' = buttonMap;
    pageMap : 'pageCount' = pageCount;

    invScreen = create_screen(player,str('generic_9x%s',(rowNo + 1)),nameString,_(screen,player,action,data,outer(rowNo),outer(pageMap),outer(currentPage)) ->(
        if(action == 'pickup',__actionHelper('pickup',screen,player,action,data,pageMap,currentPage,rowNo));
        if(action == 'pickup_all',__actionHelper('pickup_all',screen,player,action,data,pageMap,currentPage,rowNo));
        if(action == 'quick_move',__actionHelper('quick_move',screen,player,action,data,pageMap,currentPage,rowNo));
        if(action == 'swap',__actionHelper('swap',screen,player,action,data,pageMap,currentPage,rowNo));
        if(action == 'clone',__actionHelper('clone',screen,player,action,data,pageMap,currentPage,rowNo));
        if(action == 'throw',__actionHelper('throw',screen,player,action,data,pageMap,currentPage,rowNo));

        if(data:'slot' == rowNo  * 9 + 8 && action == 'pickup' && currentPage != pageMap:'pageCount' - 1,   
            currentPage += 1;
            close_screen(screen);
            __createInvScreen(player,pageMap,currentPage);        
        );    

        if(data:'slot' == rowNo * 9 + 7 && action =='pickup' && currentPage != 0,
            currentPage = currentPage -1;
            close_screen(screen);
            __createInvScreen(player,pageMap,currentPage);
        );
    ));

    //Displaying the actual icons
    task(_(outer(invScreen),outer(pageMap),outer(currentPage),outer(rowNo)) -> (
        if(screen_property(invScreen,'open'),

            buttonMap = pageMap:'buttons';
            startSlot = currentPage * (9*pageMap:'size');
            endSlot = (currentPage + 1) * (9*pageMap:'size') - 1;
            if(currentPage == pageMap:'pageCount' - 1, endSlot = length(buttonMap) - 1);

            slot = 0;
            c_for(slotNo = startSlot, slotNo < endSlot + 1, slotNo +=1,

                button = buttonMap: slotNo;
                displayText = button:'displayText';
                item = button:'item';
                if(item == null, item = 'barrier');
                lore = button:'description';
                itemCount = button:'count'; //Number of items, defaults to 1
                if(itemCount==null, itemCount = 1);
                if(lore != null,
                item=str('%s{display:{Name:\'[{"text":"%s","color":"%s"}]\',Lore:[%s]}}',item,displayText:'text',displayText:'color',lore),
                item=str('%s{display:{Name:\'[{"text":"%s","color":"%s"}]\'}}',item,displayText:'text',displayText:'color');

                inventory_set(invScreen,count,itemCount,item);
                count += 1;
            );

            nextPageButton = 'arrow';
            previousPageButton = 'arrow';

            if(currentPage != pageMap:'pageCount' -1, inventory_set(invScreen,rowNo * 9 + 8 ,1 ,nextPageButton));
            if(currentPage != 0, inventory_set(invScreen,rowNo * 9 + 7 ,1 , previousPageButton));
            );  
        )
    ));

);

__actionHelper(action,screen,player,action,data,pageMap,currentPage,rowNo) -> (

    buttonMap = pageMap : 'buttons';
    for(keys(buttonMap),
                if(data:'slot' != _, continue());
                actionMap = buttonMap: _ : 'interactions': action;
                if(actionMap == null, continue());
                meth = actionMap:'method'; //String: Name of method
                args = actionMap:'args';   //List: Additional arguments that one has to pass
                if(length(args) != 0,
                    call(meth,screen,player,action,data,buttonMap,currentPage,rowNo,...args),
                    call(meth,screen,player,action,data,buttonMap,currentPage,rowNo)
                )
    );
);