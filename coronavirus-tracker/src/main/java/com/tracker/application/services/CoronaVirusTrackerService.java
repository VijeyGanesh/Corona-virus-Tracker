package com.tracker.application.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tracker.application.models.LocationStats;

@Service
public class CoronaVirusTrackerService {
	
	@Autowired
	Environment environment;
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchGlobalData() throws IOException, InterruptedException {
		List<LocationStats> localStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(environment.getProperty("virusdata.url"))).build();
		HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		//System.out.println(response.body());
		StringReader csvBodyReader = new StringReader(response.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for(CSVRecord record:records) {
			LocationStats locationStat = new LocationStats();
			locationStat.setState(record.get("Province/State"));
			locationStat.setCountry(record.get("Country/Region"));
			int latestCases = Integer.parseInt(record.get(record.size()-1));
			int prevCases = Integer.parseInt(record.get(record.size()-2));
			locationStat.setLatestTotalCases(latestCases);
			locationStat.setDiffFromPrevDay(latestCases - prevCases);
			localStats.add(locationStat);
		}
		this.allStats=localStats;
	}

	public List<LocationStats> getAllStats() {
		return allStats;
	}

}
