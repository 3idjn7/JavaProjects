import 'package:flutter/material.dart';


class AddToDoDialog extends StatefulWidget {
  final Function(String) onAdd;

  const AddToDoDialog({
    Key? key,
    required this.onAdd,
  }) : super(key: key);

  @override
  _AddToDoDialogState createState() => _AddToDoDialogState();
}

class _AddToDoDialogState extends State<AddToDoDialog> {
  final TextEditingController controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('New ToDo'),
      content: TextField(
        controller: controller,
        decoration: const InputDecoration(hintText: "ToDo Title"),
      ),
      actions: [
        TextButton(
          onPressed: () {
            if (controller.text.isNotEmpty) {
              widget.onAdd(controller.text);
              Navigator.of(context).pop();
            }
          },
          child: const Text('Add'),
        ),
        TextButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: const Text('Cancel'),
        ),
      ],
    );
  }
}
