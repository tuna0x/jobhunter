package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create a subscriber")
    public ResponseEntity<Subscriber> createNewSubscriber(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException{
        boolean isExists=this.subscriberService.checkEmailExist(subscriber.getEmail());
        if (isExists== true) {
            throw new IdInvalidException("Email is exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("update a subscriber")
    public ResponseEntity<Subscriber> update( @RequestBody Subscriber subscriber) throws IdInvalidException {
        Subscriber sbDB=this.subscriberService.findById(subscriber.getId());
        if (sbDB == null) {
            throw new IdInvalidException("id ko ton tai");
        }
        return ResponseEntity.ok().body(this.subscriberService.update(sbDB,subscriber));
    }

    @DeleteMapping("/subscribers/{id}")
    @ApiMessage("delete a subscriber")
    public ResponseEntity<Void> delete(@PathVariable ("id") Subscriber subscriber) throws IdInvalidException{
        Subscriber cur =this.subscriberService.findById(subscriber.getId());
        if (cur== null) {
            throw new IdInvalidException("id ko ton tai");
        }
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/subscribers/skills")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email= SecurityUtil.getCurrentUserLogin().isPresent() ==true ? SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
