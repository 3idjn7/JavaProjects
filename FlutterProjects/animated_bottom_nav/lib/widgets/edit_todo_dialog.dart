import 'package:flutter/material.dart';

class EditToDoDialog extends StatefulWidget {
  final Function(String) onEdit;
  final String initialText;

  const EditToDoDialog({
    Key? key,
    required this.onEdit,
    required this.initialText,
  }) : super(key: key);

  @override
  State<EditToDoDialog> createState() => _EditToDoDialogState();
}

class _EditToDoDialogState extends State<EditToDoDialog> {
  final TextEditingController controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    controller.text = widget.initialText;
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Edit ToDo'),
      content: TextField(
        controller: controller,
        decoration: const InputDecoration(hintText: "ToDo Title"),
      ),
      actions: [
        TextButton(
          onPressed: () {
            if (controller.text.isNotEmpty) {
              widget.onEdit(controller.text);
              Navigator.of(context).pop();
            }
          },
          child: const Text('Save'),
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
