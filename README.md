# gomop

#위치 공유 SNS어플리케이션
와치를 통해 나의 위치를 어플리케이션에 공유해보세요!


#주의사항
모바일 앱은 kakao 지도 API를 사용하였습니다.
해당 API가 애뮬레이터는 지원하지 않아 실물 디바이스에서만 테스트 가능합니다.
애뮬레이터로 테스트 하는 경우 해당 코드를 모두 주석처리 해야합니다.


와치 git
https://github.com/seoyoungshinn/gomop_watch.git

-> 와치에서는 사용자의 실시간 좌표를 확인하여 갱신하고, 버튼을 누르면 sns에 자신의 위치를 업데이트 하는 기능을 넣었음
앱을 사용하는 동안 1초 단위로 사용자의 좌표가 업데이트 되지만 이름 매번 sns에 업데이트 하면 개인정보 유출의 우려가 있어 버튼을 누를 때만 sns에 업로드함
구글 gps와 카카오 tts api사용, 파이어베이스 연동, 진동 센서를 활용하였음
모바일에서는 지도 화면으로 팔로잉 하는 사람들이 최근에 업데이트한 위치를 확인 할 수 있음
