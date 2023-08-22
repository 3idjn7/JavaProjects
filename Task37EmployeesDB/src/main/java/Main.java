import java.util.List;

public class Main {
    public static void main(String[] args) {
        EmployeeDao employeeDao = new EmployeeDaoImplementation();

        // Test insert
        Employee newEmployee = new Employee(1, "John", "Doe", "05.12.1964", "HR", 50000.0);
        Employee newEmployee1 = new Employee(2, "Johnny", "Depp", "05.05.1964", "Peon", 5000.0);
        Employee newEmployee2 = new Employee(3, "Johntra", "Volta", "05.06.1954", "WorkWork", 5000.0);
        Employee newEmployee3 = new Employee(4, "Johnsey", "Thedoe", "05.07.1984", "OpaGolen", 3000.0);
        Employee newEmployee4 = new Employee(5, "John", "Doe", "05.06.1967", "WorkWork", 10000.0);
        employeeDao.insertEmployee(newEmployee);
        employeeDao.insertEmployee(newEmployee1);
        employeeDao.insertEmployee(newEmployee2);
        employeeDao.insertEmployee(newEmployee3);
        employeeDao.insertEmployee(newEmployee4);

        // Test update
        Employee employeeToUpdate = employeeDao.getEmployeeById(2);
        if (employeeToUpdate != null) {
            employeeToUpdate.setSalary(55000.0);
            employeeDao.updateEmployee(employeeToUpdate);
        }

        // Test get all employees
        List<Employee> employees = employeeDao.getAllEmployees();
        for (Employee employee : employees) {
            System.out.println(employee.getId() + " " + employee.getFirstName() + " " + employee.getLastName());
        }

        // Test delete
        int employeeIdToDelete = 1;
        employeeDao.deleteEmployee(employeeIdToDelete);
    }
}