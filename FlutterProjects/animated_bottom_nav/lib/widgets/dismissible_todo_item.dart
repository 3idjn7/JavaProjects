import 'package:flutter/material.dart';

import 'edit_todo_dialog.dart';

class DismissibleTodoItem extends StatelessWidget {
  final Map todo;
  final Function(Map) onDismissed;
  final Function(Map, bool?) onCheckboxChanged;

  const DismissibleTodoItem({
    Key? key,
    required this.todo,
    required this.onDismissed,
    required this.onCheckboxChanged,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Dismissible(
      key: Key(todo['id'].toString()),
      direction: DismissDirection.endToStart,
      onDismissed: (_) => onDismissed(todo),
      background: _buildDismissibleBackground(),
      child: _buildTodoListTile(context),
    );
  }

  Container _buildDismissibleBackground() {
    return Container(
      color: Colors.red,
      child: const Align(
        alignment: Alignment.centerRight,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            Icon(Icons.delete, color: Colors.white),
            Text(' Delete', style: TextStyle(color: Colors.white))
          ],
        ),
      ),
    );
  }

  ListTile _buildTodoListTile(BuildContext context) {
    return ListTile(
      title: InkWell(
        onLongPress: () => _handleLongPress(context),
        child: Text(todo['title']),
      ),
      trailing: Checkbox(
        value: todo['isCompleted'] ?? false,
        onChanged: (bool? value) {
          onCheckboxChanged(todo, value);
        },
      ),
    );
  }

  void _handleLongPress(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return EditToDoDialog(
          initialText: todo['title'],
          onEdit: (String newTitle) {
            onCheckboxChanged({
              'id': todo['id'],
              'title': newTitle,
              'isCompleted': todo['isCompleted']
            }, null);
          },
        );
      },
    );
  }
}
