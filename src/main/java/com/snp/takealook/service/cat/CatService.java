package com.snp.takealook.service.cat;

import com.snp.takealook.domain.BaseTimeEntity;
import com.snp.takealook.domain.cat.Cat;
import com.snp.takealook.domain.cat.CatStatus;
import com.snp.takealook.domain.user.User;
import com.snp.takealook.dto.ResponseDTO;
import com.snp.takealook.dto.cat.CatDTO;
import com.snp.takealook.repository.cat.CatRepository;
import com.snp.takealook.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CatService {

    private final CatRepository catRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long save(Long userId, CatDTO.Create dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id: " + userId + " is not valid"));

        return catRepository.save(dto.toEntity(user)).getId();
    }

    @Transactional
    public Long update(Long id, CatDTO.Update dto) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));

        return cat.updateInfo(dto.getName(), dto.getNeutered(), dto.getGender(), dto.getCharacter()).getId();
    }

    @Transactional
    public Long delete(Long id) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));

        return cat.delete().getId();
    }

    @Transactional
    public Long restore(Long id) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));

        return cat.restore().getId();
    }

    @Transactional
    public List<ResponseDTO.CatListResponse> findAllByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id: " + userId + " is not valid"));

        return catRepository.findCatsByUser(user).stream()
                .map(ResponseDTO.CatListResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long removeFromGroup(Long id) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));

        return cat.updateCatGroup(null).getId();
    }

    @Transactional
    public ResponseDTO.CatResponse findOne(Long id) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));
        List<User> carers = new ArrayList<>();
        List<CatStatus> catStatusList = new ArrayList<>();

        try { // 매칭이 되어있는 상태
            List<Cat> sameGroupCatList = cat.getCatGroup().getCatList();
            for (Cat sameCat : sameGroupCatList) {
                carers.add(sameCat.getUser());
                catStatusList.addAll(sameCat.getCatStatusList());
            }
        } catch(Exception e) { // 매칭 없이 단독 고양이 상태
            carers.add(cat.getUser());
            catStatusList.addAll(cat.getCatStatusList());
        }

        catStatusList.sort(Comparator.comparing(BaseTimeEntity::getCreatedAt));

        return new ResponseDTO.CatResponse(cat, carers, catStatusList.get(0));
    }

}
