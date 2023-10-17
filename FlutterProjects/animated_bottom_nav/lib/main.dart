import 'package:animated_bottom_nav/pages/home_page.dart';
import 'package:animated_bottom_nav/pages/search_page.dart';
import 'package:flutter/material.dart';
import 'ui/animated_bottom_nav.dart';

import 'pages/profile_page.dart';

void main() => runApp(const MyApp());

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  // ignore: library_private_types_in_public_api
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _currentIndex = 0;

  final _pages = [
    const HomePage(),
    SearchPage(),
    const ProfilePage(),
  ];

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: _pages[_currentIndex],
        bottomNavigationBar: AnimatedBottomNav(
          currentIndex: _currentIndex,
          onTap: (index) {
            setState(() {
              _currentIndex = index;
            });
          },
          items: [
            BottomNavItem(icon: Icons.home),
            BottomNavItem(icon: Icons.search),
            BottomNavItem(icon: Icons.person),
          ],
        ),
      )
    );
  }
}