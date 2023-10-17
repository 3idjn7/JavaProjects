import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  _SearchPageState createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  final TextEditingController _searchController = TextEditingController();

  _searchOnGoogle() async {
    final query = _searchController.text;
    final url = 'https://www.google.com/search?q=$query';

    if (await canLaunchUrl(url as Uri)) {
      await launchUrl(url as Uri);
    } else {
      throw 'Could not launch $url';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16.0),
          child: TextField(
            controller: _searchController,
            onSubmitted: (value) => _searchOnGoogle(),
            decoration: InputDecoration(
              labelText: 'Google Search',
              border: OutlineInputBorder(),
              suffixIcon: IconButton(
                icon: Icon(Icons.search),
                onPressed: _searchOnGoogle,
              ),
            ),
          ),
        ),
        // Other UI elements go here, like local search functionality
        // ...
      ],
    );
  }
}
