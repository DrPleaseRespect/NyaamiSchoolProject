package com.drpleaserespect.nyaamii.DataObjects;

import android.util.Log;

import java.util.Objects;

public class OrderObject {
    private StoreItem item;
    private int quantity;
    private String DocumentId;

    public OrderObject(StoreItem item, int quantity, String DocumentId) {
        this.item = item;
        this.quantity = QuantityLimit(quantity);
        this.DocumentId = DocumentId;
    }

    public OrderObject(OrderObject orderObject, int quantity) {
        this.item = orderObject.getItem();
        this.quantity = QuantityLimit(quantity);
        this.DocumentId = orderObject.getDocumentId();
    }

    private static int QuantityLimit(int quantity) {
        if (quantity < 0) {
            Log.d("OrderObject", "Quantity cannot be negative");
            return 0;
        } else if (quantity > 100) {
            Log.d("OrderObject", "Quantity cannot be greater than 100");
            return 100;
        } else {
            return quantity;
        }
    }

    public StoreItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDocumentId() { return DocumentId; }

    @Override
    public String toString() {
        return "OrderObject{" +
                "item=" + item +
                ", quantity=" + quantity +
                ", DocumentId='" + DocumentId + '\'' +
                ", getDocumentId='" + DocumentId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderObject that = (OrderObject) o;
        return getQuantity() == that.getQuantity() && Objects.equals(getItem(), that.getItem()) && Objects.equals(DocumentId, that.DocumentId) && Objects.equals(getDocumentId(), that.getDocumentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity, DocumentId);
    }
}
