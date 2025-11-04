package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.email.ResEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;




    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }


    public boolean checkEmailExist(String name){
        return this.subscriberRepository.existsByEmail(name);
    }


    public Subscriber create(Subscriber subscriber){
         // check skill;
        if (subscriber.getSkills()!=null) {
            List<Long> list=subscriber.getSkills().stream().map(x->x.getId()).collect(Collectors.toList());

            List<Skill> skills=this.skillRepository.findByIdIn(list);
            subscriber.setSkills(skills);
        }
        subscriber=this.subscriberRepository.save(subscriber);
        return subscriber;
    }

    public Subscriber findById(Long id) {
       Optional<Subscriber> sOptional=this.subscriberRepository.findById(id);
       return sOptional.isPresent() ? sOptional.get() : null;
    }
    public Subscriber update(Subscriber subsDB, Subscriber subRequest){
        if (subRequest.getSkills() !=null) {
            List<Long> reqSkill=subRequest.getSkills().stream().map(x->x.getId()).collect(Collectors.toList());

            List<Skill> skills =this.skillRepository.findByIdIn(reqSkill);
            subsDB.setSkills(skills);
        }
        return this.subscriberRepository.save(subsDB);
    }

    public void delete(Long id){
         this.skillRepository.deleteById(id);
    }

     public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                        job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                listJobs);
                    }
                }
            }
        }
    }
     public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }


     public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
     }

    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron(){
    //     System.out.println("Test Cron");
    // }

}
