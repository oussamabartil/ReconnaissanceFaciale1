import sqlite3
import cv2
import face_recognition
import numpy as np
import os

# Load user data from SQLite database
def load_user_data(database_path):
    conn = sqlite3.connect(database_path)
    cursor = conn.cursor()
    cursor.execute("SELECT id, name, image_path FROM users")
    users = cursor.fetchall()
    conn.close()
    return users

# Generate face encodings for all user images
def generate_encodings(users):
    encodings = []
    labels = []
    
    for user_id, name, image_paths in users:
        # Split image paths by commas
        paths = image_paths.split(',')
        
        for path in paths:
            path = path.strip()  # Remove any whitespace
            if not os.path.exists(path):
                print(f"Image not found: {path}")
                continue
            
            # Load image
            image = face_recognition.load_image_file(path)
            
            # Detect face and generate encoding
            face_locations = face_recognition.face_locations(image)
            face_encodings = face_recognition.face_encodings(image, face_locations)
            
            if len(face_encodings) > 0:
                encodings.append(face_encodings[0])  # Use the first face encoding
                labels.append(user_id)
            else:
                print(f"No face found in image: {path}")
    
    return np.array(encodings), np.array(labels)

# Train and save the model
def train_model(encodings, labels, model_path="face_recognition_model.npy"):
    model_data = {"encodings": encodings, "labels": labels}
    np.save(model_path, model_data)  # Save the encodings and labels
    print(f"Model saved to {model_path}")

# Recognize a face from an input image
def recognize_face(input_image_path, model_path="face_recognition_model.npy"):
    # Load the model
    model_data = np.load(model_path, allow_pickle=True).item()
    encodings = model_data["encodings"]
    labels = model_data["labels"]

    # Load input image
    input_image = face_recognition.load_image_file(input_image_path)
    input_face_locations = face_recognition.face_locations(input_image)
    input_face_encodings = face_recognition.face_encodings(input_image, input_face_locations)

    if len(input_face_encodings) == 0:
        print("No faces detected in the input image.")
        return

    for input_encoding in input_face_encodings:
        # Compare the input face with known encodings
        matches = face_recognition.compare_faces(encodings, input_encoding)
        distances = face_recognition.face_distance(encodings, input_encoding)
        
        if True in matches:
            best_match_index = np.argmin(distances)
            user_id = labels[best_match_index]
            print(f"Face recognized: User ID {user_id}")
        else:
            print("Face not recognized.")
