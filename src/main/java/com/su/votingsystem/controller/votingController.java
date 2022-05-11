package com.su.votingsystem.controller;

import com.su.votingsystem.entity.Candidate;
import com.su.votingsystem.entity.Citizen;
import com.su.votingsystem.repository.CandidateRepository;
import com.su.votingsystem.repository.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class votingController {

    public final Logger logger = Logger.getLogger(String.valueOf(votingController.class));
    @Autowired
    CitizenRepository citizenRepo;
    @Autowired
    CandidateRepository candidateRepo;

    @RequestMapping("/")
    public String goToVote() {
        logger.info("Return login.html file");
        return "login.html";
    }

    @RequestMapping("/doLogin")
    public String doLogin(@RequestParam String name, Model model, HttpSession session) {
        logger.info("getting citizen from database");
        Citizen citizen = citizenRepo.findByName(name);
        logger.info("putting citizen into session");
        session.setAttribute("citizen", citizen);

        if (!citizen.getHasVoted()) {
            logger.info("putting candidates into model");
            List<Candidate> candidates = candidateRepo.findAll();
            model.addAttribute("candidates", candidates);
            return "/performVote.html";
        } else {
            return "/alreadyVoted.html";
        }
    }

    @RequestMapping("/voteFor")
    public String voteFor(@RequestParam long id, HttpSession session) {
        Citizen citizen = (Citizen) session.getAttribute("citizen");

        if (!citizen.getHasVoted()) {
            citizen.setHasVoted(true);
            citizenRepo.save(citizen);
            Candidate c = candidateRepo.findById(id);
            logger.info("voting for candidate - " + c.getName());
            c.setNumberOfVotes(c.getNumberOfVotes() + 1);
            candidateRepo.save(c);
            return "voted.html";
        }
        return "alreadyVoted.html";
    }
}
