package jp.ymatsukawa.stockapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockapiApplication.class, args);
	}
}
