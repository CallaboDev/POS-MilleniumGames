package com.umoar.posmilleniumgames;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "app.browser.auto-open=false")
@ActiveProfiles("test")
class PosMilleniumGamesApplicationTests {

    @Test
    void contextLoads() {
    }

}
