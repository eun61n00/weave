package com.weave.authservice.repository;

import com.weave.authservice.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data Redis를 사용하여 RefreshToken을 관리하는 리포지토리입니다.
 * CrudRepository를 상속받아 기본적인 CRUD 기능을 자동으로 제공받습니다.
 * Key 타입은 RefreshToken의 @Id 필드 타입인 String (userId)입니다.
 */
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
