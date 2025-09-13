package com.swiftlogistics.cms.model;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"orderId", "clientId", "amount"})
@XmlRootElement(name = "SubmitOrderRequest", namespace = "http://swiftlogistics.com/cms/orders")
public class SubmitOrderRequest {

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String orderId;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String clientId;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private BigDecimal amount;

    public SubmitOrderRequest() {
        this.orderId = "";
        this.clientId = "";
        this.amount = BigDecimal.ZERO;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "SubmitOrderRequest{" +
                "orderId='" + orderId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
