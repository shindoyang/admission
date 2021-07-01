	package com.ut.user.unit.test;

	import com.ut.user.unit.controller.*;
	import org.junit.Assert;
	import org.junit.runner.RunWith;
	import org.junit.runners.Suite;

	@RunWith(Suite.class)
	@Suite.SuiteClasses({
			AppControllerTest.class,
			AuthorityGroupControllerTest.class,
			AuthorityControllerTest.class,
			AuthorityManageContrllerTest.class,
			UserControllerTest.class
	})
	public class AllTest {


	}
