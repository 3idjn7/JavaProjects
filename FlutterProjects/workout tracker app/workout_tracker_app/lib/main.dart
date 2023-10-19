import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:workout_tracker_app/data/workout_data.dart';
import 'package:workout_tracker_app/pages/home_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => WorkoutData(),
      child: const MaterialApp(
        home:HomePage(),
      ),
    );
  }
}
