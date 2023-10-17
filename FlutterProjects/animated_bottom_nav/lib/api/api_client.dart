import 'dart:convert';
import 'package:http/http.dart' as http;

import '../logger/logger.dart';

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
    logger.i('Attempting to create ToDo: $todo');
    final response = await http.post(
      Uri.parse('$baseUrl/api/todos'),
      headers: {"Content-Type": "application/json"},
      body: json.encode(todo),
    );

    logger.i('Response status: ${response.statusCode}');
    logger.i('Response body: ${response.body}');

    if (response.statusCode == 200 || response.statusCode == 201) { // 201 is for created
      return json.decode(response.body);
    } else {
      throw Exception('Failed to create ToDo');
    }
  }


  Future<Map> updateToDo(Map todo, int id) async {
    logger.i('Attempting to update ToDo: $todo');
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
    logger.i('Attempting to delete ToDo: $id');
    final response = await http.delete(
      Uri.parse('$baseUrl/api/todos/$id'),
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to delete ToDo');
    }
  }
}