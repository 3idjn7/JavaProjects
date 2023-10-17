import 'package:flutter/material.dart';

class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          CircleAvatar(
            radius: 50,
            backgroundImage: AssetImage(
                'assets/Troll-icon.png'),
          ),
          SizedBox(height: 20),
          Text(
            'John Doe',
            style: TextStyle(fontSize: 25, fontWeight: FontWeight.bold),
          ),
          Text('johndoe@example.com', style: TextStyle(fontSize: 18)),
        ],
      ),
    );
  }
}
