package com.tj.petstore;

public class Endpoints {

    static final String baseUrl = "https://petstore.swagger.io/v2";

    //-------------------------pet-------------------------
    public static final String findByStatus = "/pet/findByStatus";
    public static final String uploadImage = "/pet/{petId}/uploadImage";
    public static final String findByTags = "/pet/findByTags";
    public static final String newPet = "/pet";
    public static final String petById = "/pet/{petId}";

}
