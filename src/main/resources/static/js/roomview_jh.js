
var roomaddress1 = $("#roomaddress1").val();
var roomaddress2 = $("#roomaddress2").val();

var mapContainer = document.getElementById('map9'), // 지도를 표시할 div
    mapOption = {
        center: new kakao.maps.LatLng(roomaddress1, roomaddress2), // 지도의 중심좌표
        level: 12 // 지도의 확대 레벨
    };

// 지도를 표시할 div와  지도 옵션으로  지도를 생성합니다
var map9 = new kakao.maps.Map(mapContainer, mapOption);
// 지도에 마커를 생성하고 표시한다
var marker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(roomaddress1, roomaddress2), // 마커의 좌표
    map: map9 // 마커를 표시할 지도 객체
});

marker.setMap(map9);