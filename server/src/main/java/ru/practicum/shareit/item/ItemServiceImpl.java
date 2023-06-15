package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDtoForBookerId;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.CommentValidateFailException;
import ru.practicum.shareit.tools.exception.ItemNotFoundException;
import ru.practicum.shareit.tools.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final RequestService requestService;

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ItemDto saveItem(ItemDto itemDto, Long ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        Validator.allItemValidation(item);
        userService.checkUserExist(ownerId);
        item.setOwner(userService.getUser(ownerId));
        if (itemDto.getRequestId() != null) {
            item.setRequest(requestService.checkRequestExist(itemDto.getRequestId()));
        }
        ItemDto outItemDto = ItemMapper.toItemDto(itemRepository.save(item));
        if (itemDto.getRequestId() != null) {
            outItemDto.setRequestId(itemDto.getRequestId());
        }
        return outItemDto;
    }

    @Transactional
    public Item updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        Item item = checkItemExist(itemId);

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Error! Item has ownerId=" +
                    item.getOwner().getId() + " but X-Sharer-User-Id ownerId=" + ownerId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public ItemDtoDate getItemDtoDate(Long id, Long ownerId) {
        Item item = checkItemExist(id);
        ItemDtoDate itemDtoDate = ItemMapper.toItemDtoDate(item);
        setLastAndNextFields(itemDtoDate, item, ownerId);
        itemDtoDate.setComments(getAllCommentsByItemId(id));
        return itemDtoDate;
    }

    @Transactional(readOnly = true)
    public List<ItemDtoDate> getAllItemByOwner(Long ownerId) {
        List<Item> items;
        List<ItemDtoDate> itemsDtoDates = new ArrayList<>();
        items = itemRepository.findByOwnerOrderById(userService.getUser(ownerId));

        for (Item item : items) {
            ItemDtoDate itemDtoDates = ItemMapper.toItemDtoDate(item);
            setLastAndNextFields(itemDtoDates, item, ownerId);
            itemsDtoDates.add(itemDtoDates);
        }

        return itemsDtoDates;
    }

    public List<Item> getItemsByText(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        return itemRepository.getItemsByText(text);
    }

    @Transactional
    public CommentDto saveComment(Comment comment, Long itemId, Long userId) {
        Validator.commentValidation(comment);

        // Проверка, что отзыв на Item может писать только тот, у кого есть
        // записи в bookins, причем дата окончания меньше чем now
        Item item = checkItemExist(itemId);
        User user = userService.getUser(userId);
        Long l = bookingRepository.countListOfBookersForItem(item, LocalDateTime.now(), user);
        if (l == 0) {
            throw new CommentValidateFailException("Comment can send only real booker");
        }

        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }

    public Item checkItemExist(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * Вычисление и добавление полей lastBooking и nextBooking для ItemDtoDate
     */
    private void setLastAndNextFields(ItemDtoDate itemDtoDate, Item item, Long ownerId) {
        User owner = userService.getUser(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> nb = bookingRepository.getAllBookingsForNext(item, now, owner);
        List<Booking> lb = bookingRepository.getAllBookingsForLast(item, now, owner);

        if (nb.size() > 0) {
            BookingDtoForBookerId nbDto = new BookingDtoForBookerId();
            nbDto.setId(nb.get(0).getId());
            nbDto.setBookerId(nb.get(0).getBooker().getId());
            itemDtoDate.setNextBooking(nbDto);
        }

        if (lb.size() > 0) {
            BookingDtoForBookerId lbDto = new BookingDtoForBookerId();
            lbDto.setId(lb.get(0).getId());
            lbDto.setBookerId(lb.get(0).getBooker().getId());
            itemDtoDate.setLastBooking(lbDto);
        }
    }

    private List<CommentDto> getAllCommentsByItemId(Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            commentDto.setAuthorName(comment.getAuthor().getName());
            commentsDto.add(commentDto);
        }
        return commentsDto;
    }


}