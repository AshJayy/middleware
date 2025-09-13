package com.swiftlogistics.cms.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"status", "message"})
@XmlRootElement(name = "SubmitOrderResponse", namespace = "http://swiftlogistics.com/cms/orders")
public class SubmitOrderResponse {

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String status;

    @XmlElement(namespace = "http://swiftlogistics.com/cms/orders", required = true)
    private String message;

    public SubmitOrderResponse() {
        this.status = "";
        this.message = "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SubmitOrderResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
