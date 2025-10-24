package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;


    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public boolean checkEmailExist(String name){
        return this.subscriberRepository.findByEmail(name);
    }


    public Subscriber create(Subscriber subscriber){

        Subscriber curSubscriber  = new Subscriber();
        curSubscriber.setId(subscriber.getId());
        curSubscriber.setEmail(subscriber.getEmail());
        curSubscriber.setName(subscriber.getName());
        curSubscriber.setCreatedAt(subscriber.getCreatedAt());
        curSubscriber.setUpdatedAt(subscriber.getUpdatedAt());
        curSubscriber.setCreatedBy(subscriber.getCreatedBy());
        curSubscriber.setUpdatedBy(subscriber.getUpdatedBy());
         // check skill;
        if (subscriber.getSkills()!=null) {
            List<Long> list=subscriber.getSkills().stream().map(x->x.getId()).collect(Collectors.toList());
            List<Skill> skills=this.skillRepository.findByIdIn(list);
            curSubscriber.setSkills(skills);
        }
        subscriber=this.subscriberRepository.save(curSubscriber);
        return subscriber;
    }

}
