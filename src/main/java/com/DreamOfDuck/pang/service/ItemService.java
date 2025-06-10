package com.DreamOfDuck.pang.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.pang.dto.request.ItemCreateRequest;
import com.DreamOfDuck.pang.dto.response.ItemResponse;
import com.DreamOfDuck.pang.entity.Item;
import com.DreamOfDuck.pang.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public ItemResponse save(Member member, ItemCreateRequest request) {
        Item item = member.getGameItem();
        if(item!=null){
            item.setDia(request.getDia());
            item.setFeather(request.getFeather());
            return ItemResponse.from(item);
        }else{ //save
            Item newItem = Item.builder()
                    .dia(request.getDia())
                    .feather(request.getFeather())
                    .build();
            itemRepository.save(newItem);
            newItem.addHost(member);
            return ItemResponse.from(newItem);
        }
    }
    public ItemResponse getItemByHost(Member member){
        Item item = itemRepository.findByHost(member).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_ITEM));
        return ItemResponse.from(item);
    }
    @Transactional
    public void delete(Member host, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_ITEM));
        if(item.getHost().equals(host)){
            throw new CustomException(ErrorCode.DIFFERENT_USER_ITEM);
        }
        itemRepository.delete(item);
    }
    @Transactional
    public void deleteByUser(Member member){
        Item item = itemRepository.findByHost(member).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_ITEM));
        itemRepository.delete(item);
    }
}
