import 'package:flutter/material.dart';
import 'package:increment_decrement/model/change_notifier.dart';
import 'package:increment_decrement/screen/ui.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => CounterModel(),
      child: const MyApp(),
    ),
  );
}
