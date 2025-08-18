package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeService employeeService;

    private Employee employee;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{employeeId}";

        // Create an employee to associate compensation with
        employee = new Employee();
        employee.setFirstName("Test");
        employee.setLastName("Employee");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");
        employee = employeeService.create(employee);
    }

    @Test
    public void testCreateAndReadCompensation() {
        Compensation compensation = new Compensation(employee.getEmployeeId(), 120000.0, LocalDate.now());

        // Create compensation
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();

        assertNotNull(createdCompensation);
        assertEquals(employee.getEmployeeId(), createdCompensation.getEmployeeId());
        assertEquals(120000.0, createdCompensation.getSalary(), 0.001);

        // Read compensation
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, employee.getEmployeeId()).getBody();

        assertNotNull(readCompensation);
        assertEquals(createdCompensation.getEmployeeId(), readCompensation.getEmployeeId());
        assertEquals(createdCompensation.getSalary(), readCompensation.getSalary(), 0.001);
    }
}
