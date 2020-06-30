package com.tracker.application.controllers;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tracker.application.models.LocationStats;
import com.tracker.application.services.CoronaVirusTrackerService;

@Controller
public class TrackerController {
	
	@Autowired
	CoronaVirusTrackerService coronaVirusTrackerService;
	
	@GetMapping("/")
	public String track(Model model)
	{
		List<LocationStats> allStats = coronaVirusTrackerService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        return "home";
	}
}
