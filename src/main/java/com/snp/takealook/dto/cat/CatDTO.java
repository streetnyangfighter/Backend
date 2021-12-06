package com.snp.takealook.dto.cat;

import com.snp.takealook.domain.cat.Cat;
import com.snp.takealook.domain.user.User;
import lombok.Builder;
import lombok.Getter;

public class CatDTO {

    @Getter
    public static class Create {
        private String name;
        private Byte gender;
        private Byte neutered;
        private String chracter;

        @Builder
        public Create(String name, Byte gender, Byte neutered, String chracter) {
            this.name = name;
            this.gender = gender;
            this.neutered = neutered;
            this.chracter = chracter;
        }

        public Cat toEntity(User user) {
            return Cat.builder()
                    .user(user)
                    .name(name)
                    .gender(gender)
                    .neutered(neutered)
                    .character(chracter)
                    .build();
        }
    }

    @Getter
    public static class Update {
        private String name;
        private Byte gender;
        private Byte neutered;
        private String character;
    }

    @Getter
    public static class LocationList {
        private double latitude;
        private double longitude;
    }

}
