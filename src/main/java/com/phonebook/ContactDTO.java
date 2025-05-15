package com.phonebook;

public record ContactDTO(
    String firstName,
    String lastName,
    String phoneNumber,
    String address
) {} 