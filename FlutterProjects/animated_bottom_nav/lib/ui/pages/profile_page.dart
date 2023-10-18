import 'package:flutter/material.dart';

class ProfilePage extends StatelessWidget {
  const ProfilePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(),
      body: _buildBodyContent(),
      backgroundColor: Colors.white,
    );
  }

  AppBar _buildAppBar() {
    return AppBar(
      title: const Center(child: Text("Profile screen")),
      backgroundColor: Colors.blue[100],
    );
  }

  Widget _buildBodyContent() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          _buildProfileImage(),
          const SizedBox(height: 20),
          _buildProfileName(),
          _buildProfileEmail(),
        ],
      ),
    );
  }

  CircleAvatar _buildProfileImage() {
    return const CircleAvatar(
      radius: 50,
      backgroundColor: Color.fromRGBO(187, 222, 251, 1),
      backgroundImage: AssetImage('assets/Troll-icon.png'),
    );
  }

  Text _buildProfileName() {
    return const Text(
      'John Doe',
      style: TextStyle(fontSize: 25, fontWeight: FontWeight.bold),
    );
  }

  Text _buildProfileEmail() {
    return const Text(
      'johndoe@example.com',
      style: TextStyle(fontSize: 18),
    );
  }
}
