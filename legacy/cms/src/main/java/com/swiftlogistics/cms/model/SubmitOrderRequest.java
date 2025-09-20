package com.swiftlogistics.cms.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"orderId", "clientId", "amount"})
@XmlRootElement(name = "SubmitOrderRequest", namespace = "http://swiftlogistics.com/cms/orders")
public class SubmitOrderRequest {

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String orderId;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String clientId;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private Double amount;

    public SubmitOrderRequest() {
        this.orderId = "";
        this.clientId = "";
        this.amount = 0.0;
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
