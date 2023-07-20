package org.apache.camel.springboot.example.avro;

import com.github.javafaker.Faker;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CreateEmployeeProcessor implements Processor {
    private final Faker faker = new Faker();

    @Override
    public void process(Exchange exchange) throws Exception {
        final String firstName = faker.name().firstName();
        final String lastName = faker.name().lastName();
        final Date hireDate = faker.date().past(365, TimeUnit.DAYS);

        Map<String, String> personal = new HashMap<>();
        personal.put("address", faker.address().streetAddress());
        personal.put("phone", faker.phoneNumber().cellPhone());

        exchange.getIn().setBody(new Employee(firstName, lastName, hireDate.getTime(), personal));
    }
}
