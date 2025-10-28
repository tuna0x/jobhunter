package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }



    @GetMapping("/email")
    @ApiMessage("send simple email")
    // @Scheduled(cron = "*/10 * * * * *")
    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        //this.emailService.sendEmailSync("tuanzkt271104@gmail.com", "test send email", "<h1><b> TUNA ne </b></h1>", false, true);
        // this.emailService.sendEmailFromTemplateSync("tuanzkt271104@gmail.com", "Test send email","job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }

}
