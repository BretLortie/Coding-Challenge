package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeService employeeService;

    private Employee manager;
    private Employee report1;
    private Employee report2;
    private Employee report3;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";

        // Employee at the bottom of the hierarchy with no direct reports
        report1 = new Employee();
        report1.setFirstName("John");
        report1.setLastName("One");
        report1.setDepartment("Engineering");
        report1.setPosition("Developer");
        report1 = employeeService.create(report1);

        // Report with one direct report
        report2 = new Employee();
        report2.setFirstName("Joe");
        report2.setLastName("Two");
        report2.setDepartment("Engineering");
        report2.setPosition("Developer");
        report2.setDirectReports(Arrays.asList(report1));
        report2 = employeeService.create(report2);

        // Higher level employee with no direct reports
        report3 = new Employee();
        report3.setFirstName("Jilly");
        report3.setLastName("Three");
        report3.setDepartment("Engineering");
        report3.setPosition("Developer");
        report3 = employeeService.create(report3);

        // Manager with multiple reports
        manager = new Employee();
        manager.setFirstName("Manager");
        manager.setLastName("Person");
        manager.setDepartment("Engineering");
        manager.setPosition("Manager");
        manager.setDirectReports(Arrays.asList(report2, report3));
        manager = employeeService.create(manager);
    }


    /*
     * Tests the reporting structure for an employee with only one report.
     */
    @Test
    public void testReportingStructureWithOneReport() {
        ReportingStructure rs = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, report2.getEmployeeId()).getBody();

        assertNotNull(rs);
        assertEquals(report2.getEmployeeId(), rs.getEmployee().getEmployeeId());
        assertEquals(1, rs.getNumberOfReports()); 
    }

    /*
     * Tests the reporting structure for a nested employee hierarchy.
     */
    @Test
    public void testReportingStructureWithNestedReports() {
        ReportingStructure rs = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, manager.getEmployeeId()).getBody();

        assertNotNull(rs);
        assertEquals(manager.getEmployeeId(), rs.getEmployee().getEmployeeId());
        assertEquals(3, rs.getNumberOfReports()); 
    }

    /*
     * Tests the reporting structure for an employee with no direct reports.
     */
    @Test
    public void testReportingStructureNoReports() {
        ReportingStructure rs = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, report1.getEmployeeId()).getBody();

        assertNotNull(rs);
        assertEquals(report1.getEmployeeId(), rs.getEmployee().getEmployeeId());
        assertEquals(0, rs.getNumberOfReports());
    }
}
