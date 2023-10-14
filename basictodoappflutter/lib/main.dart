
import 'package:flutter/material.dart';
import 'ApiClient.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ToDo app',
      home: ToDoPage(),
    );
  }
}

class ToDoPage extends StatefulWidget {
  @override
  _ToDoPageState createState() => _ToDoPageState();
}

class _ToDoPageState extends State<ToDoPage> {
  List todos = [];
  final ApiClient apiClient = ApiClient('http://localhost:8080');

  @override
  void initState() {
    super.initState();
    _fetchToDos();
  }

  _fetchToDos() async {
    try {
      todos = await apiClient.getAllToDos();
      setState(() {});
    } catch (e) {
      print("Failed to fetch ToDos: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("To-Do App"),
      ),
      body: ListView.builder(
        itemCount: todos.length,
        itemBuilder: (context, index) {
          Map todo = todos[index];

          return Dismissible(
            key: Key(todo['id'].toString()),
            direction: DismissDirection.endToStart,
            onDismissed: (direction) async {
              if (direction == DismissDirection.endToStart) {
                await apiClient.deleteToDo(todo['id']);
                _fetchToDos(); // Refresh the to-dos after deleting
              }
            },
            background: Container(
              color: Colors.red,
              child: Align(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Icon(Icons.delete, color: Colors.white),
                    Text(' Delete', style: TextStyle(color: Colors.white))
                  ],
                ),
                alignment: Alignment.centerRight,
              ),
            ),
            child: CheckboxListTile(
              title: GestureDetector(
                onLongPress: () async {
                  TextEditingController editController =
                  TextEditingController(text: todo['title']);
                  await showDialog(
                      context: context,
                      builder: (BuildContext context) {
                        return AlertDialog(
                          title: Text('Edit ToDo'),
                          content: TextField(
                            controller: editController,
                            decoration:
                            InputDecoration(hintText: "Edit ToDo Title"),
                          ),
                          actions: [
                            TextButton(
                              onPressed: () async {
                                if (editController.text.isNotEmpty) {
                                  await apiClient.updateToDo({
                                    'title': editController.text,
                                    'isCompleted': todo['isCompleted']
                                  }, todo['id']);
                                  Navigator.of(context).pop();
                                  _fetchToDos(); //Refresh the to-dos
                                }
                              },
                              child: Text('Update'),
                            ),
                            TextButton(
                              onPressed: () {
                                Navigator.of(context).pop();
                              },
                              child: Text('Cancel'),
                            ),
                          ],
                        );
                      });
                },
                child: Text(todo['title']),
              ),
              value: todo['isCompleted'] ?? false,
              onChanged: (bool? value) async {
                if (value != null) {
                  setState(() {
                    todo['isCompleted'] = value;
                  });

                  try {
                    await apiClient.updateToDo({
                      'title': todo['title'],
                      'isCompleted': value
                    }, todo['id']);
                  } catch (e) {
                    setState(() {
                      todo['isCompleted'] = !value;
                    });
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text("Failed to update ToDo")),
                    );
                  }
                }
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
      onPressed: () async {
        TextEditingController controller = TextEditingController();
        await showDialog(
          context: context,
          builder: (BuildContext context) {
            return AlertDialog(
              title: Text('New ToDo'),
              content: TextField(
                controller: controller,
                decoration: InputDecoration(hintText: "ToDo Title"),
              ),
              actions: [
                TextButton(
                  onPressed: () async {
                    if (controller.text.isNotEmpty) {
                      await apiClient.createToDo({
                        'title': controller.text,
                        'isCompleted': false
                      });
                      Navigator.of(context).pop();
                      _fetchToDos(); //Refresh the to-dos
                    }
                  },
                  child: Text('Add'),
                ),
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                  },
                  child: Text('Cancel'),
                ),
              ],
            );
          }
        );
      },
      child: Icon(Icons.add),
      ),
    );
  }
}
