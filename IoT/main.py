###########################################
##                                       ## 
##          MQTT TOPIC SUBSCRIBER        ##
##                                       ##
## The following code will be written in ##
## english because this is my code and   ##
## I can comment it in whatever language ##
##          you can understand.          ##
##                                       ##
##  Main author : ESTIENNE Alban-Moussa  ##
##  This code reads the config.ini file  ##
##  to know what to connect to, and what ##
##           port to log into.           ##
## Add a list of topic you want to keep  ##
##               track of.               ##
##   Add alert threshold values if you   ##
##      want to log unusual activity.    ##
## Overall,just follow the provided .ini ##
##     and you should be good to go.     ##
##                                       ##
###########################################

import sys, subprocess
import json as json
import signal
import pprint
import datetime
import time
import configparser
import threading

# Handles the installation of the necessary packages
# Install the given argument
# Asks permission before installing and run the according command based on the running OS
# package IN : the package to install
def install(package):
    if ("linux" in sys.platform):
        import os
        os.system('python3 -m pip install --upgrade paho-mqtt')
    else:
        subprocess.check_call([sys.executable, "-m", "pip", "install", package])

# Checks if paho-mqtt package is installed. If it isn't, tries to install it before running the program
try:
    import paho.mqtt.client as mqtt
except ImportError or ModuleNotFoundError:
    print("paho-mqtt does not exist. Would you like to install it? (y/n)")
    c = input()
    if c == "y":
        install("paho-mqtt")
        import paho.mqtt.client as mqtt
    else:
        print("Terminating program due to unmet requirements: 1 (one) or more libraries are missing.")
        print("To install library : ")
        print("Open a console terminal running python ($python3)")
        print("Run the command : pip install paho-mqtt")
        print("Try to run the program back.")
        sys.exit(1)


# --------------------------------------------------------------------------- #

######################
# JSON Buffer Reader #
######################

input_data = open("data.json")
input_alerts = open("alerts.json")

print(input_data.read())
print(input_alerts.read())

##############################
## Dictionnaries and arrays ##
##############################

data = {}
fixed_data = {} if input_data.read() == "" or input_data.read() == "{}" else json.load(input_data)
alert_threshold = {}
temp_alert_data = {}
fixed_alert_data = {} if input_alerts.read() == "" or input_data.read() == "{}" else json.load(input_alerts)
debounce = {}
topic_list = {}

################
## Parameters ##
################

server: str = ""
port: int = 0
keepalive: int = 0
topic: str = ""
step: int = 1


def config_loader():

    global server
    global port
    global keepalive
    global topic
    global step
    global alert_threshold
    global topic_list

    config = configparser.ConfigParser()
    config.read("config.ini")

    server = config.get("Connection Infos", "host")
    port = int(config.get("Connection Infos", "port"))
    keepalive = int(config.get("Connection Infos", "keepalive"))

    topic = config.get("Topics", "topics")

    step = int(config.get("Data treatment", "step"))
    
    topic_list = topic.split("\n")
    topic_list.pop(0) # First one is an empty str.

    epoch = time.time()

    for t in topic_list:
        debounce[t] = epoch
    
    alerts = config.get("Data treatment", "alerts")

    for alert in alerts.split("\n"):
        if alert != "":
            info = alert.split(":")
            alert_threshold[info[0]] = int(info[1])

config_loader()

# --- Save scheduler --- #

# Define the saving process.
# It first unload the content of the messages received during the `step` minutes interval
# into the global and fixed variables
# Calls the file saver to export the data
# Calls another threading process to schedule another loop.
#
# For compatibility reasons, signal.signal(SIGALRM) will not be used, as it prevents the program from running in Windows OS

def saving_interval():

    print("Entering saving process...")

    global data
    global fixed_data
    global temp_alert_data
    global fixed_alert_data

    print(data)
    print("####################################################")
    print(temp_alert_data)

    # Unload temp content into global content
    fixed_data.update(data)
    fixed_alert_data.update(temp_alert_data)
    data.clear()
    temp_alert_data.clear()

    print("Saving to file...")

    save_to_file()
    t = threading.Timer(step * 60, saving_interval)
    t.start()

    print("New loop initialized")

