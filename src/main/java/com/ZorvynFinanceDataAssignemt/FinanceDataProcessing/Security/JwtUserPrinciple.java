package com.ZorvynFinanceDataAssignemt.FinanceDataProcessing.Security;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public record JwtUserPrinciple(
        Long userId,
        String username,
        Collection<? extends GrantedAuthority> authorities
) {
}