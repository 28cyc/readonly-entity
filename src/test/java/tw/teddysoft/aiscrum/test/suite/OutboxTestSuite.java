package tw.teddysoft.aiscrum.test.suite;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SuiteDisplayName("Outbox Tests - Full Integration")
@SelectClasses({
        OutboxTestSuite.ProfileSetter.class
})
@SelectPackages({
        "tw.teddysoft.aiscrum.product"
})
@ExcludeClassNamePatterns(".*ControllerTest")
public class OutboxTestSuite {

    @SpringBootTest
    public static class ProfileSetter {
        static {
            System.setProperty("spring.profiles.active", "test-outbox");
        }

        @Test
        void setProfile() {
        }
    }
}
