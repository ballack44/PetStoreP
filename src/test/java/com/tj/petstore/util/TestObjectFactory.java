package com.tj.petstore.util;

import com.tj.petstore.dto.Category;
import com.tj.petstore.dto.Pet;
import com.tj.petstore.dto.Tag;

import java.util.Collections;
import java.util.List;

public class TestObjectFactory {

    public static Category category(int id, String name) {
        return new Category(id, name);
    }

    public static Tag tag(int id, String name) {
        return new Tag(id, name);
    }

    public static Pet pet(int id, String name, String status, Category category, List<String> photoUrls, List<Tag> tags) {
        return new Pet(id, category, name, photoUrls, tags, status);
    }

    public static Pet basicPet(int id, String name) {
        return pet(
                id,
                name,
                "available",
                category(1, "Dogs"),
                Collections.singletonList("src/image/dummy.png"),
                Collections.singletonList(tag(1, "friendly"))
        );
    }

    public static Pet updatedPet(int id, String name) {
        return pet(
                id,
                name,
                "sold",
                category(2, "GuardDogs"),
                Collections.singletonList("src/image/dummy.png"),
                Collections.singletonList(tag(2, "trained"))
        );
    }
}