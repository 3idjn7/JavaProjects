import java.sql.*;
import java.util.List;
import java.util.ArrayList;

class EmployeeDaoImplementation implements EmployeeDao {
    private static final String URL = "jdbc:mysql://localhost:3306/Employeees";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "databasepassword";

    @Override
    public void insertEmployee(Employee e) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO employee (id, first_name, last_name, birth_date, department, salary) VALUES (?, ?, ?, ?, ?, ?)"
             )) {
            preparedStatement.setInt(1, e.getId());
            preparedStatement.setString(2, e.getFirstName());
            preparedStatement.setString(3, e.getLastName());
            preparedStatement.setString(4, e.getBirthDate());
            preparedStatement.setString(5, e.getDepartment());
            preparedStatement.setDouble(6, e.getSalary());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateEmployee(Employee e) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE employee SET first_name = ?, last_name = ?, birth_date = ?, department = ?, salary = ? WHERE id = ?"
             )) {
            preparedStatement.setString(1, e.getFirstName());
            preparedStatement.setString(2, e.getLastName());
            preparedStatement.setString(3, e.getBirthDate());
            preparedStatement.setString(4, e.getDepartment());
            preparedStatement.setDouble(5, e.getSalary());
            preparedStatement.setInt(6, e.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM employee")) {

            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("birth_date"),
                        resultSet.getString("department"),
                        resultSet.getDouble("salary")
                );
                employees.add(employee);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employees;
    }

    @Override
    public Employee getEmployeeById(int id) {
        Employee employee = null;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employee WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employee = new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("birth_date"),
                        resultSet.getString("department"),
                        resultSet.getDouble("salary")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employee;
    }

    @Override
    public void deleteEmployee(int id) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM employee WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}