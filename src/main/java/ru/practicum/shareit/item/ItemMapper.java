package ru.practicum.shareit.item;

public class ItemMapper {

    public static ItemDtoDate toItemDtoDate(Item item) {
        ItemDtoDate itemDtoDate = new ItemDtoDate();
        itemDtoDate.setId(item.getId());
        itemDtoDate.setName(item.getName());
        itemDtoDate.setDescription(item.getDescription());
        itemDtoDate.setAvailable(item.getAvailable());
        return itemDtoDate;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        //item.setRequest(itemDto.getRequest());
        //itemDto.getRequest() != null ? itemDto.getRequest().getId() : null
        return item;
    }

}

