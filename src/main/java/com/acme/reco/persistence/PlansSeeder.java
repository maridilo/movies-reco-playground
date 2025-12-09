package com.acme.reco.persistence;

import com.acme.reco.persistence.entity.BillingInterval;
import com.acme.reco.persistence.entity.PlanEntity;
import com.acme.reco.persistence.repo.PlanRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlansSeeder implements ApplicationRunner {

    private final PlanRepository planrepository;

    public PlansSeeder(PlanRepository planrepository) {
        this.planrepository = planrepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (planrepository.count() > 0) return;

        PlanEntity basic = new PlanEntity();
        basic.setCode("BASIC");
        basic.setName("Plan Básico");
        basic.setInterval(BillingInterval.MONTH);
        basic.setPriceCents(499);
        basic.setCurrency("EUR");
        basic.setActive(true);

        PlanEntity pro = new PlanEntity();
        pro.setCode("PRO");
        pro.setName("Plan Pro");
        pro.setInterval(BillingInterval.MONTH);
        pro.setPriceCents(999);
        pro.setCurrency("EUR");
        pro.setActive(true);

        planrepository.saveAll(List.of(basic, pro)); // id se genera solo
    }
}
