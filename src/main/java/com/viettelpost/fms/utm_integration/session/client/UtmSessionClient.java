package com.viettelpost.fms.utm_integration.session.client;

import com.viettelpost.fms.utm_integration.session.dto.UtmSessionClientConnectResult;
import com.viettelpost.fms.utm_integration.session.dto.UtmSessionClientDisconnectRequest;

public interface UtmSessionClient {

    UtmSessionClientConnectResult connect();

    void disconnect(UtmSessionClientDisconnectRequest request);
}