t = threading.Timer(step * 60, saving_interval)
t.start()

# Saves the data dictionnaries to json format
# This function gets called every `step` minutes, as defined in config.ini
def save_to_file():

    global fixed_data
    global fixed_alert_data

    with open("alerts.json", "w") as savefile:
        json.dump(fixed_alert_data, savefile)

    with open("data.json", "w") as savefile:
        json.dump(fixed_data, savefile)

    print("Successfully exported data to file.")
    print("Successfully exported alerts to file.")



# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, reason_code, properties):

    global topic
    global topic_list

    print(f"Connected with result code {reason_code}")
    print(f"List of topics to subscribe to : {topic}")    
    
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    for t in topic_list:
        print(f"Subscribing to topic {t}")
        client.subscribe(t)



def on_subscribe(client, userdata, mid, reason_code_list, properties):
    if reason_code_list[0].is_failure:
        print(f"Broker rejected you subscription: {reason_code_list[0]}")
    else:
        print(f"Broker granted the following QoS: {reason_code_list[0].value}")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    
    file = json.loads(msg.payload)

    if "AM107/by-room" in msg.topic:

        # Check "Triphaso" for explanation. Same as this.

        room_name = file[1]["room"]
        device_name = file[1]["deviceName"]

        if room_name not in data:
            data[room_name] = {}

        if device_name not in data[room_name]:
            data[room_name][device_name] = {}

        for key in file[0].keys():
            if key in ("co2", "humidity", "temperature"):
                data[file[1]["room"]][file[1]["deviceName"]][key] = file[0][key]

                if key in alert_threshold:
                    if int(file[0][key]) > alert_threshold[key]:
                        if room_name not in temp_alert_data:
                            temp_alert_data[room_name] = []
                        if device_name not in temp_alert_data[room_name]:
                            temp_alert_data[room_name][device_name] = []
                        temp_alert_data[room_name][device_name].append([
                            str(datetime.datetime.now()), 
                            key, 
                            file[0][key],
                        ])


        data[file[1]["room"]][file[1]["deviceName"]]["time"] = str(datetime.datetime.now())
        

    elif "Triphaso" in msg.topic:

        # Retrieves the room_name and device_name

        room_name = file[1]["Room"]
        device_name = file[1]["deviceName"]

        # Initializes keys in dictionary if the data isn't already present

        if room_name not in data:
            data[room_name] = {}

        if device_name not in data[room_name]:
            data[room_name][device_name] = {}  

        # For every data the PUBLISH has received      

        for key in file[0].keys():
            #Input the data in the dictionary
            this_data = file[0][key]
            data[file[1]["Room"]][file[1]["deviceName"]][key] = this_data

            # Launches the alert threshold checker

            if key in alert_threshold:
                if int(file[0][key]) > alert_threshold[key]:

                    # If data is over the set threshold, log it into a file (same process as above)

                    if room_name not in temp_alert_data:
                        temp_alert_data[room_name] = []
                    if device_name not in temp_alert_data[room_name]:
                        temp_alert_data[room_name][device_name] = []
                    temp_alert_data[room_name][device_name].append([
                        str(datetime.datetime.now()), 
                        key, 
                        file[0][key],
                    ])

        # After everything, log the date and hour

        data[file[1]["Room"]][file[1]["deviceName"]]["time"] = str(datetime.datetime.now())

    elif "solaredge" in msg.topic:

        if "Global" not in data:
            data["Global"] = {}
        
        data["Global"] = file
        

    pprint.pprint(data)



try:
    mqttc = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    mqttc.on_connect = on_connect
    mqttc.on_message = on_message
    mqttc.on_subscribe = on_subscribe

    mqttc.connect(server, int(port), int(keepalive))

    # Blocking call that processes network traffic, dispatches callbacks and
    # handles reconnecting.
    # Other loop*() functions are available that give a threaded interface and a
    # manual interface.
    mqttc.loop_forever()
except KeyboardInterrupt:
    t.cancel()