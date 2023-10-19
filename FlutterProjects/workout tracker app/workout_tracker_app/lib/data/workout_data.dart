import 'package:flutter/material.dart';
import 'package:workout_tracker_app/models/exercise.dart';
import 'package:workout_tracker_app/models/workout.dart';

class WorkoutData extends ChangeNotifier {
  List<Workout> workoutList = [
    Workout(
      name: "Upper Body",
      exercises: [
        Exercise(name: "Bicep Curls", weight: "10", reps: "10", sets: "3"),
      ],
    ),
    Workout(
      name: "Lower Body",
      exercises: [
        Exercise(name: "Squats", weight: "10", reps: "10", sets: "3"),
      ],
    ),
  ];

  //get the list of workouts
  List<Workout> getWorkoutList() {
    return workoutList;
  }

  //get length of a given workout
  int numberOfExcercisesInWorkout(String workoutName) {
    Workout relevantWorkout = getRelevantWorkout(workoutName);

    return relevantWorkout.exercises.length;
  }

  //add a workout
  void addWorkout(String name) {
    //add a new workout with a blank list of excercises
    workoutList.add(Workout(name: name, exercises: []));

    notifyListeners();
  }

  //add an excercise to a workout
  void addExcercise(String workoutName, String exerciseName, String weight,
      String reps, String sets) {
    //find the relevant workout
    Workout relevantWorkout = getRelevantWorkout(workoutName);

    relevantWorkout.exercises.add(
      Exercise(
        name: exerciseName, 
        weight: weight, 
        reps: reps, 
        sets: sets
      ),
    );

    notifyListeners();
  }

  //check off excercise
  void checkOffExercise(String workoutName, String exerciseName) {
    //find the relevant workout and relevant exercise in that workout
    Exercise relevantExercise = getRelevantExercise(workoutName, exerciseName);

    //check off boolean to show user compelted the exercise
    relevantExercise.isCompleted = !relevantExercise.isCompleted;

    notifyListeners();
  }

  //return relevant workout object, given a workout name
  Workout getRelevantWorkout(String workoutName) {
    Workout relevantWorkout =
        workoutList.firstWhere((workout) => workout.name == workoutName);

    return relevantWorkout;
  }

  //return relevant exercise object, given a workout name + exercise name
  Exercise getRelevantExercise(String workoutName, String exerciseName) {
    //find relevant workout first
    Workout relevantWorkout = getRelevantWorkout(workoutName);

    //then find the relevant exercise in that workout
    Exercise relevantExercise = relevantWorkout.exercises
        .firstWhere((exercise) => exercise.name == exerciseName);
    return relevantExercise;
  }
}
