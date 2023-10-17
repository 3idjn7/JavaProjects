import 'package:flutter/material.dart';
import '../api/api_client.dart';
import '../logger/logger.dart';
import '../widgets/add_todo_dialog.dart';
import '../widgets/dismissible_todo_item.dart';

class ToDoPage extends StatefulWidget {
  const ToDoPage({super.key});

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

  void _handleCheckboxChanged(Map todo, bool? newValue) async {
    if (newValue == null) return;

    try {
      final updatedTodo = await apiClient.updateToDo(
        {
          'id': todo['id'],
          'title': todo['title'],
          'isCompleted': newValue
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
      appBar: AppBar(
        title: const Text("To-Do App"),
      ),
      body: ListView.builder(
        itemCount: todos.length,
        itemBuilder: (context, index) {
          Map todo = todos[index];

          return DismissibleTodoItem(
            todo: todo,
            onDismissed: (Map dismissedTodo) async {
              try {
                await apiClient.deleteToDo(dismissedTodo['id']);
                setState(() {
                  todos.removeWhere((t) => t['id'] == dismissedTodo['id']);
                });
              } catch (e) {
                logger.e("Failed to delete ToDo: $e");
                // Consider adding user feedback about failure
              }
            },
            onCheckboxChanged: _handleCheckboxChanged,
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          showDialog(
            context: context,
            builder: (BuildContext context) {
              return AddToDoDialog(
                onAdd: (String title) async {
                  try {
                    final newTodo = await apiClient.createToDo({
                      'title': title,
                      'isCompleted': false
                    });
                    setState(() {
                      todos.add(newTodo);
                    });
                  } catch (e) {
                    logger.e("Failed to add ToDo: $e");
                  }
                },
              );
            },
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
