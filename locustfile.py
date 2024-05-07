from locust import HttpUser, task, between
import string
import random

class HelloWorldUser(HttpUser):
    wait_time = between(1,1)

    @task
    def hello_world(self):
        length = random.randint(5000, 9000)
        header_val = ''.join(random.choices(string.ascii_letters + string.digits, k=length))
        # header_val = "some-val"
        self.client.get("/", verify=False, headers={"x-long-header": header_val})