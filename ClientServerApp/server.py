from flask import Flask, render_template_string

app = Flask(__name__)

# HTML template with CSS for styling
HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Menu Server</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333;
            text-align: center;
            padding: 50px;
        }
        h1 {
            color: #4CAF50;
        }
        .message {
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 20px;
            margin: 20px auto;
            width: 80%;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        .footer {
            margin-top: 30px;
            font-size: 0.9em;
            color: #777;
        }
    </style>
</head>
<body>
    <h1>Welcome to Wireless networks and Mobile computing !</h1>
    <div class="message">
        <p>{{ message }}</p>
    </div>
    <div class="footer">
        <p>Server running on port 8080</p>
    </div>
</body>
</html>
"""

@app.route('/message')  # Endpoint your J2ME client will call
def get_message():
    return render_template_string(HTML_TEMPLATE, message="Hello from J2ME Server!")

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)  # Accessible via http://localhost:8080/message
