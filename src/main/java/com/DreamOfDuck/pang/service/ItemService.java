package com.DreamOfDuck.pang.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.pang.dto.request.ItemUseRequest;
import com.DreamOfDuck.pang.dto.response.ItemResponse;
import com.DreamOfDuck.pang.entity.Item;
import com.DreamOfDuck.pang.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    // BubblePang 증가
    @Transactional
    public ItemResponse updateBubblePang(Member host){
        Item item = getOrCreateItem(host);
        item.setBubblePang(item.getBubblePang() + 1);
        return ItemResponse.fromItem(item);
    }

    // BreadCrumble 증가
    @Transactional
    public ItemResponse updateBreadCrumble(Member host){
        Item item = getOrCreateItem(host);
        item.setBreadCrumble(item.getBreadCrumble() + 1);
        return ItemResponse.fromItem(item);
    }

    // Tornado 증가
    @Transactional
    public ItemResponse updateTornado(Member host){
        Item item = getOrCreateItem(host);
        item.setTornado(item.getTornado() + 1);
        return ItemResponse.fromItem(item);
    }
    @Transactional
    public ItemResponse updateGrass(Member host){
        Item item = getOrCreateItem(host);
        item.setTornado(item.getGrass() + 1);
        return ItemResponse.fromItem(item);
    }

    // Item이 없으면 새로 생성
    private Item getOrCreateItem(Member host){
        Item item = host.getItem();
        if(item == null){
            item = Item.builder()
                    .bubblePang(0L)
                    .breadCrumble(0L)
                    .tornado(0L)
                    .grass(0L)
                    .build();
            host.setItem(item); // Member와 연관관계 연결
            itemRepository.save(item);
        }
        return host.getItem();
    }
    @Transactional
    public ItemResponse useItem(Member host, ItemUseRequest request){
        Item item = getOrCreateItem(host);
        Long breadCrumble = item.getBreadCrumble();
        Long tornado = item.getTornado();
        Long bubblePang = item.getBubblePang();
        if(request.getBreadCrumble()>breadCrumble || request.getTornado()>tornado|| request.getBubblePang()>bubblePang ){
            throw new CustomException(ErrorCode.NOT_ENOUGH_ITEM);
        }
        item.setBreadCrumble(breadCrumble-request.getBreadCrumble());
        item.setTornado(tornado-request.getTornado());
        item.setBubblePang(bubblePang-request.getBubblePang());
        return ItemResponse.fromItem(item);
    }
    @Transactional
    public ItemResponse getItem(Member host){
        Item item = getOrCreateItem(host);
        return ItemResponse.fromItem(item);
    }
}

