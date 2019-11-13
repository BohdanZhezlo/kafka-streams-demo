package io.demo.kafka.streams.model;

import java.util.UUID;

public class UsageRecord {

    private UUID id;
    private String deviceSerialNumber;
    private Long downloadBytes = 0L;
    private Long uploadBytes = 0L;
    private Long sessionStartTime = 0L;

    public UsageRecord() {
    }

    public UsageRecord(UUID id, String deviceSerialNumber, Long downloadBytes, Long uploadBytes, Long sessionStartTime) {
        this.id = id;
        this.deviceSerialNumber = deviceSerialNumber;
        this.downloadBytes = downloadBytes;
        this.uploadBytes = uploadBytes;
        this.sessionStartTime = sessionStartTime;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public Long getDownloadBytes() {
        return downloadBytes;
    }

    public void setDownloadBytes(Long downloadBytes) {
        this.downloadBytes = downloadBytes;
    }

    public Long getUploadBytes() {
        return uploadBytes;
    }

    public void setUploadBytes(Long uploadBytes) {
        this.uploadBytes = uploadBytes;
    }

    public Long getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(Long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UsageRecord{" +
                "id=" + id +
                ", device_sn='" + deviceSerialNumber + '\'' +
                ", download_bytes=" + downloadBytes +
                ", upload_bytes=" + uploadBytes +
                ", session_start_time=" + sessionStartTime +
                '}';
    }
}
