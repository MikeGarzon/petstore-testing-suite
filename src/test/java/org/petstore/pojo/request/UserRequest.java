package org.petstore.pojo.request;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private int userStatus;
}