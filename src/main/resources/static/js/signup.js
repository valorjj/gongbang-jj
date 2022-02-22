$(function () {
    // 아이디 유효성 검사
    $("#memberSignupId").keyup(function(){
         // 해당 태그에 키보드가 눌렸을 때 이벤트 : keyup
         var memberId = $("#memberSignupId").val();
         var idj = /^[a-z0-9]{5,15}$/; // 정규표현식
         if(!idj.test(memberId)){ //정규표현식이 다를경우
              $("#idCheckLabel").html("영소문자 5~15 글자만 가능합니다.");
              $("#idCheckLabel").css('color', 'green');
         } else {
             // 아이디 중복체크 비동기 통신
             $.ajax({
                 url: "/member/idCheck",
                 data : {"memberId" : memberId},
                 method : "GET",
                 success : function(result){
                     if(result==1){
                         $("#idCheckLabel").html("현재 사용중인 아이디입니다.");
                         $("#idCheckLabel").css('color', 'red');
                         $("#idCheckLabel").css('font-weight', 'bold');
                     } else {
                         $("#idCheckLabel").html("사용가능");
                         $("#idCheckLabel").css('color', 'blue');
                         $("#idCheckLabel").css('font-weight', 'bold');
                     }
                 }
             });
         }
    });

    // 이메일 유효성검사
    $("#memberEmail").keyup(function () {
        var emailj = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        var memberEmail = $("#memberEmail").val();
        if (!emailj.test(memberEmail)) {
            $("#emailcheck").html("이메일 형식으로 입력해주세요");
            $("#emailcheck").css('color', 'green');
        } else {
            // 이메일 중복체크 비동기 통신
            $.ajax({
                url: "/member/emailcheck",
                data: { "memberEmail": memberEmail },
                method: "GET",
                success: function (result) {
                    if (result == 1) {
                        $("#emailcheck").html("현재 사용중인 이메일 입니다.");
                        $("#emailcheck").css('color', 'red');
                        $("#emailcheck").css('font-weight', 'bold');
                    } else {
                        $("#emailcheck").html("사용가능");
                        $("#emailcheck").css('color', 'blue');
                        $("#emailcheck").css('font-weight', 'bold');
                    }
                } // success send
            }); // ajax 함수  end
        }
    });

    // 패스워드 유효성검사
    $("#memberPassword").keyup(function () {
        var pwj = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,15}$/;
        // 영대소문자+숫자+특수문자[ !@#$%^&*()+|= ] 8~15포함
        var memberPassword = $("#memberPassword").val();
        if (!pwj.test(memberPassword)) {
            $("#passwordcheck").html("영대소문자+숫자+특수문자[ !@#$%^&*()+|= ] 8~15포함");
            $("#passwordcheck").css('color', 'green');

        } else {
            $("#passwordcheck").html("사용가능");
            $("#passwordcheck").css('color', 'blue');
            $("#passwordcheck").css('font-weight', 'bold');
        }
    });

    // 패스워드 확인 유효성검사
    $("#memberSignupPasswordConfirm").keyup(function () {
        var pwj = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/;
        // 숫자', '문자', '특수문자' 무조건 1개 이상, 비밀번호 '최소 8자에서 최대 16자'까지 허용
        var memberPassword = $("#memberSignupPassword").val();
        var memberPasswordConfirm = $("#memberSignupPasswordConfirm").val();
        if (!pwj.test(memberPasswordConfirm)) {
            $("#passwordcheck").html("숫자', '문자', '특수문자' 포함 , '최소 8문자~16글자 허용.");
            $("#passwordcheck").css('color', 'green');
            // 패스워드가 일치하지 않은 경우
        } else if (memberPassword != memberPasswordConfirm) {
            $("#passwordcheck").html("서로 패스워드가 다릅니다.");
            $("#passwordcheck").css('color', 'red');
            $("#passwordcheck").css('font-weight', 'bold');

        } else {
            $("#passwordcheck").html("사용가능");
            $("#passwordcheck").css('color', 'blue');
            $("#passwordcheck").css('font-weight', 'bold');
        }
    });

    // 이름 유효성검사
    $("#memberNameSignup").keyup(function () {
        var namej = /^[A-Za-z가-힣]{1,15}$/;	// 이름 정규표현식
        var memberName = $("#memberNameSignup").val();
        if (!namej.test(memberName)) {
            $("#namecheck").html("영대문자/한글 1~15 허용");
            $("#namecheck").css('color', 'green');
            $("#namecheck").css('font-weight', 'bold');

        } else {
            $("#namecheck").html("사용가능");
            $("#namecheck").css('color', 'blue');
            $("#namecheck").css('font-weight', 'bold');
        }
    });

    // 연락처 유효성검사
    $("#memberPhoneSignup").keyup(function () {
        var phonej = /^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$/; // 연락처
        var memberPhone = $("#memberPhoneSignup").val();
        if (!phonej.test(memberPhone)) {
            $("#phonecheck").html("01X-XXXX-XXXX 형식으로 입력해주세요");
            $("#phonecheck").css('color', 'red');
            $("#phonecheck").css('font-weight', 'bold');

        } else {
            $("#phonecheck").html("사용가능");
            $("#phonecheck").css('color', 'blue');
            $("#phonecheck").css('font-weight', 'bold');
        }
    });

    $("#formsubmit").click( function(){
        if( !$('input[name=signupsign]').is(":checked") ) {
             alert(" 회원가입 약관 동의시 회원가입이 가능합니다 . ");
        }
        else if( !$('input[name=infosign]').is(":checked") ) {
             alert(" 개인정보처리방침 동의시 회원가입이 가능합니다 . ");
        }
        else if( $("#emailcheck").html() != "사용가능" ){
             alert(" 이메일 불가능합니다 . ");
        }else if( $("#passwordcheck").html() != "사용가능" ){
             alert(" 패스워드 불가능합니다 . ");
        }
        else if( $("#idCheckLabel").html() != "사용가능" ){
          alert(" 아이디 불가능합니다 . ");
        }
        else if( $("#namecheck").html() != "사용가능" ){
            alert(" 이름 불가능합니다 . ");
        }
        else if( $("#phonecheck").html() != "사용가능" ){
            alert(" 연락처가 불가능합니다 . ");
        }else{
            $("form").submit(); // 모든 유효성검사 통과시 폼 전송
        }
    });
}); // 함수 end