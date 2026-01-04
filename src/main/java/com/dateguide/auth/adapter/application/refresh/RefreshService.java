package com.dateguide.auth.adapter.application.refresh;

import com.dateguide.auth.adapter.in.refresh.RefreshResponse;

public interface RefreshService {
    RefreshResponse refresh(String refreshToken);
}
