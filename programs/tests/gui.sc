__createInvScreen(player,nameString,elementMap,interactionMap) -> (

    elementCount = length(keys(elementMap));
    rowCount = ceil(elementCount/9);
    currentPage = 0;
    pageCount = 1;
    if(rowCount > 5, pageCount = ceil(rowCount/5));
    if(currentPage == pageCount - 1,
        rowNo = rowCount - (5 * currentPage),
        rowNo = 5;
    );

    invScreen = create_screen(player,str('generic_9x%s',(rowNo + 1)),nameString,_(screen,player,action,data,outer(rowNo),outer(interactionMap)) ->(

        if(action=='pickup' && interactionMap:'pickup'!=null,               call(interactionMap:'pickup', screen, player, action, data));
        if(action=='pickup_all' && interactionMap:'pickup_all'!=null,       call(interactionMap:'pickup_all', screen, player, action, data));
        if(action=='quick_move' && interactionMap:'quick_move'!=null,       call(interactionMap:'quick_move', screen, player, action, data));
        if(action=='swap' && interactionMap:'swap'!=null,                   call(interactionMap:'swap', screen, player, action, data));
        if(action=='clone' && interactionMap:'clone'!=null,                 call(interactionMap:'clone', screen, player, action, data));
        if(action=='throw' && interactionMap:'throw'!=null,                 call(interactionMap:'throw', screen, player, action, data));
        if(action=='quick_craft' && interactionMap:'quick_craft'!=null,     call(interactionMap:'quick_craft', screen, player, action, data));
        

        if(data:'slot' == rowNo  * 9 + 8 && action == 'pickup',   
            currentPage += 1; 
        );    

        if(data:'slot' == rowNo * 9 + 7 && action =='pickup',
            currentPage = currentPage -1;
        );
    ));

    task(_(outer(invScreen),outer(currentPage),outer(pageCount),outer(elementMap),outer(elementCount)) -> (

        while(screen_property(invScreen,'open'), 100000,

        startSlot = currentPage * 45;
        endSlot = (currentPage + 1) * 45 - 1;
        if(currentPage == pageCount - 1, endSlot = elementCount - 1);

        //Set specefic items in slots
        count = 0;
        c_for(slotNo = startSlot, slotNo < endSlot + 1, slotNo +=1,

            element = elementMap:str(slotNo);
            displayText = element:'displayText'; //Name of item as a map, composed of {'text','color'}
            item = element:'item';  //The item represnted by mc item/block. Defaults to a barrier block
            if(item == null, item = 'barrier');

            lore = element:'description'; //The item lore; Example Lore: \'[{"text":"Location: %s %s %s","color":"blue","italic":false}]\',\'[{"text":"Description:","color":"white","italic":false}]\',\'[{"text":"%s","italic":false}]\'

            itemCount = element:'count'; //Number of items, defaults to 1
            if(itemCount==null, itemCount = 1);

            if(lore != null,
                item=str('%s{display:{Name:\'[{"text":"%s","color":"%s"}]\',Lore:[%s]}}',item,displayText:'text',displayText:'color',lore),
                item=str('%s{display:{Name:\'[{"text":"%s","color":"%s"}]\'}}',item,displayText:'text',displayText:'color');
            );

            inventory_set(invScreen,count,itemCount,item);
            count += 1;    
        );

        nextPageButton = 'arrow';
        previousPageButton = 'arrow';

        inventory_set(invScreen,rowNo * 9 + 8 ,1 ,nextPageButton);
        inventory_set(invScreen,rowNo * 9 + 7 ,1 , previousPageButton);

        sleep(1000);

    );
    ));
);
