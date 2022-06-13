package com.verint.engageapidemo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ThrowItBackModel {
    private String orginalMessage;
    private String newMessage;

    public ThrowItBackModel(String originalMessage) {
        this.orginalMessage = originalMessage;
        this.newMessage = originalMessage.toUpperCase();
    }
}
