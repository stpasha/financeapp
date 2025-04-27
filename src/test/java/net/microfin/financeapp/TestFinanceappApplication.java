package net.microfin.financeapp;

import org.springframework.boot.SpringApplication;

public class TestFinanceappApplication {

	public static void main(String[] args) {
		SpringApplication.from(FinanceappApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
