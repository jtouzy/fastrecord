package com.jtouzy.fastrecord.utils;

import java.util.ArrayList;

public class Chain<T,L> extends ArrayList<Chain.ChainItemWrapper<T,L>> {
    public static class ChainItemWrapper<I,NL> {
        private final I item;
        private NL linkToNextItem;

        public ChainItemWrapper(I item) {
            this.item = item;
        }

        public I getItem() {
            return item;
        }

        public NL getLinkToNextItem() {
            return linkToNextItem;
        }

        public void setLinkToNextItem(NL linkToNextItem) {
            this.linkToNextItem = linkToNextItem;
        }
    }

    public void addFirst(T item) {
        if (size() > 0)
            throw new IllegalStateException("Chain already had elements. You must call add(L,T) instead");
        safeAddNew(item);
    }

    public void add(L link, T item) {
        if (size() == 0)
            throw new IllegalStateException("No elements in the chain. You must call addFirst() before");
        ChainItemWrapper<T,L> lastElement = get(size() - 1);
        lastElement.setLinkToNextItem(link);
        safeAddNew(item);
    }

    private void safeAddNew(T item) {
        add(new ChainItemWrapper<>(item));
    }
}
