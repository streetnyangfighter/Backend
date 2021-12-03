package com.snp.takealook.service.cat;

import com.snp.takealook.domain.BaseTimeEntity;
import com.snp.takealook.domain.cat.Cat;
import com.snp.takealook.domain.cat.CatLocation;
import com.snp.takealook.domain.user.User;
import com.snp.takealook.dto.ResponseDTO;
import com.snp.takealook.dto.cat.CatDTO;
import com.snp.takealook.repository.cat.CatLocationRepository;
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
    private final CatLocationRepository catLocationRepository;

    @Transactional
    public Long save(CatDTO.Create dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User with id: " + dto.getUserId() + " is not valid"));

        return catRepository.save(dto.toEntity(user)).getId();
    }

    @Transactional
    public Long updateInfo(Long id, CatDTO.Update dto) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));

        return cat.updateInfo(dto.getName(), dto.getNeutered(), dto.getGender(), dto.getStatus()).getId();
    }

    @Transactional
    public Long updateStatus(Long id, CatDTO.Update dto) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));

        return cat.updateStatus(dto.getStatus()).getId();
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
    public Long updateLocations(Long id, List<CatDTO.LocationList> dtoList) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));

        if (cat.getCatLocationList().size() != 0) {
            catLocationRepository.deleteAll(cat.getCatLocationList());
        }

        List<CatLocation> list = dtoList.stream()
                .map(v -> CatLocation.builder()
                        .cat(cat)
                        .latitude(v.getLatitude())
                        .longitude(v.getLongitude())
                        .build())
                .collect(Collectors.toList());

        return cat.updateLocations(list).getId();
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
        try { // 매칭이 되어있는 상태
            List<Cat> sameGroupCatList = cat.getCatGroup().getCatList();
            for (Cat c : sameGroupCatList) {
                carers.add(c.getUser());
            }
        } catch(Exception e) { // 매칭 없이 단독 고양이 상태
            carers.add(cat.getUser());
        }

        return new ResponseDTO.CatResponse(cat, carers);
    }

    @Transactional
    public List<ResponseDTO.CatLocationListResponse> findAllLocationsById(Long id) {
        Cat cat = catRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cat with id: " + id + " is not valid"));
        List<Cat> sameGroupCatList = cat.getCatGroup().getCatList();
        List<CatLocation> catLocationList = new ArrayList<>();

        for (Cat sameCat : sameGroupCatList) {
            catLocationList.addAll(sameCat.getCatLocationList());
        }

        catLocationList.sort(Comparator.comparing(BaseTimeEntity::getCreatedAt));

        return catLocationList.stream()
                .map(ResponseDTO.CatLocationListResponse::new)
                .collect(Collectors.toList());
    }
}
