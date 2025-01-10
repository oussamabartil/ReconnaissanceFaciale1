from flask import Flask, request, jsonify
import face_recognition
import numpy as np
import cv2
import os
import sqlite3

app = Flask(__name__)

# Load the trained face recognition model
MODEL_PATH = "face_recognition_model.npy"

# Load the model if it exists, otherwise initialize empty encodings and labels
if os.path.exists(MODEL_PATH):
    try:
        model_data = np.load(MODEL_PATH, allow_pickle=True).item()
        encodings = model_data.get("encodings", [])
        labels = model_data.get("labels", [])
        print(f"Model loaded successfully from {MODEL_PATH}")
    except Exception as e:
        print(f"Error loading model: {e}")
        encodings = []
        labels = []
else:
    print(f"Model file not found: {MODEL_PATH}")
    encodings = []
    labels = []

# Load all user details into an array
user_details_list = []

def load_user_details():
    global user_details_list
    try:
        conn = sqlite3.connect("gestion_access.db")  # Ensure this path is correct
        cursor = conn.cursor()
        cursor.execute("SELECT id, name, email FROM Users")
        user_details_list = cursor.fetchall()
        conn.close()
        print(f"Debug: Loaded user details: {user_details_list}")
    except Exception as e:
        print(f"Debug: Error loading user details: {e}")
        user_details_list = []

# Load user details on startup
load_user_details()

def get_user_details_from_array(user_id):
    for user in user_details_list:
        if user[0] == user_id:
            return {"name": user[1], "email": user[2]}
    return None

@app.route("/recognize", methods=["POST"])
def recognize():
    try:
        # Debug: Check if frame is in the request
        if "frame" not in request.files:
            print("Debug: No 'frame' found in request files.")
            return jsonify({"message": "No frame uploaded", "recognized": False}), 400

        # Debug: Read and process the uploaded image
        file = request.files["frame"]
        file_data = file.read()
        print("Debug: Received image file of size:", len(file_data))
        
        image_array = np.frombuffer(file_data, np.uint8)
        input_image = cv2.imdecode(image_array, cv2.IMREAD_COLOR)

        # Debug: Check the shape of the input image
        if input_image is None:
            print("Debug: Failed to decode the input image.")
            return jsonify({"message": "Invalid image format", "recognized": False}), 400
        print("Debug: Input image shape:", input_image.shape)

        # Detect faces and generate encodings
        face_locations = face_recognition.face_locations(input_image)
        face_encodings = face_recognition.face_encodings(input_image, face_locations)
        print(f"Debug: Detected {len(face_encodings)} faces.")

        # Handle case where no faces are detected
        if not face_encodings:
            print("Debug: No faces detected in the uploaded image.")
            return jsonify({"message": "No faces detected", "recognized": False})

        results = []
        for input_encoding in face_encodings:
            # Debug: Compare faces
            matches = face_recognition.compare_faces(encodings, input_encoding)
            distances = face_recognition.face_distance(encodings, input_encoding)
            print("Debug: Match distances:", distances)

            if True in matches:
                best_match_index = np.argmin(distances)
                user_id = labels[best_match_index]
                print(f"Debug: Match found - User ID: {user_id}, Distance: {distances[best_match_index]}")

                # Fetch user details from preloaded data
                user_details = get_user_details_from_array(user_id)
                if user_details:
                    print(f"Debug: User details - Name: {user_details['name']}, Email: {user_details['email']}")
                    results.append({
                        "name": user_details["name"],
                        "email": user_details["email"],
                        "distance": distances[best_match_index]
                    })
                else:
                    print("Debug: No user details found for User ID:", user_id)
                    results.append({"name": "Unknown", "email": "Unknown", "distance": distances[best_match_index]})

            else:
                print("Debug: No matches found for this face.")
                results.append({"name": "Unknown", "email": "Unknown", "distance": None})

        # Debug: Final results
        print("Debug: Recognition results:", results)
        return jsonify({"message": "Recognition completed", "recognized": True, "results": results})

    except Exception as e:
        print("Debug: An error occurred during recognition:", str(e))
        return jsonify({"message": "Internal server error", "recognized": False}), 500

if __name__ == "__main__":
    # Run the Flask server
    app.run(host="0.0.0.0", port=5000, debug=True)
