package org.fastcampus.proejct.auth.converter.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record UserPrincipal(
        Long id,
        String email,
        String password,
        String username,
        Boolean isAdmin,
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> oAuthAttributes
) implements UserDetails, OAuth2User {

    public static UserPrincipal of(
            Long id,
            String email,
            String password,
            String username,
            Boolean isAdmin
    ) {
        return UserPrincipal.of(
                id,
                email,
                password,
                username,
                isAdmin,
                Map.of()
        );
    }

    public static UserPrincipal of(
            Long id,
            String email,
            String password,
            String username,
            Boolean adminCheck,
            Map<String, Object> oAuthAttributes
    ) {
        Set<RoleType> roleTypes = new HashSet<>();
        if (adminCheck) roleTypes.add(RoleType.ADMIN);
        roleTypes.add(RoleType.USER);
        return new UserPrincipal(
                id,
                email,
                password,
                username,
                adminCheck,
                roleTypes.stream().map(RoleType::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toUnmodifiableSet()),
                oAuthAttributes
        );
    }

    public static UserPrincipal from(UserInfoDto dto) {
        return UserPrincipal.of(
                dto.id(),
                dto.email(),
                dto.password(),
                dto.name(),
                dto.adminCheck()
        );
    }

    public UserInfoDto toDto() {
        return UserInfoDto.of(id, email, password, username);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuthAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 사용자 검증은 id를 가지고 해야합니다. (board.id = userInfo.id)
     **/
    public Long getUserId() {
        return id;
    }

    /**
     * User 클래스 상속을 받지 않아 실질적으로 필요가 없다.
     * 게정 만료 여부,
     * 계정 lock 여부,
     * 인증 만료 여부,
     * 활성 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return email;
    }

    public enum RoleType {
        USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }
}

