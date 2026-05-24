from flask import Flask, request, jsonify, render_template
app = Flask(__name__)
estudiantes = [
    {"id": 1, "nombre": "Juan", "apellido": "Perez", "nota": 85},
    {"id": 2, "nombre": "Maria", "apellido": "Garcia", "nota": 92},
    {"id": 3, "nombre": "Carlos", "apellido": "Lopez", "nota": 78},
    {"id": 4, "nombre": "Ana", "apellido": "Martinez", "nota": 88},
    {"id": 5, "nombre": "Pedro", "apellido": "Gonzalez", "nota": 91},
    {"id": 6, "nombre": "Laura", "apellido": "Rodriguez", "nota": 87},
    {"id": 7, "nombre": "Miguel", "apellido": "Sanchez", "nota": 79},
    {"id": 8, "nombre": "Sofia", "apellido": "Hernandez", "nota": 93},
    {"id": 9, "nombre": "Diego", "apellido": "Torres", "nota": 84},
    {"id": 10, "nombre": "Patricia", "apellido": "Vargas", "nota": 89},
    {"id": 11, "nombre": "Luis", "apellido": "Dominguez", "nota": 82},
    {"id": 12, "nombre": "Elena", "apellido": "Castro", "nota": 90},
    {"id": 13, "nombre": "Roberto", "apellido": "Morales", "nota": 77},
    {"id": 14, "nombre": "Isabel", "apellido": "Reyes", "nota": 86},
    {"id": 15, "nombre": "Antonio", "apellido": "Medina", "nota": 80},
    {"id": 16, "nombre": "Rosa", "apellido": "Ortiz", "nota": 94},
    {"id": 17, "nombre": "Francisco", "apellido": "Gutierrez", "nota": 75},
    {"id": 18, "nombre": "Carmen", "apellido": "Rojas", "nota": 88},
    {"id": 19, "nombre": "Manuel", "apellido": "Fuentes", "nota": 81},
    {"id": 20, "nombre": "Francisca", "apellido": "Munoz", "nota": 95}
]

@app.route('/estudiantes', methods=['GET'])
def listar():
    return jsonify({
        "mensaje": "Estudiantes leídos exitosamente",
        "data": estudiantes
    }), 200

@app.route('/estudiantes', methods=['POST'])
def agregar():
    estudiantes.append(request.json)
    return jsonify({
        "mensaje": "Estudiante creado exitosamente",
        "data": request.json
    }), 201

@app.route('/estudiantes/<int:i>', methods=['PUT'])
def actualizar(i):
    if i < len(estudiantes):
        estudiantes[i] = request.json
        return jsonify({
            "mensaje": "Estudiante actualizado exitosamente",
            "data": request.json
        }), 200
    else:
        return jsonify({
            "mensaje": "Estudiante no encontrado"
        }), 404

@app.route('/estudiantes/<int:i>', methods=['DELETE'])
def eliminar(i):
    if i < len(estudiantes):
        estudiante = estudiantes.pop(i)
        return jsonify({
            "mensaje": "Estudiante eliminado exitosamente",
            "data": estudiante
        }), 200
    else:
        return jsonify({
            "mensaje": "Estudiante no encontrado"
        }), 404

@app.route('/')
def inicio():
    return render_template('index.html')

if __name__ == '__main__':
    app.run()