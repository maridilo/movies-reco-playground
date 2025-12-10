package com.acme.reco.api.dto;

public class IncomeStatDTO {

    private int year;
    private int month; // 1-12
    private long totalAmountCents;

    public IncomeStatDTO() {
    }

    public IncomeStatDTO(int year, int month, long totalAmountCents) {
        this.year = year;
        this.month = month;
        this.totalAmountCents = totalAmountCents;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getTotalAmountCents() {
        return totalAmountCents;
    }

    public void setTotalAmountCents(long totalAmountCents) {
        this.totalAmountCents = totalAmountCents;
    }
}
