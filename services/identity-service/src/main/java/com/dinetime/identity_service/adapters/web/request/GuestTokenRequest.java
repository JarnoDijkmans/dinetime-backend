package com.dinetime.identity_service.adapters.web.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuestTokenRequest {
    private String deviceId;
}
