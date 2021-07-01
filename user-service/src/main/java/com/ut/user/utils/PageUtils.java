package com.ut.user.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class PageUtils<T> {

    public Page<T> pageUserBasicVos(Page resourcePage, List<T> result) {
        return new Page<T>() {
            @Override
            public int getTotalPages() {
                return resourcePage.getTotalPages();
            }

            @Override
            public long getTotalElements() {
                return resourcePage.getTotalElements();
            }

            @Override
            public <U> Page<U> map(Function<? super T, ? extends U> function) {
                return null;
            }

            @Override
            public int getNumber() {
                return resourcePage.getNumber();
            }

            @Override
            public int getSize() {
                return resourcePage.getSize();
            }

            @Override
            public int getNumberOfElements() {
                return resourcePage.getNumberOfElements();
            }

            @Override
            public List<T> getContent() {
                return new ArrayList<>(result);
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return resourcePage.getSort();
            }

            @Override
            public boolean isFirst() {
                return resourcePage.isFirst();
            }

            @Override
            public boolean isLast() {
                return resourcePage.isLast();
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<T> iterator() {
                return null;
            }
        };
    }
}
