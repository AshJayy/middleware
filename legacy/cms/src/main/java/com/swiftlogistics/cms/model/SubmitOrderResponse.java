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
@XmlType(name = "", propOrder = {"status", "message", "orderId", "billedAmount"})
@XmlRootElement(name = "SubmitOrderResponse", namespace = "http://swiftlogistics.com/cms/orders")
public class SubmitOrderResponse {

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String status;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String message;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String orderId;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private Double billedAmount;

    public SubmitOrderResponse() {
        this.status = "";
        this.message = "";
        this.orderId = "";
        this.billedAmount = 0.0;
    }

    @Override
    public String toString() {
        return "SubmitOrderResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", orderId='" + orderId + '\'' +
                ", billedAmount=" + billedAmount +
                '}';
    }
}
