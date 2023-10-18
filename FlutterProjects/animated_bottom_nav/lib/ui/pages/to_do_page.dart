import 'package:flutter/material.dart';
import '../../api/api_client.dart';
import '../../logger/logger.dart';
import '../../widgets/add_todo_dialog.dart';
import '../../widgets/dismissible_todo_item.dart';

class ToDoPage extends StatefulWidget {
  const ToDoPage({Key? key}) : super(key: key);

  @override
  State<ToDoPage> createState() => _ToDoPageState();
}

class _ToDoPageState extends State<ToDoPage> {
  List todos = [];
  final ApiClient apiClient = ApiClient('http://localhost:8080');

  @override
  void initState() {
    super.initState();
    _fetchToDos();
  }

  Future<void> _fetchToDos() async {
    try {
      todos = await apiClient.getAllToDos();
      setState(() {});
    } catch (e) {
      logger.e("Failed to fetch ToDos: $e");
    }
  }

  Future<void> _addTodo(String title) async {
    try {
      final newTodo = await apiClient.createToDo({
        'title': title,
        'isCompleted': false,
      });
      setState(() {
        todos.add(newTodo);
      });
    } catch (e) {
      logger.e("Failed to add ToDo: $e");
    }
  }

  Future<void> _deleteTodo(Map dismissedTodo) async {
    try {
      await apiClient.deleteToDo(dismissedTodo['id']);
      setState(() {
        todos.removeWhere((t) => t['id'] == dismissedTodo['id']);
      });
    } catch (e) {
      logger.e("Failed to delete ToDo: $e");
    }
  }

  void _handleCheckboxChanged(Map todo, bool? newValue) async {
    if (newValue == null) return;

    try {
      final updatedTodo = await apiClient.updateToDo(
        {
          'id': todo['id'],
          'title': todo['title'],
          'isCompleted': newValue,
        },
        todo['id'],
      );
      setState(() {
        int index = todos.indexWhere((t) => t['id'] == todo['id']);
        todos[index] = updatedTodo;
      });
    } catch (e) {
      logger.e("Failed to update ToDo: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: _buildTodoList(),
      floatingActionButton: _buildAddTodoButton(),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      title: const Text("To-Do App"),
    );
  }

  Widget _buildTodoList() {
    return ListView.builder(
      itemCount: todos.length,
      itemBuilder: (context, index) {
        Map todo = todos[index];
        return DismissibleTodoItem(
          todo: todo,
          onDismissed: _deleteTodo,
          onCheckboxChanged: _handleCheckboxChanged,
        );
      },
    );
  }

  FloatingActionButton _buildAddTodoButton() {
    return FloatingActionButton(
      onPressed: () {
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return AddToDoDialog(
              onAdd: _addTodo,
            );
          },
        );
      },
      child: const Icon(Icons.add),
    );
  }
}
