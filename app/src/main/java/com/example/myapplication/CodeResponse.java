package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class CodeResponse {
    @SerializedName("message")
    public String message;
    @SerializedName("code")
    public String code;
}
