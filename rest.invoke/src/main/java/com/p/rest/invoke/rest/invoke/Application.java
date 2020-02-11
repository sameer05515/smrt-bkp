package com.p.rest.invoke.rest.invoke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class Application {

	@Autowired
	RestTemplate restTemplate;

	private static int beg = 627;
//	private static int end = 0;
	private static int incr = 1;
	
	private static PrintStream customPrintStream=null;
	
	static {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		try {
			customPrintStream=new PrintStream(new File("C:\\split\\logs\\logs_"
					+ timeStamp
					+ ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Scheduled(cron = "${rest.invoke.cronExpression.jobRerunCron}")
	public void resetMaxSeqNoNextDay() throws Exception {
		customPrintStream.println("\n========================================");
		customPrintStream.println("Current date : " + new Date());
		String param = "";
		if (beg < arr.length - 1) {
//			end = beg + end + incr;
//			if (end >= arr.length - 1) {
//				end = arr.length - 1;
//			}
			customPrintStream.println("beg" + beg + "incr" + incr);
			String[][] subArr = Arrays.copyOfRange(arr, beg, beg+1);
			String resp=printEncode(subArr);
			System.out.println(resp);
			callForPan(resp);
			beg = beg+incr;
			
		}
//		customPrintStream.println("========================================\n");
		// callForPan(param);

	}
	
	public String printEncode(String[][] arr/*, int beg, int end*/) {

		String response=null;
		try  {

//			System.out.println("arr.length"+((arr!=null&arr.length>0)?arr.length:0));
			List<String> encStringList = new ArrayList<String>();
			for (String[] sarr : arr) {

				encStringList.add("{\"stud_id\":\"" + sarr[0] + "\" }");
			}
			//////////
			
			int cout=1;

			for (String str : encStringList) {

				String encStrs = encode(str);

				String decStrs = decode(encStrs);
				System.out.println(
						"\n$$$$$$$$ re-endoded\n===========\n" + encStrs + "\nre-decoded \n===========\n" + decStrs);

				customPrintStream.println("\n$$$$$$$$\n "
				 		+ cout++
				 		+ ". re-endoded\n===========\n" +decStrs+"\n\nparam="+encStrs);
				response=encStrs;
//				ps.println(encStrs+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace(customPrintStream);;
		}

		return response;
	}
	
	
	public String decode(String param) {
		return new String(Base64.getDecoder().decode(param));
	}

	public String encode(String param) {
		return new String(Base64.getEncoder().encode(param.getBytes()));
	}

	private void callForPan(String param) {
		String url = "https://127.0.0.1:8081/getState";
//		System.out.println("\n=====================================");
		System.out.println("Current date : " + new Date());
		// String
		// response=restTemplate.getForObject("http://127.0.0.1:8080/word-meaning-db-backup-service/words?name=aar",
		// String.class);

		// System.out.println("Response recieved : \n"+response);

		////

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("param", param);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response3 = restTemplate.postForEntity(url, request, String.class);
		// ResponseEntity<String> response3 = restTemplate.getForEntity( url, request ,
		// String.class );
		customPrintStream.println(response3.getBody());
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	private static String[][] arr = { { "12345", "555555", "05/01/1989", "juju.356@jem.com" } };

}
