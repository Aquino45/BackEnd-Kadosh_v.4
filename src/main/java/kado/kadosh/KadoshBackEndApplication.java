package kado.kadosh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 👈 IMPORTANTE

@SpringBootApplication
@EnableScheduling // 👈 ESTO ACTIVA EL "RELOJ" INTERNO
public class KadoshBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(KadoshBackEndApplication.class, args);
	}

}
