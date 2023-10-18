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
      title: _buildDialogTitle(),
      content: _buildDialogContent(),
      actions: _buildDialogActions(context),
    );
  }

  Text _buildDialogTitle() {
    return const Text('Edit ToDo');
  }

  TextField _buildDialogContent() {
    return TextField(
      controller: controller,
      decoration: const InputDecoration(hintText: "ToDo Title"),
    );
  }

  List<Widget> _buildDialogActions(BuildContext context) {
    return [
      _buildSaveButton(context),
      _buildCancelButton(context),
    ];
  }

  TextButton _buildSaveButton(BuildContext context) {
    return TextButton(
      onPressed: () => _handleSave(context),
      child: const Text('Save'),
    );
  }

  TextButton _buildCancelButton(BuildContext context) {
    return TextButton(
      onPressed: () {
        Navigator.of(context).pop();
      },
      child: const Text('Cancel'),
    );
  }

  void _handleSave(BuildContext context) {
    if (controller.text.isNotEmpty) {
      widget.onEdit(controller.text);
      Navigator.of(context).pop();
    }
  }
}