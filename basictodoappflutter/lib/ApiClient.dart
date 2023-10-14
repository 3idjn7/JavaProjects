import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiClient {
  final String baseUrl;

  ApiClient(this.baseUrl);

  Future<List> getAllToDos() async {
    final response = await http.get(Uri.parse('$baseUrl/api/todos'));
    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to load ToDos');
    }
  }

  Future<Map> createToDo(Map todo) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/todos'),
      headers: {"Content-Type": "application/json"},
      body: json.encode(todo),
    );
    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to create ToDo');
    }
  }

  Future<Map> updateToDo(Map todo, int id) async {
    final response = await http.put(
      Uri.parse('$baseUrl/api/todos/$id'),
      headers: {"Content-Type": "application/json"},
      body: json.encode(todo),
    );
    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to update ToDo');
    }
  }

  Future<void> deleteToDo(int id) async {
    final response = await http.delete(
      Uri.parse('$baseUrl/api/todos/$id'),
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to delete ToDo');
    }
  }
}