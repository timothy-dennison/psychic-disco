package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.commons.text.StringEscapeUtils;


@RestController
public class HelloController {

	@Secured("ROLE_USER")
	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@PostMapping("/launch")
	public String launch(@RequestBody String inject) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		String[] commands = { "bash", "-c", inject };
		Process p = runtime.exec(commands);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		List<String> outputLines = new LinkedList<String>();
		while ((line = reader.readLine()) != null) {
			outputLines.add(line);
		}
		return Arrays.toString(outputLines.toArray());
	}

	@GetMapping("/random")
	public int random() {
		Random rand = new Random();
		return rand.nextInt(100);
	}

	@PostMapping("/textAttack")
	public String textAttack(@RequestBody String payload) {
		StringSubstitutor interpolator = StringSubstitutor.createInterpolator();
		// try payload=${script:javascript:java.lang.Runtime.getRuntime().exec('touch
		// /tmp/foo')} to test
		try {
			String pwn = interpolator.replace(payload);
		} catch (Exception e) {
			System.out.println(e);
		}
		return "Result for: " + StringEscapeUtils.escapeHtml4(payload);
	}

}
