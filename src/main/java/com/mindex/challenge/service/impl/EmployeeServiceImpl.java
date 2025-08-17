package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId); // Grab the employee object from the database using the findByEmployeeId method
        
        if (employee == null){
            throw new RuntimeException("Invalid employeeId: " + employeeId); // Logging in case an improper employee id is provided
        }

        int numberOfReports = calculateNumberOfReports(employee); //Use the helper method to calculate the number of reports for a given employee (single responsibility principle)
        return new ReportingStructure(employee, numberOfReports);
    }

    //Recursive helper method
    private int calculateNumberOfReports(Employee employee){
        int count = 0;
        List<Employee> directReports = employee.getDirectReports(); // Grab all of the directReports for a given user

        if(directReports != null){ // If the user has direct reports, loop through each one
            for(Employee report : directReports){

                // For each direct report, we need to get their full employee object from the database to see if they have direct reports of their own
                Employee person = employeeRepository.findByEmployeeId(report.getEmployeeId()); 

                count++;
                count += calculateNumberOfReports(person); // Recursively call this method to get the number of reports for each direct report
            }

        }

        return count;
    }
}
