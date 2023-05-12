package ru.practicum.shareit.item;

public class ItemMapper {

//    public static ItemDto toItemDto(Item item) {
//        return new ItemDto(
//                item.getName(),
//                item.getDescription(),
//                item.getAvailable(),
//                null // заглушка для следующего спринта
//                // item.getRequest() != null ? item.getRequest().getId() : null
//        );
//    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(itemDto.getRequest());
        return item;
        //itemDto.getRequest() != null ? itemDto.getRequest().getId() : null
    }

}

