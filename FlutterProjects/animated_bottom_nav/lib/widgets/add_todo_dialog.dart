import 'package:flutter/material.dart';

class AddToDoDialog extends StatefulWidget {
  final Function(String) onAdd;

  const AddToDoDialog({
    Key? key,
    required this.onAdd,
  }) : super(key: key);

  @override
  State<AddToDoDialog> createState() => _AddToDoDialogState();
}

class _AddToDoDialogState extends State<AddToDoDialog> {
  final TextEditingController controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: _buildDialogTitle(),
      content: _buildTextField(),
      actions: _buildDialogActions(),
    );
  }

  Text _buildDialogTitle() {
    return const Text('New ToDo');
  }

  TextField _buildTextField() {
    return TextField(
      controller: controller,
      decoration: const InputDecoration(hintText: "ToDo Title"),
    );
  }

  List<Widget> _buildDialogActions() {
    return [
      _buildAddButton(),
      _buildCancelButton(),
    ];
  }

  TextButton _buildAddButton() {
    return TextButton(
      onPressed: _handleAddPressed,
      child: const Text('Add'),
    );
  }

  TextButton _buildCancelButton() {
    return TextButton(
      onPressed: () {
        Navigator.of(context).pop();
      },
      child: const Text('Cancel'),
    );
  }

  void _handleAddPressed() {
    if (controller.text.isNotEmpty) {
      widget.onAdd(controller.text);
      Navigator.of(context).pop();
    }
  }
}