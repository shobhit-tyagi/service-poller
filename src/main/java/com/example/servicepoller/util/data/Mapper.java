package com.example.servicepoller.util.data;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<E, P> {

    E toEntity(P p);

    P toPresentation(E e);

    default List<E> toEntity(List<P> pList) {
        return pList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    default List<P> toPresentation(List<E> eList) {
        return eList.stream()
                .map(this::toPresentation)
                .collect(Collectors.toList());
    }
}
