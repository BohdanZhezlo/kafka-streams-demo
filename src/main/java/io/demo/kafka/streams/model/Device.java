package io.demo.kafka.streams.model;

import java.util.UUID;

public class Device {

    private UUID id;
    private String deviceSerialNumber;
    private String accountNumber;

    public Device() {
    }

    public Device(UUID id, String deviceSerialNumber, String accountNumber) {
        this.id = id;
        this.deviceSerialNumber = deviceSerialNumber;
        this.accountNumber = accountNumber;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", serial_number='" + deviceSerialNumber + '\'' +
                ", account_sn='" + accountNumber + '\'' +
                '}';
    }
}
