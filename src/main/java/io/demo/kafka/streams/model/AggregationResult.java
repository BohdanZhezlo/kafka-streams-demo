package io.demo.kafka.streams.model;

public class AggregationResult {

    private String accountNumber;
    private Long periodStart = 0L;
    private Long periodEnd = 0L;
    private Long downloadBytes = 0L;
    private Long uploadBytes = 0L;

    public AggregationResult() {
    }

    public AggregationResult(String accountNumber, Long periodStart, Long periodEnd, Long downloadBytes, Long uploadBytes) {
        this.accountNumber = accountNumber;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.downloadBytes = downloadBytes;
        this.uploadBytes = uploadBytes;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Long periodStart) {
        this.periodStart = periodStart;
    }

    public Long getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Long periodEnd) {
        this.periodEnd = periodEnd;
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

    @Override
    public String toString() {
        return "AggregationResult{" +
                "account_sn='" + accountNumber + '\'' +
                ", period_start='" + periodStart + '\'' +
                ", period_end='" + periodEnd + '\'' +
                ", download_bytes=" + downloadBytes +
                ", upload_bytes=" + uploadBytes +
                '}';
    }
}
