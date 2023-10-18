import 'package:animated_bottom_nav/ui/pages/to_do_page.dart';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late String _timeString;
  late Timer _timer;

  @override
  void initState() {
    _timeString = _formatDateTime(DateTime.now());
    _timer =
        Timer.periodic(const Duration(seconds: 1), (Timer t) => _getTime());
    super.initState();
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  String _formatDateTime(DateTime dateTime) {
    return DateFormat('hh:mm:ss').format(dateTime);
  }

  void _getTime() {
    final DateTime now = DateTime.now();
    final String formattedDateTime = _formatDateTime(now);
    setState(() {
      _timeString = formattedDateTime;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: _buildBody(),
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      title: const Center(child: Text("Home screen")),
      backgroundColor: Colors.blue[100],
    );
  }

  Widget _buildBody() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          _buildTimeDisplay(),
          const SizedBox(height: 10),
          _buildCurrentDateDisplay(),
          const SizedBox(height: 20),
          _buildToDoButton(),
          const SizedBox(height: 10),
          const Text('ToDo App'),
        ],
      ),
    );
  }

  Widget _buildTimeDisplay() {
    return Container(
      padding: const EdgeInsets.all(15.0),
      decoration: BoxDecoration(
        color: Colors.grey.withOpacity(0.3),
        shape: BoxShape.rectangle,
        borderRadius: BorderRadius.circular(15.0),
      ),
      child: Text(
        _timeString,
        style: const TextStyle(fontSize: 50, fontWeight: FontWeight.bold),
      ),
    );
  }

  Widget _buildCurrentDateDisplay() {
    DateTime now = DateTime.now();
    String formattedDate = DateFormat('EEEE, MMM, d').format(now);

    return Text(formattedDate, style: const TextStyle(fontSize: 20));
  }

  Widget _buildToDoButton() {
    return InkWell(
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const ToDoPage()),
        );
      },
      child: Container(
        width: 80,
        height: 80,
        decoration: const BoxDecoration(
          color: Colors.grey,
          shape: BoxShape.circle,
        ),
        child: const Center(
          child: Icon(Icons.check_box, size: 40),
        ),
      ),
    );
  }
}
