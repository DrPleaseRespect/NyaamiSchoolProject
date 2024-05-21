package com.drpleaserespect.nyaamii;

public class StoreItem {
    private final String name;
    private final int price;
    private final String imageUrl;

    private final String documentId;

    public StoreItem(String name, int price, String imageUrl, String documentId) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.documentId = documentId;

    }

    public StoreItem(String name, int price, String documentId) {
        this.name = name;
        this.price = price;
        this.imageUrl = "https://picsum.photos/500";
        this.documentId = documentId;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreItem storeItem = (StoreItem) o;
        return name.equals(storeItem.name) && price == storeItem.price && imageUrl.equals(storeItem.imageUrl);
    }

    @Override
    public String toString() {
        return "StoreItem{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", documentId='" + documentId + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return documentId;
    }

    public boolean equalsID(StoreItem storeItem) {
        return documentId.equals(storeItem.getId());
    }
}
