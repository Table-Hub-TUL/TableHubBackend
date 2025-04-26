import stomp
import json

class MyListener(stomp.ConnectionListener):
    def on_connected(self, headers, body):
        print('Connected to server!')

    def on_message(self, frame):
        print('Received message:', frame.body)

    def on_error(self, frame):
        print('Received error:', frame.body)

conn = stomp.Connection([('localhost', 8080)])
conn.set_listener('', MyListener())

conn.connect(login='alice', passcode='hunter2', wait=True)

# Example: Send a message
login_request = {
    "header": {
        "messageId": 1,
        "correlationId": 1,
        "sender": "client",
        "type": "LOGIN_REQUEST",
        "accessToken": "abc123",
        "timestamp": 1234567890
    },
    "body": {
        "username": "alice",
        "password": "hunter2"
    }
}
conn.send(destination="/app/login", body=json.dumps(login_request))

# Subscribe to a topic if needed
conn.subscribe(destination="/topic/responses", id=1, ack='auto')

input("Press Enter to exit...\n")
conn.disconnect()
