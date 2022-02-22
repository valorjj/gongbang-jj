package com.ezen.service;

import com.ezen.domain.dto.IntergratedDto;
import com.ezen.domain.dto.MemberDto;
import com.ezen.domain.entity.MemberEntity;
import com.ezen.domain.entity.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private HttpServletRequest request;

    // 회원등록 메소드
    public boolean memberSignup(MemberDto memberDto) {
        // 패스워드 암호화 [ BCryptPasswordEncoder ]
        // 1. 암호화 클래스 객체 생성
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // 2. 입력받은 memberDto내 패스워드 재설정 [ 암호화객체명.encode( 입력받은 패스워드 ) ]
        memberDto.setMemberPassword(passwordEncoder.encode(memberDto.getMemberPassword()));
        memberRepository.save(memberDto.toentity()); // save(entity) : insert / update : Entity를 DB에 저장
        return true;
    }

    // 회원수정 메소드
    public boolean memberUpdate(MemberDto memberDto) {
        // 패스워드 암호화 [ BCryptPasswordEncoder ]
        // 1. 암호화 클래스 객체 생성
        // BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // 2. 입력받은 memberDto내 패스워드 재설정 [ 암호화객체명.encode( 입력받은 패스워드 ) ]
        // memberDto.setMemberPassword(passwordEncoder.encode(memberDto.getMemberPassword()));
        memberRepository.save(memberDto.toentity()); // save(entity) : insert / update : Entity를 DB에 저장
        return true;
    }

    // 이메일 중복체크
    public boolean emailcheck(String memberEmail) {
        // 1. 모든 엔티티 가져오기
        List<MemberEntity> memberEntities = memberRepository.findAll();
        // 2. 모든 엔티티 반복문 돌려서 엔티티 하나씩 가쟈오기
        for (MemberEntity memberEntity : memberEntities) {
            // 3. 해당 엔티티가 입력한 아이디와 동일하면
            if (memberEntity.getMemberEmail().equals(memberEmail)) {
                return true; // 중복
            }
        }
        return false; // 중복 없음
    }

    // 아이디 중복체크
    public boolean idCheck(String memberId) {
        // 1. 모든 엔티티 가져오기
        List<MemberEntity> memberEntities = memberRepository.findAll();
        // 2. 모든 엔티티 반복문 돌려서 엔티티 하나씩 가쟈오기
        for (MemberEntity memberEntity : memberEntities) {
            // 3. 해당 엔티티가 입력한 아이디와 동일하면
            if (memberEntity.getMemberEmail().equals(memberId)) {
                return true; // 중복
            }
        }
        return false; // 중복 없음
    }

    // 회원 번호로 회원 entity 가져오기
    public MemberEntity getMember(int memberNo) {
        Optional<MemberEntity> entityOptional = memberRepository.findById(memberNo);
        return entityOptional.get();
    }

    // 회원번호 -> 회원정보 반환
    public MemberDto getmemberDto(int memberNo) {
        // memberRepository.findAll(); : 모든 엔티티 호출
        // memberRepository.findById( pk값 ) : 해당 pk값의 엔티티 호출
        // 1. 해당 회원번호[pk] 만 엔티티 호출
        Optional<MemberEntity> memberEntity = memberRepository.findById(memberNo);
        // 2. 찾은 entity를 dto 변경후 반환 [ 패스워드 , 수정날짜 제외 ]

        return MemberDto.builder()
                // 02-17 조지훈
                .memberNo(memberEntity.get().getMemberNo())
                // end
                .memberName(memberEntity.get().getMemberName())
                .memberId(memberEntity.get().getMemberId())
                .memberEmail(memberEntity.get().getMemberEmail())
                .memberPhone(memberEntity.get().getMemberPhone())
                .memberPoint(memberEntity.get().getMemberPoint())
                .memberGender(memberEntity.get().getMemberGender())
                .memberGrade(memberEntity.get().getMemberGrade())
                .createdDate(memberEntity.get().getCreatedDate())
                .build();
    }

    // 회원탈퇴
    @Transactional
    public boolean delete(int memberNo, String passwordconfirm) {
        // 1. 로그인된 회원번호의 엔티티[레코드] 호출
        Optional<MemberEntity> entityOptional = memberRepository.findById(memberNo);
        // Optional 클래스 : null 포함 객체 저장
        // 2. 해당 엔티티내 패스워드가 확인패스워드와 동일하면
        if (entityOptional.get().getMemberPassword().equals(passwordconfirm)) {
            // Optional 클래스 -> memberEntity.get() : Optional 내 객체 호출
            memberRepository.delete(entityOptional.get());
            return true; // 회원탈퇴
        }
        return false; // 회원탈퇴 X
    }

    // 회원 아이디 찾기
    // 이름, 핸드폰 번호 받고 회원정보를 조회합니다.
    public MemberEntity findMyId(String name, String phone) {

        // 1. Member 엔티티를 불러옵니다.
        List<MemberEntity> memberEntities = memberRepository.findAll();
        // 2. 이름, 핸드폰 번호가 일치하는 회원이 있는지 검사합니다.
        for (MemberEntity member : memberEntities) {
            if (member.getMemberName().equals(name) && member.getMemberPhone().equals(phone)) {
                // 3. 일치하는 회원이 있으면
                return member;
            }
        }
        return null;
    }

    // 비밀번호
    public String findpassword(MemberDto memberDto) {
        // 1. 모든 엔티티 호출
        List<MemberEntity> memberEntities = memberRepository.findAll();
        // 2. 반복문 이용한 모든 엔티티를 하나씩 꺼내보기
        for (MemberEntity memberEntity : memberEntities) {
            // 3. 만약에 해당 엔티티가 이름과 이메일이 동일하면
            if (memberEntity.getMemberEmail().equals(memberDto.getMemberEmail()) &&
                    memberEntity.getMemberPhone().equals(memberDto.getMemberPhone())) {
                // 4. 패스워드를 반환한다
                return memberEntity.getMemberPassword();
            }
        }
        // 5. 만약에 동일한 정보가 없으면
        return null;
    }

    public MemberEntity getMemberEntity(int memberNo) {
        Optional<MemberEntity> entityOptional = memberRepository.findById(memberNo);
        return entityOptional.get();
    }

    @Override // /member/logincontroller URL 호출시 실행되는 메소드 [ 로그인처리(인증처리) 메소드 ]
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        // 회원 아이디로 회원엔티티 찾기
        Optional<MemberEntity> entityOptional = memberRepository.findBymemberId(memberId);
        MemberEntity memberEntity = entityOptional.orElse(null);
        // .orElse( null ) : 만약에 엔티티가 없으면 null

        // 찾은 회원엔티티의 권한[키] 을 리스트에 담기
        List<GrantedAuthority> authorities = new ArrayList<>();
        assert memberEntity != null;
        authorities.add(new SimpleGrantedAuthority(memberEntity.getRolekey()));
        // GrantedAuthority : 권한 [ 키 저장 가능한 클래스 ]

        // 세션 부여
        MemberDto loginDto = MemberDto.builder().memberEmail(memberEntity.getMemberEmail())
                .memberNo(memberEntity.getMemberNo()).build();
        HttpSession session = request.getSession(); // 서버내 세션 가져오기
        session.setAttribute("logindto", loginDto); // 세션 설정

        // 회원정보와 권한을 갖는 UserDetails 반환
        return new IntergratedDto(memberEntity, authorities);
    }

    // 충전금액 증가
    @Transactional
    public boolean payment(int memberNo, int memberPoint, int totalpay) {
        try {
            // 1. 수정할 엔티티 찾는다
            Optional<MemberEntity> entityOptional = memberRepository.findById(memberNo);
            // 2. 엔티티를 수정한다[엔티티변화=>DB변경]
            entityOptional.get().setMemberPoint(memberPoint + totalpay);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // 02-15 채널 정보 등록하기 - 조지훈
    @Transactional
    public boolean channelregistration(MemberEntity memberEntity) {
        try {
            Optional<MemberEntity> entityOptional = memberRepository.findById(memberEntity.getMemberNo());
            entityOptional.get().setChannelTitle(memberEntity.getChannelTitle());
            entityOptional.get().setChannelContent(memberEntity.getChannelContent());
            entityOptional.get().setChannelImg(memberEntity.getChannelImg());
            return true;
        } catch (Exception e) {
            System.out.println("채널정보등록 실패 " + e);
            return false;
        }
    }

    // 02-17 채널 정보 수정시 기존이미지 삭제버튼 - 조지훈
    @Transactional
    public boolean channelimgdelete(int memberNo) {
        try {
            MemberEntity memberEntity = memberRepository.findById(memberNo).get();
            memberEntity.setChannelImg(null);
            return true;
        } catch (Exception e) {
            System.out.println("기존 사진 삭제 실패" + e);
            return false;
        }
    }

    // 02-17 강사소개 작성여부 체크 - 조지훈
    public boolean channelcheck(int memberNo) {
        MemberEntity memberEntity = memberRepository.findById(memberNo).get();
        if (memberEntity.getChannelContent() == null) {
            return true;
        } else {
            return false;
        }
    }
    // @Transactional
    // public boolean channelupdate(MemberEntity memberEntity) {
    // try {
    //
    // }catch (Exception e) {}
    //
    // return true;
    // }

}
