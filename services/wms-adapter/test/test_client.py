import sys
import types
import json
import unittest
from unittest.mock import MagicMock
from pathlib import Path
import importlib.util


REPO_ROOT = Path(__file__).resolve().parents[3]
CLIENT_PATH = REPO_ROOT / 'services' / 'wms-adapter' / 'app' / 'rabbitmq' / 'client.py'


def install_stub_pika(channel_mock: MagicMock, connection_mock: MagicMock):
    mod = types.ModuleType('pika')

    class BasicProperties:
        def __init__(self, delivery_mode=None, **kwargs):
            self.delivery_mode = delivery_mode

    class PlainCredentials:
        def __init__(self, username, password):
            self.username = username
            self.password = password

    class ConnectionParameters:
        def __init__(self, host, port, credentials):
            self.host = host
            self.port = port
            self.credentials = credentials

    def BlockingConnection(params):
        return connection_mock

    # Attach symbols
    mod.BasicProperties = BasicProperties
    mod.PlainCredentials = PlainCredentials
    mod.ConnectionParameters = ConnectionParameters
    mod.BlockingConnection = BlockingConnection

    sys.modules['pika'] = mod


def import_client_module():
    # Force-load the client module from file path
    if 'rabbitmq.client' in sys.modules:
        del sys.modules['rabbitmq.client']
    spec = importlib.util.spec_from_file_location('rabbitmq.client', str(CLIENT_PATH))
    module = importlib.util.module_from_spec(spec)
    loader = spec.loader
    assert loader is not None
    loader.exec_module(module)
    sys.modules['rabbitmq.client'] = module
    return module


class RabbitMQClientTests(unittest.TestCase):
    def test_publish_success(self):
        channel = MagicMock()
        channel.is_open = True
        connection = MagicMock()
        connection.is_open = True
        connection.channel.return_value = channel

        install_stub_pika(channel, connection)
        client_module = import_client_module()
        client = client_module.RabbitMQClient()

        ok = client.publish('test-queue', { 'a': 1 })

        self.assertTrue(ok)
        channel.queue_declare.assert_called_once()
        channel.basic_publish.assert_called_once()
        args, kwargs = channel.basic_publish.call_args
        self.assertEqual(kwargs.get('routing_key'), 'test-queue')
        body = kwargs.get('body')
        self.assertEqual(json.loads(body), { 'a': 1 })

    def test_consume_invokes_callback_and_acks(self):
        # Set up channel to capture callback
        channel = MagicMock()
        channel.is_open = True
        captured = {}

        def basic_consume(queue, on_message_callback, auto_ack=False):
            captured['cb'] = on_message_callback
            return 'ctag'

        def start_consuming_side_effect():
            # Simulate one delivery
            method = types.SimpleNamespace(delivery_tag=123)
            body = json.dumps({'x': 2}).encode()
            captured['cb'](channel, method, None, body)
            return None

        channel.basic_consume.side_effect = basic_consume
        channel.start_consuming.side_effect = start_consuming_side_effect

        connection = MagicMock()
        connection.is_open = True
        connection.channel.return_value = channel

        install_stub_pika(channel, connection)
        client_module = import_client_module()
        client = client_module.RabbitMQClient()

        received = []
        def cb(msg):
            received.append(msg)

        client.consume('orders', cb)

        self.assertEqual(received, [{'x': 2}])
        channel.basic_ack.assert_called_once()


if __name__ == '__main__':
    unittest.main()
