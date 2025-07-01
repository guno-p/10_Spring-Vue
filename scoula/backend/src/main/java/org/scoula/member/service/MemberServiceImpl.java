package org.scoula.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.dto.MemberJoinDTO;
import org.scoula.member.dto.MemberUpdateDTO;
import org.scoula.member.exception.PasswordMissmatchException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.account.domain.AuthVO;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.io.File;
import java.io.IOException;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    final PasswordEncoder passwordEncoder;
    final MemberMapper mapper;

    @Override
    public boolean checkDuplicate(String username) {
        MemberVO member = mapper.findByUsername(username);
        return member != null ? true : false;
    }

    @Override
    public MemberDTO get(String username) {
        MemberVO member = Optional.ofNullable(mapper.get(username))
                .orElseThrow(NoSuchElementException::new);
                // 사용자 정의 예외로 추후 수정 가능
        return MemberDTO.of(member);
    }

    private void saveAvatar(MultipartFile avatar, String username) {
        //아바타 업로드
        if(avatar != null && !avatar.isEmpty()) {
            File dest = new File("c:/upload/avatar", username + ".png");
            try {
                avatar.transferTo(dest);
            } catch (IOException e) {
                throw new RuntimeException(e);
                // 예외 타입을 런타입으로 바꿨음
                // @Transactional 은 런타입에 대해서만 반응
                // 최종적으로 Advice 예외처리가 넘어간다잇
            }
        }
    }

    @Transactional
    @Override
    public MemberDTO join(MemberJoinDTO dto) {
        MemberVO member = dto.toVO();

        member.setPassword(passwordEncoder.encode(member.getPassword())); // 비밀번호 암호화
        // equals 불가능, matches 사용해야함
        mapper.insert(member); // 예외 발생 가능

        AuthVO auth = new AuthVO();
        auth.setUsername(member.getUsername());
        auth.setAuth("ROLE_MEMBER");
        mapper.insertAuth(auth); // 예외 발생 가능

        saveAvatar(dto.getAvatar(), member.getUsername()); // 예외 발생 가능
        
        // 하나라도 예외 발생하면 안됨 -> @Transactional 처리

        return get(member.getUsername());
    }

    @Override
    public MemberDTO update(MemberUpdateDTO member) {
        MemberVO vo = mapper.get(member.getUsername());
        if(!passwordEncoder.matches(member.getPassword(),vo.getPassword())) { // 비밀번호 일치 확인
            throw new PasswordMissmatchException();
        }
        mapper.update(member.toVO());
        saveAvatar(member.getAvatar(), member.getUsername());
        return get(member.getUsername());
    }
}
