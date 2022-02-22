/* 결제 방식 선택  */
function paymentselect( payselect ){
	document.getElementById("payselect").innerHTML= payselect;
}
/* 결제 API 아임포트 */
function payment(){

/*
    var IMP = window.IMP;
	IMP.init("imp65839811"); // [본인]관리자 식별코드 [ 관리자 계정마다 다름]
    IMP.request_pay({ // 결제 요청변수
	    pg: "html5_inicis",	// pg사 [ 아임포트 관리자페이지에서 선택한 pg사 ]
	    pay_method: "card",	// 결제방식
	    merchant_uid: "ORD20180131-0000011", // 주문번호[별도]
	    name: "나만의 쇼핑몰", // 결제창에 나오는 결제이름
	    amount: "1000",	// 결제금액
	    buyer_email: "gildong@gmail.com",
	    buyer_name: "asd",
	    buyer_tel: "asd",
	    buyer_addr: "asdasd",
	    buyer_postcode: "asdasd"	// 우편번호
		  }, function (rsp) { // callback
		      if (rsp.success) { // 결제 성공했을때 -> 주문 완료 페이지로 이동 []
		      } else {

		      }
	  });
*/

    var totalpay =$("#totalpay").val();
    var payselect =$("#payselect").val();
	var IMP = window.IMP;
	IMP.init("imp65839811"); // [본인]관리자 식별코드 [ 관리자 계정마다 다름]
    IMP.request_pay({ // 결제 요청변수
	    pg: "html5_inicis",	// pg사 [ 아임포트 관리자페이지에서 선택한 pg사 ]
	    pay_method: payselect,	// 결제방식
	    merchant_uid: "ORD20180131-0000011", // 주문번호[별도]
	    name: "ezen 공방", // 결제창에 나오는 결제이름
	    amount: totalpay,	// 결제금액
	    buyer_email: "gildong@gmail.com",
	    buyer_name: $("#memberId").val(),
	    buyer_point: $("#memberPoint").val()

		  }, function (rsp) { // callback
		      if (rsp.success) { // 결제 성공했을때 -> 주문 완료 페이지로 이동 []
		      } else {

				// 결제 실패 했을때  // 테스트 : 결제 성공
				$.ajax({
					url : "/member/paymentcontroller" ,
					data : {
						"totalpay" : totalpay
					 } ,
					success : function( result ){ // 결제성공 과  db처리 성공시 결제주문 완료 페이지 이동
						if(result==1){
						    location.href ="/member/reservationListController";
						} else if(result==2){
						    location.href ="/index";
						}

					}
				});
		      }
	  });
}
/* 결제 API 아임포트 END */


function pchange2( i , type , stock , price ){
	var p_count = document.getElementById("pcount"+i).value*1;
	if( type=='m'){		p_count -= 1;
		if( p_count<1){
			alert("최소인원은 1명 이상 선택바람"); p_count = 1;
		}
	}else if( type =="p" ){	p_count += 1;
		if( p_count > stock ){
			alert("죄송합니다. 인원이 마감되었습니다.");	p_count = stock;
		}
	}else{
		if( p_count > stock ){
			alert("죄송합니다. 인원이 마감되었습니다.");	p_count = stock;
		}
		if( p_count<1){	// 만약에 1보다 작아지면
			alert("최소인원은 1명 이상 선택바람"); p_count = 1;
		}
	}
	document.getElementById("pcount"+i).value = p_count;
	var totalprice = p_count * price; // 총가격 = 제품수량 * 제품가격
	document.getElementById("total"+i).innerHTML = totalprice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','); 	// . innerHTML 속성 태그 [ div ]
	$.ajax({
			url :  "../../controller/productcartdeletecontroller.jsp" ,
			data : { type : type , p_num : -1 , p_size : -1 , i : i , p_count : p_count } ,
			success : function( result ){
				location.reload();
			}
	});
}