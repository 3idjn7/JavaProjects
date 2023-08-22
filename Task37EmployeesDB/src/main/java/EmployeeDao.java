import java.util.List;

public interface EmployeeDao {
    void insertEmployee(Employee e);
    void updateEmployee(Employee e);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id);
    void deleteEmployee(int id);
}
