package com.example.servicepoller.mock.server;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MockService {

    LOR("lord-of-the-rings");

    private final String id;
}
