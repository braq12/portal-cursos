package com.portal.portal_cursos.service;

import com.portal.portal_cursos.dtos.LoginRequest;
import com.portal.portal_cursos.dtos.LoginResponse;

public interface IAuthService {
    LoginResponse login(LoginRequest request);
}
