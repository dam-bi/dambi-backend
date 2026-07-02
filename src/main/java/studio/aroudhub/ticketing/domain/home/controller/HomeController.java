package studio.aroudhub.ticketing.domain.home.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("""
                <!doctype html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Ticketing</title>
                    <style>
                        * { box-sizing: border-box; }
                        body {
                            margin: 0;
                            font-family: Arial, "Malgun Gothic", sans-serif;
                            color: #1f2937;
                            background: #f6f7f9;
                        }
                        main {
                            width: min(1080px, calc(100% - 32px));
                            margin: 0 auto;
                            padding: 32px 0;
                        }
                        .event-banner {
                            display: grid;
                            min-height: 260px;
                            align-content: end;
                            padding: 28px;
                            color: #ffffff;
                            text-decoration: none;
                            background:
                                linear-gradient(90deg, rgba(17, 24, 39, .84), rgba(17, 24, 39, .26)),
                                url("https://images.unsplash.com/photo-1540039155733-5bb30b53aa14?auto=format&fit=crop&w=1400&q=80") center/cover;
                            border-radius: 8px;
                        }
                        .event-banner h1 {
                            max-width: 620px;
                            margin: 0 0 10px;
                            font-size: 34px;
                            line-height: 1.25;
                            letter-spacing: 0;
                        }
                        .event-banner p {
                            max-width: 560px;
                            margin: 0;
                            line-height: 1.6;
                        }
                        @media (max-width: 560px) {
                            .event-banner {
                                min-height: 230px;
                                padding: 22px;
                            }
                            .event-banner h1 { font-size: 26px; }
                        }
                    </style>
                </head>
                <body>
                    <main>
                        <a class="event-banner" href="/events">
                            <h1>진행 중인 이벤트</h1>
                            <p>할인과 추천 공연을 확인하고 원하는 콘서트 예매로 이동하세요.</p>
                        </a>
                    </main>
                </body>
                </html>
                """);
    }
}
