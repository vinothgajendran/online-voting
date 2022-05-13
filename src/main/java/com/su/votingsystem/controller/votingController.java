package com.su.votingsystem.controller;

import com.su.votingsystem.model.Candidate;
import com.su.votingsystem.model.User;
import com.su.votingsystem.repository.CandidateRepository;
import com.su.votingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    CandidateRepository candidateRepo;
    @Autowired
    UserRepository userRepository;


    @RequestMapping("/")
    public String welcome(){
        return "welcomePage.html";
    }

    @RequestMapping("/user")
    public String goToVote() {
        logger.info("Return login.html file");
        return "login.html";
    }

    @RequestMapping("/id")
    public String doLogin(@RequestParam Long id, Model model, HttpSession session) {
        logger.info("getting citizen from database");
       // Citizen citizen = citizenRepo.findById(id);
        User user = userRepository.findById(id);
        logger.info("putting citizen into session");
        session.setAttribute("user", user);

        if (!user.getVote_status()) {
            logger.info("putting candidates into model");
            List<Candidate> candidates = candidateRepo.findAll();
            model.addAttribute("candidates", candidates);
            return "/performVote.html";
        } else {
            return "/alreadyVoted.html";
        }
    }

    @RequestMapping("/voteFor")
    public String voteFor(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (!user.getVote_status()) {
            user.setVote_status(true);
            userRepository.save(user);
            Candidate c = candidateRepo.findById(id);
            logger.info("voting for candidate - " + c.getName());
            c.setNumberOfVotes(c.getNumberOfVotes() + 1);
            candidateRepo.save(c);
            return "voted.html";
        }
        return "alreadyVoted.html";
    }

    @RequestMapping("/resultPage")
    public String votingResult(Model model, HttpSession session){
        logger.info("Return Results page");
        List<Candidate> c = candidateRepo.findAll(Sort.by(Sort.Direction.DESC,"numberOfVotes"));
        model.addAttribute("candidates", c);
        return "resultPage.html";
    }
}
