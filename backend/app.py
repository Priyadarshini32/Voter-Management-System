from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS

# Initialize Flask and SQLAlchemy
app = Flask(__name__)
CORS(app)
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://postgres:priya32@localhost:5432/voters_db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# Abstract class for filtering logic
class FilterCriteria:
    def apply_filter(self, voters):
        raise NotImplementedError("Subclasses should implement this!")

# Concrete class for age-based filtering
class AgeFilter(FilterCriteria):
    def __init__(self, min_age):
        self.min_age = min_age

    def apply_filter(self, voters):
        return [v for v in voters if v.age >= self.min_age]

# Concrete class for registered status-based filtering
class RegisteredFilter(FilterCriteria):
    def __init__(self, registered):
        self.registered = registered

    def apply_filter(self, voters):
        return [v for v in voters if v.registered == self.registered]

class Voter(db.Model):
    __tablename__ = 'voters'

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(256), nullable=False)
    age = db.Column(db.Integer, nullable=False)
    registered = db.Column(db.Boolean, nullable=False, default=False)

    def __repr__(self):
        return f"<Voter(id={self.id}, name={self.name}, age={self.age}, registered={self.registered})>"

# Create the database tables when the app starts
with app.app_context():
    db.create_all()

# API to register a voter
@app.route('/register_voter', methods=['POST'])
def register_voter():
    try:
        data = request.get_json()
        if not all(k in data for k in ("name", "age", "registered")):
            return jsonify({"error": "Missing required fields"}), 400

        new_voter = Voter(name=data['name'], age=data['age'], registered=data['registered'])
        db.session.add(new_voter)
        db.session.commit()

        return jsonify({"message": "Voter registered successfully!", "id": new_voter.id}), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500

# API to get all voters
@app.route('/get_voters', methods=['GET'])
def get_voters():
    voters = Voter.query.all()
    voters_list = [{"id": v.id, "name": v.name, "age": v.age, "registered": v.registered} for v in voters]
    return jsonify(voters_list)

@app.route('/filter_voters', methods=['GET'])
def filter_voters():
    try:
        # Get query parameters
        min_age = request.args.get('min_age', type=int)
        max_age = request.args.get('max_age', type=int)
        registered = request.args.get('registered', type=str)

        # Start with a base query
        query = Voter.query

        # Apply age filtering correctly
        if min_age is not None and max_age is not None:
            query = query.filter(Voter.age.between(min_age, max_age))
        elif min_age is not None:
            query = query.filter(Voter.age >= min_age)
        elif max_age is not None:
            query = query.filter(Voter.age <= max_age)

        # Apply registered filtering properly
        if registered is not None:
            if registered.lower() == "true":
                query = query.filter(Voter.registered == True)
            elif registered.lower() == "false":
                query = query.filter(Voter.registered == False)

        # Fetch filtered voters
        voters = query.all()
        filtered_voters = [{"id": v.id, "name": v.name, "age": v.age, "registered": v.registered} for v in voters]

        return jsonify(filtered_voters)

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(debug=True)
