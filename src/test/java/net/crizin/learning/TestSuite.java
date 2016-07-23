package net.crizin.learning;

import net.crizin.learning.controller.AuthenticateControllerTest;
import net.crizin.learning.controller.DefaultControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		AuthenticateControllerTest.class,
		DefaultControllerTest.class
})
public class TestSuite {
}