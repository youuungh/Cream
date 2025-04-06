<img src="https://github.com/user-attachments/assets/37b81499-bcc6-40a4-b01b-9bdb5b8d8e71" />

# Cream
Cream은 실제 서비스를 클론 코딩하여 최신 Android 기술 스택의 활용과 실무 역량을 키우기 위해 개발된 쇼핑 애플리케이션입니다. </br>
UI/UX 측면에서는 Jetpack Compose를 사용하여 선언적이고 현대적인 UI를 구현했으며, Material Design 3 가이드라인을 따른 일관된 디자인 시스템으로 직관적인 사용자 경험을 제공합니다. </br>
Firebase 플랫폼을 활용하여 Crashlytics로 앱의 안정성을 모니터링하고, Authentication을 통해 구글, 카카오, 네이버 등 소셜 로그인을 지원하는 간편한 회원 시스템을 구축했습니다. </br>
아키텍처 설계에 있어서는 클린 아키텍처를 기반으로 코드의 가독성, 유지보수성, 테스트 용이성을 향상시켰으며, </br>
MVI(Model-View-Intent) 패턴을 채택하여 단방향 데이터 흐름과 예측 가능한 상태 관리를 구현했습니다.

## Downloads

<a href='https://github.com/youuungh/Cream/releases'><img alt='Get it on GitHub' height="80" src='https://github.com/youuungh/Shopang/assets/97438155/d19cce65-aaa3-4d0d-a7ec-d0d26479e9f2'/></a>

## Demo Video

<a href="https://streamable.com/xyx025">
  <img src="https://github.com/user-attachments/assets/c31c4ee2-0dbc-40e9-b24c-36d50aec7852" height="600px"/>
</a>

## Architecture

- ### Clean Architecture
<img src="https://github.com/user-attachments/assets/ae0fe1c1-2823-480b-86bd-4bd663473194" width="500" /> </br>
> Presentation Layer: 사용자 인터페이스를 담당하는 레이어로,
Acitivty, Fragment, View, ViewModel 등으로 구성, 사용자 입력을 처리하고, 데이터를 화면에 표시하며 이 레이어는 UI와 관련된 코드만 포함하며, 비즈니스 로직은 포함하지 않음 </br>

> Domain Layer: 애플리케이션의 비즈니스 로직을 담당하는 레이어로, Use case와 Entity, Interface로 구성. Use case는 애플리케이션의 기능을 구현하는 로직을 담당, 
Entity는 데이터 모델을 담당, Interface는 Data Layer의 구현체를 표현함 </br>

> Data Layer: 애플리케이션에서 사용하는 데이터를 관리하는 레이어로 Repository, DataSource 등으로 구성. 데이터를 로컬 데이터베이스 서버에서 가져와 Use case에 전달함 </br>

> 각 레이어 별로 의존성을 최소화하기 위해 의존성 주입(DI)가 필요

- ### [MVI(Model-View-Intent)](https://proandroiddev.com/mvi-architecture-with-kotlin-flows-and-channels-d36820b2028d)
<img src="https://github.com/user-attachments/assets/f4226669-3536-4470-8af2-a922c8a8574c" width="500" /> </br>


## Built With
<ul>
  <li><a href="https://developer.android.com/develop/ui/compose/documentation?hl=ko">Jetpack Compose</a></li>
  <li><a href="https://github.com/coil-kt/coil">Coil</a></li>
  <li><a href="https://developer.android.com/topic/libraries/architecture/datastore?hl=ko">DataStore</a></li>
  <li><a href="https://square.github.io/retrofit">Retrofit2</a></li>
  <li><a href="https://developer.android.com/training/dependency-injection/hilt-android">Hilt</a></li>
  <li><a href="https://developer.android.com/topic/libraries/architecture/room" target="_blank">Room</a></li>
  <li><a href="https://github.com/google/gson" target="_blank">Gson</a></li>
  <li><a href="https://github.com/JakeWharton/timber" target="_blank">Timber</a></li>
  <li><a href="https://developer.android.com/topic/libraries/architecture/viewmodel" target="_blank">ViewModel</a></li>
  <li><a href="https://developer.android.com/kotlin/coroutines" target="_blank">Coroutines</a></li>
  <li><a href="https://developer.android.com/kotlin/flow/stateflow-and-sharedflow" target="_blank">Flow</a></li>
  <li><a href="https://m3.material.io" target="_blank">Material You</a></li>
  <li><a href="https://airbnb.io/lottie/#/">Lottie</a></li>
  <li><a href="https://firebase.google.com/" target="_blank">Firebase</a></li>
  <li><a href="https://developers.naver.com/docs/login/api/api.md" target="_blank">Naver Login SDK</a></li>
  <li><a href="https://developers.kakao.com/docs/latest/ko/kakaologin/android" target="_blank">Kakao Login SDK</a></li>
</ul>

## Screenshots
<table align="center">
  <tr>
    <td><img src="https://github.com/user-attachments/assets/9eea21f2-c12c-4299-8e60-e5d63d525ae5" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/3736d2af-8fe6-449d-9615-41fcf16cd146" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/9be09758-15c9-431a-813f-113f8303290d" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/cfdfb961-6066-41c7-9c37-59a366f378c8" height="20%" /></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/923810ee-ec67-46b1-8516-dc68ba954a62" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/353016df-7794-428c-b276-77eecbc10257" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/99991f17-4fb1-4384-bd31-d4c4a3e2e04d" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/8f476a54-b18b-4ccf-a769-f84b0e56d221" height="20%" /></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/6eee442b-48b2-4737-a833-f3592ee0cef3" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/12458274-39a4-43f8-a268-462b0d4a3df6" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/2d16e04c-5ec5-4772-9f37-8d43dacc7093" height="20%" /></td>
    <td><img src="https://github.com/user-attachments/assets/8089cb22-2de3-4118-87b7-3a9dca919004" height="20%" /></td>
  </tr>
</table>
