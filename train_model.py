from face_recognition_model import load_user_data, generate_encodings, train_model

# Load users from SQLite database
database_path = "gestion_access.db"
users = load_user_data(database_path)

# Generate encodings
encodings, labels = generate_encodings(users)

# Train and save the model
train_model(encodings, labels)