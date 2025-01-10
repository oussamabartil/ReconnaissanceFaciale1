import face_recognition
import numpy as np
import sys

def recognize_face(input_image_path, model_path="face_recognition_model.npy"):
    # Load the model
    try:
        model_data = np.load(model_path, allow_pickle=True).item()
    except FileNotFoundError:
        print(f"Model file '{model_path}' not found. Please train the model first.")
        sys.exit(1)

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

if __name__ == "__main__":
    # Get image path from command line arguments
    if len(sys.argv) < 2:
        print("Usage: python test_image.py <path_to_test_image>")
        sys.exit(1)

    input_image_path = sys.argv[1]
    model_path = "face_recognition_model.npy"  # Path to the saved model

    recognize_face(input_image_path, model_path)
