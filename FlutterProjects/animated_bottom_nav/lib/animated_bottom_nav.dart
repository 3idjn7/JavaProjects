import 'package:flutter/material.dart';

class AnimatedBottomNav extends StatefulWidget {
  final int currentIndex;
  final List<BottomNavItem> items;
  final Function(int) onTap;

  AnimatedBottomNav({
    required this.currentIndex,
    required this.items,
    required this.onTap,
  });

  @override
  _AnimatedBottomNavState createState() => _AnimatedBottomNavState();
}

class _AnimatedBottomNavState extends State<AnimatedBottomNav> {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 60.0,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: widget.items.map((item) {
          int index = widget.items.indexOf(item);
          bool isSelected = widget.currentIndex == index;

          return GestureDetector(
            onTap: () => widget.onTap(index),
            child: AnimatedContainer(
              duration: Duration(milliseconds: 300),
              padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
              decoration: isSelected
                  ? BoxDecoration(
                      color: item.activeColor,
                      borderRadius: BorderRadius.circular(30.0),
                    )
                  : null,
              child: Icon(
                item.icon,
                color: isSelected ? Colors.white : item.inactiveColor,
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

class BottomNavItem {
  final IconData icon;
  final Color activeColor;
  final Color inactiveColor;

  BottomNavItem({
    required this.icon,
    this.activeColor = Colors.blue,
    this.inactiveColor = Colors.grey,
  });
}